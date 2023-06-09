package org.instaquarm.auth;

import io.vertx.ext.auth.webauthn.Authenticator;
import io.vertx.ext.auth.webauthn.PublicKeyCredential;

import java.util.ArrayList;
import java.util.List;

public class WebAuthnCredential {
    
    /**
     * The username linked to this authenticator
     */
    public String userName;

    /**
     * The type of key (must be "public-key")
     */
    public String type = "public-key";

    /**
     * The non user identifiable id for the authenticator
     */
    public String credID;

    /**
     * The public key associated with this authenticator
     */
    public String publicKey;

    /**
     * The signature counter of the authenticator to prevent replay attacks
     */
    public long counter;

    public String aaguid;

    /**
     * The Authenticator attestation certificates object, a JSON like:
     * <pre>{@code
     *   {
     *     "alg": "string",
     *     "x5c": [
     *       "base64"
     *     ]
     *   }
     * }</pre>
     */
    /**
     * The algorithm used for the public credential
     */
    public PublicKeyCredential alg;

    /**
     * The list of X509 certificates encoded as base64url.
     */
    public List<WebAuthnCertificate> x5c = new ArrayList<>();
    
    public String fmt;
    
    // owning side
    public User user;

    public WebAuthnCredential() {
    }
    
    public WebAuthnCredential(Authenticator authenticator, User user) {
        aaguid = authenticator.getAaguid();
        if(authenticator.getAttestationCertificates() != null)
            alg = authenticator.getAttestationCertificates().getAlg();
        counter = authenticator.getCounter();
        credID = authenticator.getCredID();
        fmt = authenticator.getFmt();
        publicKey = authenticator.getPublicKey();
        type = authenticator.getType();
        userName = authenticator.getUserName();
        if(authenticator.getAttestationCertificates() != null
                && authenticator.getAttestationCertificates().getX5c() != null) {
            for (String x5c : authenticator.getAttestationCertificates().getX5c()) {
                WebAuthnCertificate cert = new WebAuthnCertificate();
                cert.x5c = x5c;
                cert.webAuthnCredential = this;
                this.x5c.add(cert);
            }
        }
        this.user = user;
        user.credential = this;
    }

    @Override
    public String toString() {
        return "WebAuthnCredential{" +
                "userName='" + userName + '\'' +
                ", type='" + type + '\'' +
                ", credID='" + credID + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", counter=" + counter +
                ", aaguid='" + aaguid + '\'' +
                ", alg=" + alg +
                ", x5c=" + x5c +
                ", fmt='" + fmt + '\'' +
                ", user=" + user +
                '}';
    }
}