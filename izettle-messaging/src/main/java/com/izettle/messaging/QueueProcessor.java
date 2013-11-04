package com.izettle.messaging;

import static com.izettle.java.ValueChecks.empty;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.izettle.messaging.handler.MessageHandler;
import com.izettle.messaging.handler.MessageHandlerForSingleMessageType;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a poller on a single queue. All messages that get received on the queue
 * will be passed on to the supplied messagehandler. If the messagehandler does not throw any
 * exceptions, the message will also be deleted from the queue. Otherwise (if an exception is
 * thrown), the message will remain on the queue, and will most likely be processed again.
 */
public class QueueProcessor {

	private static final Logger LOG = LoggerFactory.getLogger(QueueProcessor.class);
	private final String name;

	private static final int MAXIMUM_NUMBER_OF_MESSAGES_TO_RECEIVE = 10;
	private static final int MESSAGE_WAIT_SECONDS = 20;
	private final String queueUrl;
	private final AmazonSQS amazonSQS;
	private final MessageHandler<Message> messageHandler;

	public static QueueProcessor createQueueProcessor(
			AmazonSQS amazonSQS,
			String name,
			String queueUrl,
			MessageHandler<Message> messageHandler) {
		return new QueueProcessor(name,
				queueUrl,
				amazonSQS,
				messageHandler);
	}

	public static <M> QueueProcessor createQueueProcessor(
			AmazonSQS amazonSQS,
			Class<M> classType,
			String name,
			String queueUrl,
			MessageHandler<M> messageHandler) {
		return new QueueProcessor(name,
				queueUrl,
				amazonSQS,
				new MessageHandlerForSingleMessageType<>(classType, messageHandler));
	}

	QueueProcessor(
			String name,
			String queueUrl,
			AmazonSQS amazonSQS,
			MessageHandler<Message> messageHandler) {
		this.name = name;
		this.queueUrl = queueUrl;
		this.amazonSQS = amazonSQS;
		this.messageHandler = messageHandler;
	}

	public String getName() {
		return name;
	}

	public void poll() throws MessagingException {
		ReceiveMessageRequest messageRequest = new ReceiveMessageRequest(queueUrl);
		messageRequest.setMaxNumberOfMessages(MAXIMUM_NUMBER_OF_MESSAGES_TO_RECEIVE);
		messageRequest.setWaitTimeSeconds(MESSAGE_WAIT_SECONDS);
		List<Message> messages;

		try {
			messages = amazonSQS.receiveMessage(messageRequest).getMessages();
		} catch (AmazonClientException e) {
			throw new MessagingException("Failed to poll message queue.", e);
		}

		if (!empty(messages)) {
			handleMessages(messages);
		}
	}

	private void handleMessages(List<Message> messages) {
		LOG.info("Message queue processor {} fetched {} message(s) from queue.", name, messages.size());

		for (Message message : messages) {
			try {
				messageHandler.handle(message);
				delete(message.getReceiptHandle());
			} catch (Exception e) {
				/*
				 Note: We should only log here and continue with the other messages fetched. The reason for that is
				 that we can during release have different versions of the messages, some possible to parse and some
				 not.
				 */
				LOG.warn("Failed to handle message {} from queue.", message.getMessageId(), e);
			}
		}
	}

	private void delete(String messageReceiptHandle) throws MessagingException {
		try {
			amazonSQS.deleteMessage(new DeleteMessageRequest(queueUrl, messageReceiptHandle));
		} catch (AmazonClientException ase) {
			throw new MessagingException("Failed to delete message with receipt handle " + messageReceiptHandle, ase);
		}
	}
}
