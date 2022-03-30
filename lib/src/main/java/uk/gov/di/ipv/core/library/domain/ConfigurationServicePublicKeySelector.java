package uk.gov.di.ipv.core.library.domain;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.oauth2.sdk.auth.ClientAuthenticationMethod;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.auth.verifier.ClientCredentialsSelector;
import com.nimbusds.oauth2.sdk.auth.verifier.Context;
import com.nimbusds.oauth2.sdk.auth.verifier.InvalidClientException;
import com.nimbusds.oauth2.sdk.id.ClientID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.di.ipv.core.library.service.ConfigurationService;

import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.text.ParseException;
import java.util.Base64;
import java.util.List;

public class ConfigurationServicePublicKeySelector implements ClientCredentialsSelector<Object> {

    public static final Logger LOGGER =
            LoggerFactory.getLogger(ConfigurationServicePublicKeySelector.class);

    private static final Base64.Decoder decoder = Base64.getDecoder();
    public static final String ES256 = "ES256";
    public static final String RS256 = "RS256";

    private final ConfigurationService configurationService;

    public ConfigurationServicePublicKeySelector(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Override
    public List<Secret> selectClientSecrets(
            ClientID claimedClientID, ClientAuthenticationMethod authMethod, Context context) {
        throw new UnsupportedOperationException("We don't do that round here...");
    }

    @Override
    public List<? extends PublicKey> selectPublicKeys(
            ClientID claimedClientID,
            ClientAuthenticationMethod authMethod,
            JWSHeader jwsHeader,
            boolean forceRefresh,
            Context context)
            throws InvalidClientException {

        JWSAlgorithm algorithm = jwsHeader.getAlgorithm();
        String clientId = claimedClientID.getValue();
        String publicKeyMaterial = configurationService.getClientPublicKeyMaterial(clientId);

        switch (algorithm.toString()) {
            case ES256:
                try {
                    return List.of(ECKey.parse(publicKeyMaterial).toECPublicKey());
                } catch (ParseException | JOSEException e) {
                    throw new InvalidClientException(
                            String.format(
                                    "Could not parse public key material for client ID '%s': %s",
                                    clientId, e.getMessage()));
                }
            case RS256:
                try {
                    return List.of(
                            CertificateFactory.getInstance("X.509")
                                    .generateCertificate(
                                            new ByteArrayInputStream(
                                                    decoder.decode(publicKeyMaterial)))
                                    .getPublicKey());
                } catch (CertificateException | IllegalArgumentException e) {
                    throw new InvalidClientException(
                            String.format(
                                    "Could not parse public key material for client ID '%s': %s",
                                    clientId, e.getMessage()));
                }
            default:
                throw new InvalidClientException(
                        String.format(
                                "%s algorithm is not supported. Received from client ID '%s'",
                                algorithm, clientId));
        }
    }
}
