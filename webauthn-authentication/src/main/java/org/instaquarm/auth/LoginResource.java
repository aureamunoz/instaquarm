package org.instaquarm.auth;

import io.quarkus.security.webauthn.WebAuthnLoginResponse;
import io.quarkus.security.webauthn.WebAuthnRegisterResponse;
import io.quarkus.security.webauthn.WebAuthnSecurity;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.auth.webauthn.Authenticator;
import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestForm;

import static jakarta.ws.rs.core.Response.Status.*;

@Path("/login")
public class LoginResource {

    @Inject
    WebAuthnSecurity webAuthnSecurity;

    @Inject
    UserRepository users;

    @Inject
    WebAuthnCredentialRepository credentials;

    @POST
    public Uni<Response> login(@RestForm String userName,
                               @BeanParam WebAuthnLoginResponse webAuthnResponse,
                               RoutingContext ctx) {
        // Input validation
        if (userName == null || userName.isEmpty()
                || !webAuthnResponse.isSet()
                || !webAuthnResponse.isValid()) {
            return Uni.createFrom().item(Response.status(BAD_REQUEST).build());
        }

        Uni<User> userUni = users.findByUserName(userName);
        return userUni.flatMap(user -> {
            if (user == null) {
                // Invalid user
                return Uni.createFrom().item(Response.status(BAD_REQUEST).build());
            }
            Uni<Authenticator> authenticator = this.webAuthnSecurity.login(webAuthnResponse, ctx);

            return authenticator
                    // bump the auth counter
                    .invoke(auth -> user.credential.counter = auth.getCounter())
                    .map(auth -> {
                        // make a login cookie
                        this.webAuthnSecurity.rememberUser(auth.getUserName(), ctx);
                        return Response.ok().build();
                    })
                    // handle login failure
                    .onFailure().recoverWithItem(x -> {
                        // make a proper error response
                        return Response.status(BAD_REQUEST).build();
                    });

        });
    }

    @Path("/register")
    @POST
    public Uni<Response> register(@RestForm String userName,
                                  @BeanParam WebAuthnRegisterResponse webAuthnResponse,
                                  RoutingContext ctx) {
        // Input validation
        if (userName == null || userName.isEmpty()
                || !webAuthnResponse.isSet()
                || !webAuthnResponse.isValid()) {
            return Uni.createFrom().item(Response.status(BAD_REQUEST).build());
        }

        Uni<User> userUni = users.findByUserName(userName);
        return userUni.flatMap(user -> {
            if (user != null) {
                // Duplicate user
                return Uni.createFrom().item(Response.status(BAD_REQUEST).build());
            }
            Uni<Authenticator> authenticator = this.webAuthnSecurity.register(webAuthnResponse, ctx);

            return authenticator
                    // store the user
                    .map(auth -> {
                        User newUser = new User();
                        newUser.username = auth.getUserName();
                        WebAuthnCredential credential = new WebAuthnCredential(auth, newUser);
                        credentials.persist(credential);
                        users.persist(newUser);
                        this.webAuthnSecurity.rememberUser(newUser.username, ctx);
                        return Response.ok().build();
                    })
                    // handle login failure
                    .onFailure().recoverWithItem(x -> {
                        // make a proper error response
                        return Response.status(BAD_REQUEST).build();
                    });

        });
    }
}
