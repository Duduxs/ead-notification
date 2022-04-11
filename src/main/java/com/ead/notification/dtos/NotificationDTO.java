package com.ead.notification.dtos;

import com.ead.notification.enums.NotificationStatus;

import javax.validation.constraints.NotNull;

public class NotificationDTO {

    @NotNull
    private NotificationStatus status;

    public NotificationStatus getStatus() {
        return status;
    }

    public void setStatus(NotificationStatus status) {
        this.status = status;
    }
}
