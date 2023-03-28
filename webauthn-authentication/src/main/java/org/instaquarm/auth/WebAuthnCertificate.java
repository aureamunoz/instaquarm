package org.instaquarm.auth;

public class WebAuthnCertificate {

    public WebAuthnCredential webAuthnCredential;

    /**
     * The list of X509 certificates encoded as base64url.
     */
    public String x5c;
}