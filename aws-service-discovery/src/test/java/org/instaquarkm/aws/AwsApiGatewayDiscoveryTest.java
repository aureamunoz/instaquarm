package org.instaquarkm.aws;

import io.smallrye.stork.api.ServiceInstance;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;


@Disabled
class AwsApiGatewayDiscoveryTest {


    @Test
    void test() {
        var c = new AwsApiGatewayConfiguration();
        c = c.withRegion("us-east-1");

        AwsApiGatewayDiscovery discovery = new AwsApiGatewayDiscovery(c, "squarer", null, null);
        ServiceInstance squarer = discovery.getServiceInstances().await().indefinitely().get(0);
        Assertions.assertEquals("vaekn02h31.execute-api.us-east-1.amazonaws.com", squarer.getHost());
        Assertions.assertEquals("/stage/", squarer.getPath().orElse(null));
        Assertions.assertTrue(squarer.isSecure());
        Assertions.assertEquals(433, squarer.getPort());
    }

}