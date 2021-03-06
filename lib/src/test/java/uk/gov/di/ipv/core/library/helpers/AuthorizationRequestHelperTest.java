package uk.gov.di.ipv.core.library.helpers;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.di.ipv.core.library.domain.Address;
import uk.gov.di.ipv.core.library.domain.BirthDate;
import uk.gov.di.ipv.core.library.domain.Name;
import uk.gov.di.ipv.core.library.domain.NameParts;
import uk.gov.di.ipv.core.library.domain.SharedClaimsResponse;
import uk.gov.di.ipv.core.library.dto.CredentialIssuerConfig;
import uk.gov.di.ipv.core.library.exceptions.HttpResponseExceptionWithErrorBody;
import uk.gov.di.ipv.core.library.service.ConfigurationService;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.util.Base64;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.di.ipv.core.library.config.ConfigurationVariable.AUDIENCE_FOR_CLIENTS;
import static uk.gov.di.ipv.core.library.config.ConfigurationVariable.CORE_FRONT_CALLBACK_URL;
import static uk.gov.di.ipv.core.library.config.ConfigurationVariable.JWT_TTL_SECONDS;
import static uk.gov.di.ipv.core.library.fixtures.TestFixtures.EC_PRIVATE_KEY;
import static uk.gov.di.ipv.core.library.fixtures.TestFixtures.EC_PUBLIC_JWK;
import static uk.gov.di.ipv.core.library.fixtures.TestFixtures.RSA_ENCRYPTION_PRIVATE_KEY;
import static uk.gov.di.ipv.core.library.fixtures.TestFixtures.RSA_ENCRYPTION_PUBLIC_JWK;
import static uk.gov.di.ipv.core.library.fixtures.TestFixtures.SIGNED_JWT;
import static uk.gov.di.ipv.core.library.helpers.AuthorizationRequestHelper.SHARED_CLAIMS;

@ExtendWith(MockitoExtension.class)
class AuthorizationRequestHelperTest {

    public static final String CLIENT_ID_FIELD = "client_id";
    public static final String IPV_CLIENT_ID_VALUE = "testClientId";
    public static final String IPV_ISSUER = "http://example.com/issuer";
    public static final String AUDIENCE = "Audience";
    public static final String IPV_TOKEN_TTL = "900";
    public static final String MOCK_CORE_FRONT_CALLBACK_URL = "callbackUri";
    public static final String CRI_ID = "cri_id";
    public static final String TEST_USER_ID = "test-user-id";
    public static final String OAUTH_STATE = SecureTokenHelper.generate();

    private final SharedClaimsResponse sharedClaims =
            new SharedClaimsResponse(
                    Set.of(new Name(List.of(new NameParts("Dan", "first_name")))),
                    Set.of(new BirthDate("2011-01-01")),
                    Set.of(new Address()));

    private ECDSASigner signer;

    @Mock JWSSigner jwsSigner;

    @Mock CredentialIssuerConfig credentialIssuerConfig;

    @Mock ConfigurationService configurationService;

    private RSAEncrypter rsaEncrypter;

    @BeforeEach
    void setUp()
            throws InvalidKeySpecException, NoSuchAlgorithmException, JOSEException,
                    ParseException {
        signer = new ECDSASigner(getPrivateKey());
        rsaEncrypter = new RSAEncrypter((RSAPublicKey) getEncryptionPublicKey());
    }

    @Test
    void shouldCreateSignedJWTWithCorrectClaims()
            throws JOSEException, ParseException, HttpResponseExceptionWithErrorBody {
        setupCredentialIssuerConfigMock();
        setupConfigurationServiceMock();
        when(credentialIssuerConfig.getAudienceForClients()).thenReturn(AUDIENCE);

        SignedJWT result =
                AuthorizationRequestHelper.createSignedJWT(
                        sharedClaims,
                        signer,
                        credentialIssuerConfig,
                        configurationService,
                        OAUTH_STATE,
                        TEST_USER_ID);

        assertEquals(IPV_ISSUER, result.getJWTClaimsSet().getIssuer());
        assertEquals(TEST_USER_ID, result.getJWTClaimsSet().getSubject());
        assertEquals(AUDIENCE, result.getJWTClaimsSet().getAudience().get(0));
        assertEquals(sharedClaims, result.getJWTClaimsSet().getClaims().get(SHARED_CLAIMS));
        assertEquals(OAUTH_STATE.toString(), result.getJWTClaimsSet().getClaim("state"));
        assertEquals(
                IPV_CLIENT_ID_VALUE, result.getJWTClaimsSet().getClaims().get(CLIENT_ID_FIELD));
        assertEquals(
                String.format("%s?id=%s", MOCK_CORE_FRONT_CALLBACK_URL, CRI_ID),
                result.getJWTClaimsSet().getClaims().get("redirect_uri"));
        assertTrue(result.verify(new ECDSAVerifier(ECKey.parse(EC_PUBLIC_JWK))));
    }

    @Test
    void shouldNotReturnSharedClaimsIfSharedClaimsMapIsEmpty()
            throws ParseException, HttpResponseExceptionWithErrorBody {
        setupCredentialIssuerConfigMock();
        setupConfigurationServiceMock();

        SignedJWT result =
                AuthorizationRequestHelper.createSignedJWT(
                        null,
                        signer,
                        credentialIssuerConfig,
                        configurationService,
                        OAUTH_STATE,
                        TEST_USER_ID);
        assertNull(result.getJWTClaimsSet().getClaims().get(SHARED_CLAIMS));
    }

    @Test
    void shouldThrowExceptionWhenUnableToSignJwt() {
        setupCredentialIssuerConfigMock();
        setupConfigurationServiceMock();

        HttpResponseExceptionWithErrorBody exception =
                assertThrows(
                        HttpResponseExceptionWithErrorBody.class,
                        () ->
                                AuthorizationRequestHelper.createSignedJWT(
                                        null,
                                        jwsSigner,
                                        credentialIssuerConfig,
                                        configurationService,
                                        OAUTH_STATE,
                                        TEST_USER_ID));
        assertEquals(500, exception.getResponseCode());
        assertEquals("Failed to sign Shared Attributes", exception.getErrorReason());
    }

    @Test
    void shouldThrowExceptionWhenUnableToBuildRedirectionUri() {
        when(credentialIssuerConfig.getId()).thenReturn(CRI_ID);
        when(configurationService.getSsmParameter(CORE_FRONT_CALLBACK_URL)).thenReturn("[[]]]][[[");

        HttpResponseExceptionWithErrorBody exception =
                assertThrows(
                        HttpResponseExceptionWithErrorBody.class,
                        () ->
                                AuthorizationRequestHelper.createSignedJWT(
                                        null,
                                        jwsSigner,
                                        credentialIssuerConfig,
                                        configurationService,
                                        OAUTH_STATE,
                                        TEST_USER_ID));
        assertEquals(500, exception.getResponseCode());
        assertEquals("Failed to build Core Front Callback Url", exception.getErrorReason());
    }

    @Test
    void shouldCreateJWEObject()
            throws ParseException, JOSEException, NoSuchAlgorithmException, InvalidKeySpecException,
                    HttpResponseExceptionWithErrorBody {
        JWEObject result =
                AuthorizationRequestHelper.createJweObject(
                        rsaEncrypter, SignedJWT.parse(SIGNED_JWT));

        assertEquals(JWEObject.State.ENCRYPTED, result.getState());

        RSADecrypter rsaDecrypter = new RSADecrypter(getEncryptionPrivateKey());
        result.decrypt(rsaDecrypter);
        SignedJWT signedJWT = result.getPayload().toSignedJWT();

        assertEquals(IPV_CLIENT_ID_VALUE, signedJWT.getJWTClaimsSet().getIssuer());
        assertEquals(IPV_CLIENT_ID_VALUE, signedJWT.getJWTClaimsSet().getSubject());
        assertEquals(AUDIENCE, signedJWT.getJWTClaimsSet().getAudience().get(0));
        assertEquals(
                IPV_CLIENT_ID_VALUE, signedJWT.getJWTClaimsSet().getClaims().get(CLIENT_ID_FIELD));
        assertEquals(
                String.format("%s?id=%s", MOCK_CORE_FRONT_CALLBACK_URL, CRI_ID),
                signedJWT.getJWTClaimsSet().getClaims().get("redirect_uri"));
    }

    @Test
    void shouldThrowExceptionWhenUnableToEncryptJwt() {
        HttpResponseExceptionWithErrorBody exception =
                assertThrows(
                        HttpResponseExceptionWithErrorBody.class,
                        () ->
                                AuthorizationRequestHelper.createJweObject(
                                        mock(RSAEncrypter.class), SignedJWT.parse(SIGNED_JWT)));
        assertEquals(500, exception.getResponseCode());
        assertEquals("Failed to encrypt JWT", exception.getErrorReason());
    }

    private void setupCredentialIssuerConfigMock() {
        when(credentialIssuerConfig.getId()).thenReturn(CRI_ID);
        when(credentialIssuerConfig.getIpvClientId()).thenReturn(IPV_CLIENT_ID_VALUE);
    }

    private void setupConfigurationServiceMock() {
        when(configurationService.getSsmParameter(CORE_FRONT_CALLBACK_URL))
                .thenReturn(MOCK_CORE_FRONT_CALLBACK_URL);
        when(configurationService.getSsmParameter(JWT_TTL_SECONDS)).thenReturn(IPV_TOKEN_TTL);
        when(configurationService.getSsmParameter(AUDIENCE_FOR_CLIENTS)).thenReturn(IPV_ISSUER);
    }

    private PrivateKey getEncryptionPrivateKey()
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance("RSA")
                .generatePrivate(
                        new PKCS8EncodedKeySpec(
                                Base64.getDecoder().decode(RSA_ENCRYPTION_PRIVATE_KEY)));
    }

    private ECPrivateKey getPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
        return (ECPrivateKey)
                KeyFactory.getInstance("EC")
                        .generatePrivate(
                                new PKCS8EncodedKeySpec(
                                        Base64.getDecoder().decode(EC_PRIVATE_KEY)));
    }

    private PublicKey getEncryptionPublicKey()
            throws NoSuchAlgorithmException, ParseException, InvalidKeySpecException {
        RSAKey rsaKey = RSAKey.parse(RSA_ENCRYPTION_PUBLIC_JWK);
        RSAPublicKeySpec rsaPublicKeySpec =
                new RSAPublicKeySpec(
                        rsaKey.getModulus().decodeToBigInteger(),
                        rsaKey.getPublicExponent().decodeToBigInteger());
        return KeyFactory.getInstance("RSA").generatePublic(rsaPublicKeySpec);
    }
}
