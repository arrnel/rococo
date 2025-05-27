package org.rococo.logs.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.rococo.logs.data.log.LogEntity;
import org.rococo.logs.model.ServiceName;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LogConsumer {

    private final LogService logService;

    @KafkaListener(topics = {
            "rococo-artists",
            "rococo-auth",
            "rococo-countries",
            "rococo-files",
            "rococo-gateway",
            "rococo-museums",
            "rococo-paintings",
            "rococo-users"
    }, groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void consumeLogMessage(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
        LogEntity logEntity = LogEntity.builder()
                .serviceName(ServiceName.findByServiceName(record.topic()))
                .time(LocalDateTime.now())
                .message(record.value())
                .build();

        logService.save(logEntity);
        acknowledgment.acknowledge();
    }

}