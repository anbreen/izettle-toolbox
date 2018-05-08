package model;

import com.amazonaws.AmazonServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import model.exception.GdprDataNotDeleteException;
import model.exception.S3FailedException;

abstract class GdprEventManager implements GdprEventHandler {

    private static final int DEFAULT_PAGING_SIZE = 100;
    private final S3TransferManager s3TransferManager;
    private final GdprPublisher gdprPublisher;
    private final List<GdprDao> grpdDaos;

    public GdprEventManager(S3TransferManager s3TransferManager, GdprPublisher gdprPublisher, GdprDao... daos) {
        this.s3TransferManager = s3TransferManager;
        this.gdprPublisher = gdprPublisher;
        grpdDaos = Arrays.asList(daos);
    }

    @Override
    public void handleForget(GdprEvent event) {
        grpdDaos.forEach(dao -> dao.deleteAll(event));
        try {
            assertDataIsDeleted(event);
            publish(GdprStatusReportMessage.ack(event));
        } catch (GdprDataNotDeleteException e) {
            publish(GdprStatusReportMessage.nack(event));
            throw e;
        }
    }

    @Override
    public void handleExport(GdprEvent event) {
        Paging paging = new Paging(DEFAULT_PAGING_SIZE);
        grpdDaos
            .forEach(dao -> {
                    AtomicInteger counter = new AtomicInteger();
                    Boolean dataExhaused;
                    do {
                        final PagingResult<?> dataChunk = dao.getAll(paging);
                        uploadToS3(event, dataChunk.getItems(), dao.getDescription() + counter.getAndIncrement());
                        dataChunk.getPagingState().ifPresent(paging::pagingState);
                        dataExhaused = !dataChunk.isFullyFetched();
                    } while (dataExhaused);
                }
            );

        publish(GdprStatusReportMessage.ack(event));
    }

    @Override
    public void uploadToS3(GdprEvent event, Object data, String sequenceIdentifier) {
        try {
            s3TransferManager.handleUpload(event.getBucket(), event.getPath() + sequenceIdentifier, data);
        } catch (InterruptedException | AmazonServiceException | UnsupportedEncodingException |
            JsonProcessingException e) {
            publish(GdprStatusReportMessage.nack(event));
            throw new S3FailedException(
                "Could not upload user data to S3 for gdprEvent=" + event, e);

        }
    }

    @Override
    public void publish(GdprStatusReportMessage message) {
        gdprPublisher.publish(message);
    }

    protected abstract void assertDataIsDeleted(GdprEvent event) throws GdprDataNotDeleteException;
}
