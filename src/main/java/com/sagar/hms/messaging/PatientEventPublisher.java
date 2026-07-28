package com.sagar.hms.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PatientEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.messaging.patient-queue:patient.notifications}")
    private String patientQueue;

    public void publishPatientCreated(String patientNumber) {
        rabbitTemplate.convertAndSend(patientQueue, "PATIENT_CREATED:" + patientNumber);
    }

    public void publishPatientDeleted(String patientNumber) {
        rabbitTemplate.convertAndSend(patientQueue, "PATIENT_DELETED:" + patientNumber);
    }
}
