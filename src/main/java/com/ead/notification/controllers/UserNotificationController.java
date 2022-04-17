package com.ead.notification.controllers;

import com.ead.notification.dtos.NotificationDTO;
import com.ead.notification.models.NotificationModel;
import com.ead.notification.services.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

import static org.springframework.data.domain.Sort.Direction.ASC;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserNotificationController {

    private final NotificationService service;

    public UserNotificationController(final NotificationService service) {
        this.service = service;
    }

    @GetMapping("/users/{userId}/notifications")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<Page<NotificationModel>> findAllNotificationsByUser(
            @PathVariable UUID userId,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = ASC)
            Pageable pageable
    ) {

        return ResponseEntity.ok(service.findAllByUser(userId, pageable));

    }

    @PatchMapping("/users/{userId}/notifications/{notificationId}")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public ResponseEntity<NotificationModel> updateStatus(
            @PathVariable UUID userId,
            @PathVariable UUID notificationId,
            @RequestBody @Valid NotificationDTO notificationDTO
    ) {

        final var result = service.updateStatusBy(userId, notificationId, notificationDTO);

        return ResponseEntity.ok(result);
    }

}
