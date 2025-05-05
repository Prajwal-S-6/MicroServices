package com.pm.patient_service.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGRPCClient {

    private static final Logger LOG = LoggerFactory.getLogger(BillingServiceGRPCClient.class);
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    public BillingServiceGRPCClient(@Value("${billing.service.address:localhost}") String serverAddress, @Value("${billing.service.grpc.port: 9001}") int serverPort) {
        LOG.info("Connecting to billing grpc service at {} {}", serverAddress, serverPort);

        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(serverAddress, serverPort).usePlaintext().build();
        blockingStub = BillingServiceGrpc.newBlockingStub(managedChannel);
    }

    public BillingResponse createBillingAccount(String patientId, String firstName, String lastName, String email) {
        BillingRequest request = BillingRequest.newBuilder()
                .setPatientId(patientId)
                .setFirstName(firstName)
                .setLastName(lastName)
                .setEmail(email)
                .build();
        BillingResponse response = blockingStub.createBillingAccount(request);
        LOG.info("Received response from Billing grpc service {}", response);
        return response;
    }
}
