package com.pm.patient_service.service;

import com.pm.patient_service.dto.PatientRequestDTO;
import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.exceptions.EmailAlreadyExistsException;
import com.pm.patient_service.exceptions.PatientNotFoundException;
import com.pm.patient_service.grpc.BillingServiceGRPCClient;
import com.pm.patient_service.kafka.KafkaProducer;
import com.pm.patient_service.model.Patient;
import com.pm.patient_service.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private static final Logger LOG = LoggerFactory.getLogger(PatientService.class);
    //constructor-based DI (or can use @Autowired)
    private final PatientRepository patientRepository;

    private final BillingServiceGRPCClient billingServiceGRPCClient;

    private final KafkaProducer kafkaProducer;

    public PatientService(PatientRepository patientRepository, BillingServiceGRPCClient billingServiceGRPCClient, KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGRPCClient = billingServiceGRPCClient;
        this.kafkaProducer = kafkaProducer;
    }

    public static PatientResponseDTO toResponseDTO(Patient patient) {
        return new PatientResponseDTO(patient.getId().toString(), patient.getFirstName(), patient.getLastName(), patient.getEmail(), patient.getAddress(), patient.getRegisteredDate().toString());
    }

    public static Patient toModel(PatientRequestDTO patientRequestDTO) {
        Patient patient = new Patient();
        patient.setFirstName(patientRequestDTO.firstName());
        patient.setLastName(patientRequestDTO.lastName());
        patient.setEmail(patientRequestDTO.email());
        patient.setAddress(patientRequestDTO.address());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.dateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDTO.registeredDate()));

        return patient;
    }

    public List<PatientResponseDTO> getAllPatients() {
        return patientRepository.findAll().stream().map(PatientService::toResponseDTO).toList();
    }

    public PatientResponseDTO addPatient(PatientRequestDTO patientRequestDTO) {
        if (patientRepository.existsByEmail(patientRequestDTO.email())) {
            throw new EmailAlreadyExistsException("A patient with this email already exists " + patientRequestDTO.email());
        }
        Patient patient = patientRepository.save(toModel(patientRequestDTO));
        LOG.info("Calling billing service createBillingAccount using gRPC");
        billingServiceGRPCClient.createBillingAccount(patient.getId().toString(), patient.getFirstName(),
                patient.getLastName(), patient.getEmail());
        kafkaProducer.sendPatientMessage(patient);
        return toResponseDTO(patient);

    }

    public PatientResponseDTO updatePatient(UUID id, PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id).orElseThrow(() -> new PatientNotFoundException("Patient doesnt exist with id: " + id));

        if (patientRepository.existsByEmailAndIdNot(patientRequestDTO.email(), id)) {
            throw new EmailAlreadyExistsException("A patient with this email already exists " + patientRequestDTO.email());
        }

        patient.setFirstName(patientRequestDTO.firstName());
        patient.setLastName(patientRequestDTO.lastName());
        patient.setEmail(patientRequestDTO.email());
        patient.setAddress(patientRequestDTO.address());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.dateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);

        return toResponseDTO(updatedPatient);

    }

    public void deletePatient(UUID id) {
        if (!patientRepository.existsById(id)) {
            throw new PatientNotFoundException("Patient doesnt exist with id: " + id);
        }
        patientRepository.deleteById(id);
    }


}
