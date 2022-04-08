package uk.gov.di.ipv.core.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.di.ipv.core.library.domain.UserIdentity;
import uk.gov.di.ipv.core.library.persistence.DataStore;
import uk.gov.di.ipv.core.library.persistence.item.UserIssuedCredentialsItem;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.di.ipv.core.library.domain.VerifiableCredentialConstants.VC_EVIDENCE;
import static uk.gov.di.ipv.core.library.fixtures.TestFixtures.SIGNED_VC_1;
import static uk.gov.di.ipv.core.library.fixtures.TestFixtures.SIGNED_VC_2;
import static uk.gov.di.ipv.core.library.helpers.VerifiableCredentialGenerator.generateVerifiableCredential;
import static uk.gov.di.ipv.core.library.helpers.VerifiableCredentialGenerator.vcClaim;

@ExtendWith(MockitoExtension.class)
class UserIdentityServiceTest {

    @Mock private ConfigurationService mockConfigurationService;

    @Mock private DataStore<UserIssuedCredentialsItem> mockDataStore;

    private UserIdentityService userIdentityService;

    @BeforeEach
    void setUp() {
        userIdentityService = new UserIdentityService(mockConfigurationService, mockDataStore);
    }

    @Test
    void shouldReturnCredentialsFromDataStore() throws ParseException {
        List<UserIssuedCredentialsItem> userIssuedCredentialsItemList =
                List.of(
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1",
                                "PassportIssuer",
                                SIGNED_VC_1,
                                LocalDateTime.now()),
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1",
                                "FraudIssuer",
                                SIGNED_VC_2,
                                LocalDateTime.now()));

        when(mockDataStore.getItems(anyString())).thenReturn(userIssuedCredentialsItemList);

        UserIdentity credentials = userIdentityService.getUserIssuedCredentials("ipv-session-id-1");

        assertEquals(SIGNED_VC_1, credentials.getVcs().get(0));
        assertEquals(SIGNED_VC_2, credentials.getVcs().get(1));
    }

    @Test
    void shouldReturnDebugCredentialsFromDataStore() {
        List<UserIssuedCredentialsItem> userIssuedCredentialsItemList =
                List.of(
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1",
                                "PassportIssuer",
                                SIGNED_VC_1,
                                LocalDateTime.parse("2022-01-25T12:28:56.414849")),
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1",
                                "FraudIssuer",
                                SIGNED_VC_2,
                                LocalDateTime.parse("2022-01-25T12:28:56.414849")));

        when(mockDataStore.getItems(anyString())).thenReturn(userIssuedCredentialsItemList);

        Map<String, String> credentials =
                userIdentityService.getUserIssuedDebugCredentials("ipv-session-id-1");

        assertEquals(
                "{\"attributes\":{\"ipvSessionId\":\"ipv-session-id-1\",\"dateCreated\":\"2022-01-25T12:28:56.414849\"},\"evidence\":{\"strength\":4,\"validity\":2,\"type\":\"CriStubCheck\"}}",
                credentials.get("PassportIssuer"));
        assertEquals(
                "{\"attributes\":{\"ipvSessionId\":\"ipv-session-id-1\",\"dateCreated\":\"2022-01-25T12:28:56.414849\"},\"evidence\":{\"Gpg45\":\"Score\"}}",
                credentials.get("FraudIssuer"));
    }

    @Test
    void shouldReturnDebugCredentialsFromDataStoreWhenMissingAGpg45Score() throws Exception {
        Map<String, Object> credentialVcClaim = vcClaim(Map.of("test", "test-value"));
        credentialVcClaim.put(VC_EVIDENCE, List.of());
        List<UserIssuedCredentialsItem> userIssuedCredentialsItemList =
                List.of(
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1",
                                "PassportIssuer",
                                generateVerifiableCredential(credentialVcClaim),
                                LocalDateTime.parse("2022-01-25T12:28:56.414849")),
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1",
                                "FraudIssuer",
                                generateVerifiableCredential(credentialVcClaim),
                                LocalDateTime.parse("2022-01-25T12:28:56.414849")));

        when(mockDataStore.getItems(anyString())).thenReturn(userIssuedCredentialsItemList);

        Map<String, String> credentials =
                userIdentityService.getUserIssuedDebugCredentials("ipv-session-id-1");

        assertEquals(
                "{\"attributes\":{\"ipvSessionId\":\"ipv-session-id-1\",\"dateCreated\":\"2022-01-25T12:28:56.414849\"}}",
                credentials.get("PassportIssuer"));
        assertEquals(
                "{\"attributes\":{\"ipvSessionId\":\"ipv-session-id-1\",\"dateCreated\":\"2022-01-25T12:28:56.414849\"}}",
                credentials.get("FraudIssuer"));
    }

    @Test
    void shouldReturnDebugCredentialsEvenIfFailingToParseCredentialJson() {
        List<UserIssuedCredentialsItem> userIssuedCredentialsItemList =
                List.of(
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1",
                                "PassportIssuer",
                                "invalid-verifiable-credential",
                                LocalDateTime.parse("2022-01-25T12:28:56.414849")));

        when(mockDataStore.getItems(anyString())).thenReturn(userIssuedCredentialsItemList);

        Map<String, String> credentials =
                userIdentityService.getUserIssuedDebugCredentials("ipv-session-id-1");

        assertEquals(
                "{\"attributes\":{\"ipvSessionId\":\"ipv-session-id-1\",\"dateCreated\":\"2022-01-25T12:28:56.414849\"}}",
                credentials.get("PassportIssuer"));
    }

    @Test
    void shouldReturnDebugCredentialsEvenIfFailingToParseGpg45ScoreParamFromJson()
            throws Exception {
        Map<String, Object> credentialVcClaim = vcClaim(Map.of("test", "test-value"));
        credentialVcClaim.put(VC_EVIDENCE, "This should be a list of objects...");
        List<UserIssuedCredentialsItem> userIssuedCredentialsItemList =
                List.of(
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1",
                                "PassportIssuer",
                                generateVerifiableCredential(credentialVcClaim),
                                LocalDateTime.parse("2022-01-25T12:28:56.414849")));

        when(mockDataStore.getItems(anyString())).thenReturn(userIssuedCredentialsItemList);

        Map<String, String> credentials =
                userIdentityService.getUserIssuedDebugCredentials("ipv-session-id-1");

        assertEquals(
                "{\"attributes\":{\"ipvSessionId\":\"ipv-session-id-1\",\"dateCreated\":\"2022-01-25T12:28:56.414849\"}}",
                credentials.get("PassportIssuer"));
    }

    private UserIssuedCredentialsItem createUserIssuedCredentialsItem(
            String ipvSessionId,
            String credentialIssuer,
            String credential,
            LocalDateTime dateCreated) {
        UserIssuedCredentialsItem userIssuedCredentialsItem = new UserIssuedCredentialsItem();
        userIssuedCredentialsItem.setIpvSessionId(ipvSessionId);
        userIssuedCredentialsItem.setCredentialIssuer(credentialIssuer);
        userIssuedCredentialsItem.setCredential(credential);
        userIssuedCredentialsItem.setDateCreated(dateCreated);
        return userIssuedCredentialsItem;
    }
}
