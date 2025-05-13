package com.pm.stack;



import com.amazonaws.services.rds.model.DBInstance;
import software.amazon.awscdk.*;
import software.amazon.awscdk.services.ec2.InstanceClass;
import software.amazon.awscdk.services.ec2.InstanceSize;
import software.amazon.awscdk.services.ec2.InstanceType;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.rds.*;

public class LocalStack extends Stack {

    private final Vpc vpc;

    private final Cluster ecsCluster;

    public LocalStack(final App scope, final String id, final StackProps props) {
        super(scope, id, props);

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


    public static void main(String[] args) {
        App app = new App(AppProps.builder().outdir("./cdk.out").build());
        StackProps props = StackProps.builder().synthesizer(new BootstraplessSynthesizer()).build();

        new LocalStack(app, "localstack", props);
        app.synth();
        System.out.println("App synthesizing in progress...");
    }


}
