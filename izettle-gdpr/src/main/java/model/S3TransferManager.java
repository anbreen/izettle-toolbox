package model;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.izettle.java.DateFormatCreator;
import com.izettle.java.TimeZoneId;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

@Slf4j
public class S3TransferManager {

    private static final String MAX_AGE_SECONDS = "119957";

    private final TransferManager transferManager;
    private ObjectMapper objectMapper;

    public S3TransferManager(TransferManager transferManager, ObjectMapper objectMapper) {
        this.transferManager = transferManager;
        this.objectMapper = objectMapper;
    }

    public void handleUpload(String bucketName, String path, Object data)
        throws InterruptedException, AmazonServiceException, UnsupportedEncodingException, JsonProcessingException {
        final String dataString = objectMapper.writeValueAsString(data);
        final byte[] dataArray = dataString.getBytes("UTF-8");
        log.info("Uploading file to path=" + path);
        final Upload upload = transferManager.upload(
            bucketName,
            path,
            new ByteArrayInputStream(dataArray),
            getObjectMetadata(dataArray)
        );
        upload.waitForCompletion();
    }

    //TODO what should be here?
    protected ObjectMetadata getObjectMetadata(byte[] data) {
        Map<String, String> userMetadata = new HashMap<>();
        userMetadata.put("created", DateFormatCreator.createDateAndTimeFormatter(TimeZoneId.UTC).format(new Date()));
        ObjectMetadata metadata = new ObjectMetadata();
        // these are just http headers, not actual object TTL
        metadata.setExpirationTime(DateUtils.addMonths(new Date(), 3));
        metadata.setCacheControl("max-age=" + MAX_AGE_SECONDS + ",s-maxage=" + MAX_AGE_SECONDS + ",public");
        metadata.setContentType("application/octet-stream");
        metadata.setContentLength(data.length);

        metadata.setUserMetadata(userMetadata);

        return metadata;
    }
}
