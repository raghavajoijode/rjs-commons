package org.subra.commons.helpers;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;

public class GoogleTokenVerificationHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleTokenVerificationHelper.class);
    private static final String CLIENT_ID = "Nzk0NDk5MzE0OTE4LWY4bjQxMjZhYW1yY25ndGk0MDExODZka3Q3Ymk1Mm9kLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29t";

    private GoogleTokenVerificationHelper() {
        throw new UnsupportedOperationException();
    }

    public static Payload verify(String idTokenString) {
        return verifyToken(idTokenString);
    }

    private static Payload verifyToken(String idTokenString) {
        final String clientId = Arrays.toString(Base64.getDecoder().decode(CLIENT_ID));
        final GoogleIdTokenVerifier googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                //.setIssuers(Arrays.asList("https://accounts.google.com", "accounts.google.com"))
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(clientId))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        LOGGER.debug("validating: idTokenString {} for clientId {} and received verifier {}", idTokenString, clientId, googleIdTokenVerifier);
        return Optional.of(googleIdTokenVerifier).map(v -> {
            try {
                return v.verify(idTokenString);
            } catch (GeneralSecurityException | IOException e) {
                LOGGER.error("Exception verifying {} {}", idTokenString, e);
            }
            return null;
        }).map(GoogleIdToken::getPayload).orElse(null);
    }
}
