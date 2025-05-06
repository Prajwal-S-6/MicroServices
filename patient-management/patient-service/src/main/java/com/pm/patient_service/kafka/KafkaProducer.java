package com.pm.patient_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    // Kafka message(event) key: string, value:byte[]
    private final KafkaTemplate<String, byte[]> kafkaMessage;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaMessage) {
        this.kafkaMessage = kafkaMessage;
    }
}
