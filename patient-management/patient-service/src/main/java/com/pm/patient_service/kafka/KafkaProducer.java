package com.pm.patient_service.kafka;

import com.pm.patient_service.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.messages.PatientMessage;

@Service
public class KafkaProducer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaProducer.class);
    // Kafka message(event) key: string, value:byte[]
    private final KafkaTemplate<String, byte[]> kafkaMessage;

    public KafkaProducer(KafkaTemplate<String, byte[]> kafkaMessage) {
        this.kafkaMessage = kafkaMessage;
    }

    // send patient kafka message to kafka broker(topic)
    public void sendPatientMessage(Patient patient) {
        PatientMessage message = PatientMessage.newBuilder()
                .setPatientId(patient.getId().toString())
                .setFirstName(patient.getFirstName())
                .setLastName(patient.getLastName())
                .setEmail(patient.getEmail())
                .setMessageType("patient_created")
                .build();
        try {
            kafkaMessage.send("patient", message.toByteArray());
        } catch (Exception e) {
            LOG.error("Error while sending patient_created kafka message {}", message);
        }

    }
}
