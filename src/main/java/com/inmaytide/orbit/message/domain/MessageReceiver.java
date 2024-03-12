package com.inmaytide.orbit.message.domain;

import com.inmaytide.orbit.commons.constants.MessageReadingStatus;
import com.inmaytide.orbit.commons.constants.MessageSendingMode;
import com.inmaytide.orbit.commons.constants.MessageSendingStatus;
import com.inmaytide.orbit.commons.domain.pattern.Entity;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * @author inmaytide
 * @since 2024/2/28
 */
public class MessageReceiver extends Entity {

    private Long messageId;

    private Long receiver;

    private MessageSendingMode sendingMode;

    private MessageSendingStatus sendingStatus;

    private Instant sendingStatusTime;

    private MessageReadingStatus readingStatus;

    private Instant readingStatusTime;

    private BigDecimal failedCount;

    private String failedReason;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getReceiver() {
        return receiver;
    }

    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }

    public MessageSendingMode getSendingMode() {
        return sendingMode;
    }

    public void setSendingMode(MessageSendingMode sendingMode) {
        this.sendingMode = sendingMode;
    }

    public MessageSendingStatus getSendingStatus() {
        return sendingStatus;
    }

    public void setSendingStatus(MessageSendingStatus sendingStatus) {
        this.sendingStatus = sendingStatus;
    }

    public Instant getSendingStatusTime() {
        return sendingStatusTime;
    }

    public void setSendingStatusTime(Instant sendingStatusTime) {
        this.sendingStatusTime = sendingStatusTime;
    }

    public MessageReadingStatus getReadingStatus() {
        return readingStatus;
    }

    public void setReadingStatus(MessageReadingStatus readingStatus) {
        this.readingStatus = readingStatus;
    }

    public Instant getReadingStatusTime() {
        return readingStatusTime;
    }

    public void setReadingStatusTime(Instant readingStatusTime) {
        this.readingStatusTime = readingStatusTime;
    }

    public BigDecimal getFailedCount() {
        if (failedCount == null) {
            failedCount = BigDecimal.ZERO;
        }
        return failedCount;
    }

    public void setFailedCount(BigDecimal failedCount) {
        this.failedCount = failedCount;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }
}
