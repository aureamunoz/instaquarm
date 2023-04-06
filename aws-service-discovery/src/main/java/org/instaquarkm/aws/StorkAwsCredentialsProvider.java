package org.instaquarkm.aws;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;

import java.util.Optional;

@ApplicationScoped
public class StorkAwsCredentialsProvider implements AwsCredentialsProvider {

    @ConfigProperty(name = "aws-access-key-id")
    Optional<String> accessKey;

    @ConfigProperty(name = "aws-secret-access-key")
    Optional<String> secretAccessKey;

    @Override
    public AwsCredentials resolveCredentials() {
        // null/null means anonymous.
        return AwsBasicCredentials.create(accessKey.orElse(null), secretAccessKey.orElse(null));
    }
}
