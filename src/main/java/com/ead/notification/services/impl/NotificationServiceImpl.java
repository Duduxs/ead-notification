package com.ead.notification.services.impl;

import com.ead.notification.dtos.NotificationDTO;
import com.ead.notification.enums.NotificationStatus;
import com.ead.notification.models.NotificationModel;
import com.ead.notification.repositories.NotificationRepository;
import com.ead.notification.services.NotificationService;
import org.apache.catalina.connector.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import javax.ws.rs.WebApplicationException;
import java.util.UUID;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        repository = notificationRepository;
    }

    @Override
    public NotificationModel saveNotification(NotificationModel notificationModel) {
        return repository.save(notificationModel);
    }

    @Override
    public Page<NotificationModel> findAllByUser(UUID userId, Pageable pageable) {
        return repository.findAllByUserIdAndStatus(userId, NotificationStatus.CREATED, pageable);
    }

    @Override
    public NotificationModel updateStatusBy(UUID userId, UUID notificationId, NotificationDTO notificationDTO) {
        final var entity  = repository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new WebApplicationException(SC_NOT_FOUND));

        entity.setStatus(notificationDTO.getStatus());

        return repository.save(entity);
    }
}
