package org.instaquarm.auth;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.util.ArrayList;

public class Application {

    @Inject WebAuthnCredentialRepository webauthn;
    @Inject UserRepository users;

    public void init(@Observes StartupEvent ev) {
        var clement = new User();
        clement.username = "clement";
        users.persist(clement);

        var credential = new WebAuthnCredential();
        credential.aaguid = "00000000-0000-0000-0000-000000000000";
        credential.counter = 1;
        credential.alg = null;
        credential.credID = "Co5VWMTtqLcpkQdrVwwvgbsvPL0XIevWJSJ2HhDfD7g";
        credential.publicKey = "pQECAyYgASFYIDs5SEwnNholOlD6Cmqo1j1w19gt72aGShJhB5GZxVP3IlggPpuF-KsLYDvrpF4sR0jsJDmKymOR1c6wDaK9VyE_Eog";
        credential.type = "public-key";
        credential.user = clement;
        credential.userName = clement.username;
        credential.fmt = "none";
        credential.x5c = new ArrayList<>();
        webauthn.persist(credential);

        clement.credential = credential;
    }
}
