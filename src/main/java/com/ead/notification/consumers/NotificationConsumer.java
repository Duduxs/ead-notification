package com.ead.notification.consumers;

import com.ead.notification.dtos.NotificationCommandDTO;
import com.ead.notification.models.NotificationModel;
import com.ead.notification.services.NotificationService;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.ead.notification.enums.NotificationStatus.CREATED;
import static org.springframework.amqp.core.ExchangeTypes.TOPIC;

@Component
public class NotificationConsumer {

    private final NotificationService service;

    public NotificationConsumer(final NotificationService service) {
        this.service = service;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${ead.broker.queue.notificationCommandQueue.name}"),
                    exchange = @Exchange(
                            value = "${ead.broker.exchange.notificationCommandExchange}",
                            type = TOPIC, ignoreDeclarationExceptions = "true"
                    ),
                    key = "${ead.broker.key.notificationCommandKey}"
            )
    )
    public void listen(@Payload NotificationCommandDTO dto) {

        var model = new NotificationModel();
        BeanUtils.copyProperties(dto, model);

        model.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        model.setStatus(CREATED);

        service.saveNotification(model);
    }

}
