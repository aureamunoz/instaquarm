package org.instaquarm.auth;

import io.quarkus.arc.Unremovable;
import io.quarkus.security.webauthn.WebAuthnUserProvider;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.auth.webauthn.AttestationCertificates;
import io.vertx.ext.auth.webauthn.Authenticator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@ApplicationScoped
@Typed({WebAuthnUserProvider.class, WebAuthnCredentialRepository.class})
@Unremovable
public class WebAuthnCredentialRepository implements WebAuthnUserProvider {

    // DEMO ONLY - Keep everything in memory
    private final List<WebAuthnCredential> credentials = new CopyOnWriteArrayList<>();

    @Inject
    UserRepository users;

    public Uni<List<WebAuthnCredential>> findByUserName(String username) {
        return Uni.createFrom().item(() ->
                credentials.stream().filter(cred -> cred.userName.equals(username)).collect(Collectors.toList())
        );
    }

    @Override
    public Uni<List<Authenticator>> findWebAuthnCredentialsByUserName(String userName) {
        return findByUserName(userName)
                .flatMap(WebAuthnCredentialRepository::toAuthenticators);
    }

    public Uni<List<WebAuthnCredential>> findByCredId(String credId) {
        return Uni.createFrom().item(() ->
                credentials.stream().filter(cred -> cred.credID.equals(credId)).collect(Collectors.toList())
        );
    }

    @Override
    public Uni<List<Authenticator>> findWebAuthnCredentialsByCredID(String userName) {
        return findByCredId(userName)
                .flatMap(WebAuthnCredentialRepository::toAuthenticators);
    }

    @Override
    public Uni<Void> updateOrStoreWebAuthnCredentials(Authenticator authenticator) {
        // leave the scooby user to the manual endpoint, because if we do it here it will be
        // created/updated twice
        if (authenticator.getUserName().equals("scooby")) {
            return Uni.createFrom().nullItem();
        }

        return users.findByUserName(authenticator.getUserName())
                .invoke(user -> {
                    // new user
                    if (user == null) {
                        User newUser = new User();
                        newUser.username = authenticator.getUserName();
                        WebAuthnCredential credential = new WebAuthnCredential(authenticator, newUser);
                        // The constructor updates the new user, so no need to set it.

                        persist(credential);
                        users.persist(newUser);
                        System.out.println("New user registered " + authenticator.getUserName());
                    } else {
                        // existing user
                        user.credential.counter = authenticator.getCounter();
                    }
                }).replaceWithVoid();
    }

    public void persist(WebAuthnCredential credential) {
        credentials.add(credential);
    }


    private static Uni<List<Authenticator>> toAuthenticators(List<WebAuthnCredential> dbs) {
        // can't call combine/uni on empty list
        if (dbs.isEmpty()) {
            return Uni.createFrom().item(Collections.emptyList());
        }
        List<Uni<Authenticator>> ret = new ArrayList<>(dbs.size());
        for (WebAuthnCredential db : dbs) {
            ret.add(toAuthenticator(db));
        }
        return Uni.join().all(ret).andFailFast();
    }

    private static Uni<Authenticator> toAuthenticator(WebAuthnCredential credential) {
        return Uni.createFrom().item(() -> credential.x5c)
                .map(x5c -> {
                    Authenticator ret = new Authenticator();
                    ret.setAaguid(credential.aaguid);
                    AttestationCertificates attestationCertificates = new AttestationCertificates();
                    attestationCertificates.setAlg(credential.alg);
                    List<String> x5cs = new ArrayList<>(x5c.size());
                    for (WebAuthnCertificate webAuthnCertificate : x5c) {
                        x5cs.add(webAuthnCertificate.x5c);
                    }
                    ret.setAttestationCertificates(attestationCertificates);
                    ret.setCounter(credential.counter);
                    ret.setCredID(credential.credID);
                    ret.setFmt(credential.fmt);
                    ret.setPublicKey(credential.publicKey);
                    ret.setType(credential.type);
                    ret.setUserName(credential.userName);
                    return ret;
                });
    }

    @Override
    public Set<String> getRoles(String userId) {
        return Collections.singleton("user");
    }


}
