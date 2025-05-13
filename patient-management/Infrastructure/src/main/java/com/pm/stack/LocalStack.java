package com.pm.stack;



import software.amazon.awscdk.*;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.Protocol;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.logs.RetentionDays;
import software.amazon.awscdk.services.msk.CfnCluster;
import software.amazon.awscdk.services.rds.*;
import software.amazon.awscdk.services.route53.CfnHealthCheck;

import java.util.*;
import java.util.stream.Collectors;

public class LocalStack extends Stack {

    private final Vpc vpc;

    private final Cluster ecsCluster;

    public LocalStack(final App scope, final String id, final StackProps props) {
        super(scope, id, props);

        // creating VPC
        this.vpc = createVpc();

        // creating DB using AWS RDS
        DatabaseInstance authServiceDb = createMySQLDatabase("AuthServiceDB", "auth-service-db");
        CfnHealthCheck authServiceDBHealthCheck = createDBHealthCheck(authServiceDb, "AuthServiceDBHealthCheck");
        DatabaseInstance patientServiceDb = createPostgresDatabase("PatientServiceDB", "patient-service-db");
        CfnHealthCheck patientServiceDbHealthCheck = createDBHealthCheck(patientServiceDb, "PatientServiceDBHealthCheck");

        // creating MSK cluster(Amazon managed stream for Apache Kafka)
        CfnCluster mskCluster = createMSKCluster();

        //creating ECS Cluster
        this.ecsCluster = createECSCluster();

        FargateService authService = createFargateService("AuthService",
                "auth-service-auth-service:latest",
                List.of(8085),
                authServiceDb,
                Map.of("JWT_SECRET", "wHdu3kUe8z1+aG/ZVY/JhEtfFZ1Zx4mWJysJ3xIepQo="));

        authService.getNode().addDependency(authServiceDb);
        authService.getNode().addDependency(authServiceDBHealthCheck);

        FargateService billingService = createFargateService("BillingService",
                "billing-service-billing-service:latest",
                List.of(8081, 9001),
                null,
                null);

        FargateService analyticsService = createFargateService("AnalyticsService",
                "analytics-service-analytics-service:latest",
                List.of(8082),
                null,
                null);

        analyticsService.getNode().addDependency(mskCluster);

        FargateService patientService = createFargateService("PatientService",
                "patient-service-patient-service-1:latest",
                List.of(8080),
                patientServiceDb,
                Map.of("BILLING_SERVICE_ADDRESS", "host.docker.internal",
                        "BILLING_SERVICE_GRPC_PORT", "9001"));

        patientService.getNode().addDependency(patientServiceDb);
        patientService.getNode().addDependency(patientServiceDbHealthCheck);
        patientService.getNode().addDependency(billingService);
        patientService.getNode().addDependency(mskCluster);
    }

    private Vpc createVpc() {
        return Vpc.Builder.create(this, "PatientManagementVPC")
                .vpcName("PatientManagementVPC")
                .maxAzs(2)
                .build();
    }

    private DatabaseInstance createPostgresDatabase(String id, String dbName) {
        return DatabaseInstance.Builder.create(this, id)
                .engine(DatabaseInstanceEngine.postgres(PostgresInstanceEngineProps.builder()
                                .version(PostgresEngineVersion.VER_17_2)
                                .build()))
                .vpc(vpc)
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                .allocatedStorage(20)
                .credentials(Credentials.fromGeneratedSecret("admin"))
                .databaseName(dbName)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
    }

    private DatabaseInstance createMySQLDatabase(String id, String dbName) {
        return DatabaseInstance.Builder.create(this, id)
                .engine(DatabaseInstanceEngine.mysql(MySqlInstanceEngineProps.builder()
                        .version(MysqlEngineVersion.VER_8_4_3)
                        .build()))
                .vpc(vpc)
                .instanceType(InstanceType.of(InstanceClass.BURSTABLE2, InstanceSize.MICRO))
                .allocatedStorage(20)
                .credentials(Credentials.fromGeneratedSecret("admin"))
                .databaseName(dbName)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
    }

    private CfnHealthCheck createDBHealthCheck(DatabaseInstance dbInstance, String id) {
        return CfnHealthCheck.Builder.create(this, id)
                .healthCheckConfig(CfnHealthCheck.HealthCheckConfigProperty.builder()
                        .type("TCP")
                        .port(Token.asNumber(dbInstance.getDbInstanceEndpointPort()))
                        .ipAddress(dbInstance.getDbInstanceEndpointAddress())
                        .requestInterval(30)
                        .failureThreshold(3)
                        .build())
                .build();
    }

    private CfnCluster createMSKCluster() {
        return CfnCluster.Builder.create(this, "MSKCluster")
                .clusterName("kafka-cluster")
                .kafkaVersion("2.8.0")
                .numberOfBrokerNodes(1)
                .brokerNodeGroupInfo(CfnCluster.BrokerNodeGroupInfoProperty.builder()
                        .instanceType("kafka.m5.xlarge")
                        .clientSubnets(vpc.getPrivateSubnets().stream().map(ISubnet::getSubnetId)
                                .collect(Collectors.toList()))
                        .brokerAzDistribution("DEFAULT")
                        .build())
                .build();
    }

    private Cluster createECSCluster() {
        return Cluster.Builder.create(this, "patient-management-cluster")
                .vpc(this.vpc)
                .defaultCloudMapNamespace(CloudMapNamespaceOptions.builder()
                        .name("patient-management.localstack").build())
                .build();
    }

    private FargateService createFargateService(String id, String imageName, List<Integer> ports, DatabaseInstance db, Map<String, String> envVariables) {
        FargateTaskDefinition fargateTaskDefinition = FargateTaskDefinition.Builder.create(this, id + "Task")
                .cpu(256)
                .memoryLimitMiB(512)
                .build();

        ContainerDefinitionOptions.Builder containerDefinitionOptions = ContainerDefinitionOptions.builder()
                .image(ContainerImage.fromRegistry(imageName))
                .portMappings(ports.stream().map(port -> PortMapping.builder()
                        .containerPort(port)
                        .hostPort(port)
                        .protocol(Protocol.TCP)
                        .build()).toList())
                .logging(LogDriver.awsLogs(AwsLogDriverProps.builder()
                                .logGroup(LogGroup.Builder.create(this, id + "LogGroup")
                                        .logGroupName("/ecs/" + imageName)
                                        .removalPolicy(RemovalPolicy.DESTROY)
                                        .retention(RetentionDays.ONE_DAY)
                                        .build())
                                .streamPrefix(imageName)
                        .build()));

        Map<String, String> envVars = new HashMap<>();
        envVars.put("SPRING_KAFKA_BOOTSTRAP_SERVERS", "localhost.localstack.cloud:4510, localhost.localstack.cloud:4511, localhost.localstack.cloud:4512");

        if(envVariables != null) {
            envVars.putAll(envVariables);
        }

        if(db != null) {
            if("mysql".equals(db.getEngine().getEngineType())) {
                envVars.put("SPRING_DATASOURCE_URL", "jdbc:mysql://%s:%s/%s-db".formatted(db.getDbInstanceEndpointAddress(), db.getDbInstanceEndpointPort(), imageName));
            } else if ("postgres".equals(db.getEngine().getEngineType())) {
                envVars.put("SPRING_DATASOURCE_URL", "jdbc:postgresql://%s:%s/%s-db".formatted(db.getDbInstanceEndpointAddress(), db.getDbInstanceEndpointPort(), imageName));
            }

            envVars.put("SPRING_DATASOURCE_USERNAME", "admin");
            envVars.put("SPRING_DATASOURCE_PASSWORD", db.getSecret().secretValueFromJson("password").toString());
            envVars.put("SPRING_JPA_HIBERNATE_DDL_AUTO", "update");
            envVars.put("SPRING_SQL_INIT_MODE", "always");
            envVars.put("SPRING_DATASOURCE_HIKARI_INITIALIZATION_FAIL_TIMEOUT", "60000");
        }

        containerDefinitionOptions.environment(envVars);

        fargateTaskDefinition.addContainer(imageName + "Container", containerDefinitionOptions.build());

        return FargateService.Builder.create(this, id)
                .cluster(ecsCluster)
                .taskDefinition(fargateTaskDefinition)
                .assignPublicIp(false)
                .serviceName(imageName)
                .build();
    }

    public static void main(String[] args) {
        App app = new App(AppProps.builder().outdir("./cdk.out").build());
        StackProps props = StackProps.builder().synthesizer(new BootstraplessSynthesizer()).build();

        new LocalStack(app, "localstack", props);
        app.synth();
        System.out.println("App synthesizing in progress...");
    }


}
