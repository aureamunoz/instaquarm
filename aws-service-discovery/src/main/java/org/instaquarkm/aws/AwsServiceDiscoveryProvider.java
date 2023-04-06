package org.instaquarkm.aws;

import io.smallrye.stork.api.ServiceDiscovery;
import io.smallrye.stork.api.config.ServiceConfig;
import io.smallrye.stork.api.config.ServiceDiscoveryAttribute;
import io.smallrye.stork.api.config.ServiceDiscoveryType;
import io.smallrye.stork.spi.ServiceDiscoveryProvider;
import io.smallrye.stork.spi.StorkInfrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ServiceDiscoveryType("aws-api-gateway")
@ServiceDiscoveryAttribute(name = "api",
        description = "The name of the API to look for. Defaults to the service name.")
@ServiceDiscoveryAttribute(name = "stage",
        description = "The name of the stage, mandatory if there are multiple stages defined.")
@ServiceDiscoveryAttribute(name = "region",
        description = "The cloud region", defaultValue = "us-east-1")
@ApplicationScoped
public class AwsServiceDiscoveryProvider implements ServiceDiscoveryProvider<AwsApiGatewayConfiguration> {

    @Inject StorkAwsCredentialsProvider credentialsProvider;

    public ServiceDiscovery createServiceDiscovery(AwsApiGatewayConfiguration awsConfiguration, String name, ServiceConfig serviceConfig, StorkInfrastructure storkInfrastructure) {
        return new AwsApiGatewayDiscovery(awsConfiguration, name, credentialsProvider);

    }
}
