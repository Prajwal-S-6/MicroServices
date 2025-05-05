package com.pm.billing_service.billing_service.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.grpc.stub.StreamObserver;

@GrpcService
public class BillingGRPCService extends BillingServiceGrpc.BillingServiceImplBase {

    private static final Logger LOG = LoggerFactory.getLogger(BillingGRPCService.class);

    @Override
    public void createBillingAccount(BillingRequest request, StreamObserver<BillingResponse> billingResponse) {
        LOG.info("createBillingAccount request received {} from grpc client", request.getEmail());

        BillingResponse response = BillingResponse.newBuilder().setAccountId("123").setStatus("ACTIVE").build();

        billingResponse.onNext(response);
        billingResponse.onCompleted();
    }
}
