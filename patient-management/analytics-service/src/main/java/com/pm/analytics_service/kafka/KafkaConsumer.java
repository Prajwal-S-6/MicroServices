package com.pm.analytics_service.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import patient.messages.PatientMessage;

@Service
public class KafkaConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "patient", groupId = "analytics-service")         // saying we are listening/consuming patient topic; groupId is for Kafka broker to know about the consumer
    public void consumePatientMessage(byte[] patientMessage) {
        try {
            PatientMessage message = PatientMessage.parseFrom(patientMessage);
            LOG.info("Successfully consumed patient message PatientId: {}, FirstName: {}, LastName: {}, Email: {}",
                    message.getPatientId(), message.getFirstName(), message.getLastName(), message.getEmail());
        } catch (InvalidProtocolBufferException e) {
            LOG.error("Error while consuming the patient kafka message");
        }
    }
}
