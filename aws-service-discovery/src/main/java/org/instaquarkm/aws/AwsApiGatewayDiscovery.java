package org.instaquarkm.aws;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import io.smallrye.stork.api.NoServiceInstanceFoundException;
import io.smallrye.stork.api.ServiceDiscovery;
import io.smallrye.stork.api.ServiceInstance;
import io.smallrye.stork.impl.DefaultServiceInstance;
import io.smallrye.stork.utils.ServiceInstanceIds;
import org.jboss.logging.Logger;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.apigateway.ApiGatewayAsyncClient;
import software.amazon.awssdk.services.apigateway.ApiGatewayAsyncClientBuilder;
import software.amazon.awssdk.services.apigateway.model.GetStagesRequest;
import software.amazon.awssdk.services.apigateway.model.RestApi;
import software.amazon.awssdk.services.apigateway.model.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class AwsApiGatewayDiscovery implements ServiceDiscovery {

    private static final Logger LOGGER = Logger.getLogger(AwsApiGatewayDiscovery.class);

    private final ApiGatewayAsyncClient client;
    private final String serviceName;

    private final static String HOST_TEMPLATE = "%s.execute-api.%s.amazonaws.com";

    private final static String NO_API_ERROR = "No REST APIS named %s found in region %s";
    private final static String NO_STAGE_ERROR = "No stage defined for API %s (%S) in region %s";

    private final static String TOO_MANY_STAGES_ERROR = "Too many stages defined for API %s (%s) in region %s. Set " +
            "the `stage` attribute to one of the following value: %s";


    private final String region;
    private final String stage;
    private final Uni<List<ServiceInstance>> retrieval;

    public AwsApiGatewayDiscovery(AwsApiGatewayConfiguration awsConfiguration, String name, StorkAwsCredentialsProvider credentialsProvider) {
        ApiGatewayAsyncClientBuilder builder;
        if (credentialsProvider == null || credentialsProvider.resolveCredentials().accessKeyId() == null) {
            LOGGER.info("using default AWS credentials");
            builder = ApiGatewayAsyncClient.builder()
                    .region(Region.of(awsConfiguration.getRegion()));
        } else {
            LOGGER.info("using configured AWS credential");
            builder = ApiGatewayAsyncClient.builder()
                    .credentialsProvider(credentialsProvider)
                    .region(Region.of(awsConfiguration.getRegion()));
        }
        client = builder.build();
        serviceName = awsConfiguration.getApi() == null ? name : awsConfiguration.getApi();
        region = awsConfiguration.getRegion();
        stage = awsConfiguration.getStage();

        retrieval = Uni.createFrom().completionStage(client::getRestApis).map(resp -> {
            for (RestApi item : resp.items()) {
                if (item.name().equals(serviceName)) {
                    return item;
                }
            }
            throw new NoServiceInstanceFoundException(NO_API_ERROR.formatted(serviceName, region));
        }).flatMap(api -> {
            if (stage != null) {
                return Uni.createFrom().item(Tuple2.of(api, stage));
            } else {
                return Uni.createFrom().completionStage(() ->
                        client.getStages(GetStagesRequest.builder().restApiId(api.id()).build())
                ).map(resp -> {
                    var stages = resp.item();
                    if (stages == null || stages.size() == 0) {
                        throw new NoServiceInstanceFoundException(NO_STAGE_ERROR.formatted(serviceName, api.id(), region));
                    }
                    if (stages.size() != 1) {
                        throw new NoServiceInstanceFoundException(TOO_MANY_STAGES_ERROR.formatted(serviceName, api.id(), region,
                                stages.stream().map(Stage::stageName).collect(Collectors.joining(","))));
                    } else {
                        return Tuple2.of(api, stages.get(0).stageName());
                    }
                });
            }
        }).<List<ServiceInstance>>map(tuple ->
                List.of(new DefaultServiceInstance(ServiceInstanceIds.next(),
                        HOST_TEMPLATE.formatted(tuple.getItem1().id(), region), 443,
                        "/" + tuple.getItem2(), true))
        ).memoize().indefinitely()
                .log();
    }

    @Override
    public Uni<List<ServiceInstance>> getServiceInstances() {
        return retrieval;
    }
}
