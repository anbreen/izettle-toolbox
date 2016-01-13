package com.izettle.messaging;

import static java.lang.Thread.sleep;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a runnable that keeps on polling a message queue until explicitly stopped.
 */
public class QueueProcessingRunnable implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(QueueProcessingRunnable.class);
    private final String name;
    private final MessageQueueProcessor queueProcessor;
    private final TimeUnit sleepDurationTimeUnit;
    private final long sleepDuration;
    private volatile boolean alive;
    private volatile Thread executingThread;

    public QueueProcessingRunnable(MessageQueueProcessor queueProcessor, long sleepDuration, TimeUnit sleepDurationTimeUnit) {
        this.name = queueProcessor.getName();
        this.queueProcessor = queueProcessor;
        this.sleepDuration = sleepDuration;
        this.sleepDurationTimeUnit = sleepDurationTimeUnit;
    }

    @Override
    public void run() {

        LOG.info(String.format("Message queue processor %s started.", name));
        executingThread = Thread.currentThread();
        alive = true;

        while (isAlive()) {
            try {
                queueProcessor.poll();
            } catch (MessagingException e) {
                LOG.error(String.format("Message queue processor %s failed to poll for new messages.", name), e);
                if (!isAlive()) {
                    break;
                }
                try {
                    sleep(sleepDurationTimeUnit.toMillis(sleepDuration));
                } catch (InterruptedException e1) {
                    break;
                }
            }
        }
        alive = false;
        executingThread = null;

        LOG.info(String.format("Message queue processor %s stopped.", name));
    }

    private boolean isAlive() {
        if (!alive) {
            return false;
        }
        final Thread thread = this.executingThread;
        if (thread != null && thread.isInterrupted()) {
            return false;
        }
        return true;
    }

    /**
     * Makes the polling loop stop some time in the future.
     */
    public void shutdown() {
        alive = false;
        final Thread thread = this.executingThread;
        if (thread != null) {
            thread.interrupt();
        }
    }
}
