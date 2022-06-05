package uk.gov.di.ipv.core.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.di.ipv.core.library.domain.IdentityClaim;
import uk.gov.di.ipv.core.library.domain.UserIdentity;
import uk.gov.di.ipv.core.library.domain.VectorOfTrust;
import uk.gov.di.ipv.core.library.persistence.DataStore;
import uk.gov.di.ipv.core.library.persistence.item.UserIssuedCredentialsItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static uk.gov.di.ipv.core.library.domain.VerifiableCredentialConstants.VC_EVIDENCE;
import static uk.gov.di.ipv.core.library.fixtures.TestFixtures.SIGNED_VC_1;
import static uk.gov.di.ipv.core.library.fixtures.TestFixtures.SIGNED_VC_2;
import static uk.gov.di.ipv.core.library.fixtures.TestFixtures.SIGNED_VC_3;
import static uk.gov.di.ipv.core.library.fixtures.TestFixtures.SIGNED_VC_4;
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
    void shouldReturnCredentialsFromDataStore() {
        List<UserIssuedCredentialsItem> userIssuedCredentialsItemList =
                List.of(
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1", "ukPassport", SIGNED_VC_1, LocalDateTime.now()),
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1", "fraud", SIGNED_VC_2, LocalDateTime.now()));

        when(mockDataStore.getItems(anyString())).thenReturn(userIssuedCredentialsItemList);

        UserIdentity credentials =
                userIdentityService.generateUserIdentity("ipv-session-id-1", "test-sub");

        assertEquals(SIGNED_VC_1, credentials.getVcs().get(0));
        assertEquals(SIGNED_VC_2, credentials.getVcs().get(1));
        assertEquals("test-sub", credentials.getSub());
    }

    @Test
    void shouldReturnCredentialFromDataStoreForSpecificCri() {
        String ipvSessionId = "ipvSessionId";
        String criId = "criId";
        UserIssuedCredentialsItem credentialItem =
                createUserIssuedCredentialsItem(
                        "ipv-session-id-1", "ukPassport", SIGNED_VC_1, LocalDateTime.now());

        when(mockDataStore.getItem(ipvSessionId, criId)).thenReturn(credentialItem);

        UserIssuedCredentialsItem retrievedCredentialItem =
                userIdentityService.getUserIssuedCredential(ipvSessionId, criId);

        assertEquals(credentialItem, retrievedCredentialItem);
    }

    @Test
    void shouldReturnDebugCredentialsFromDataStore() {
        List<UserIssuedCredentialsItem> userIssuedCredentialsItemList =
                List.of(
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1",
                                "ukPassport",
                                SIGNED_VC_1,
                                LocalDateTime.parse("2022-01-25T12:28:56.414849")),
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1",
                                "fraud",
                                SIGNED_VC_2,
                                LocalDateTime.parse("2022-01-25T12:28:56.414849")));

        when(mockDataStore.getItems(anyString())).thenReturn(userIssuedCredentialsItemList);

        Map<String, String> credentials =
                userIdentityService.getUserIssuedDebugCredentials("ipv-session-id-1");

        assertEquals(
                "{\"attributes\":{\"ipvSessionId\":\"ipv-session-id-1\",\"dateCreated\":\"2022-01-25T12:28:56.414849\"},\"evidence\":{\"validityScore\":2,\"strengthScore\":4,\"txn\":\"1e0f28c5-6329-46f0-bf0e-833cb9b58c9e\",\"type\":\"IdentityCheck\"}}",
                credentials.get("ukPassport"));
        assertEquals(
                "{\"attributes\":{\"ipvSessionId\":\"ipv-session-id-1\",\"dateCreated\":\"2022-01-25T12:28:56.414849\"},\"evidence\":{\"txn\":\"some-uuid\",\"identityFraudScore\":1,\"type\":\"CriStubCheck\"}}",
                credentials.get("fraud"));
    }

    @Test
    void shouldReturnDebugCredentialsFromDataStoreWhenMissingAGpg45Score() throws Exception {
        Map<String, Object> credentialVcClaim = vcClaim(Map.of("test", "test-value"));
        credentialVcClaim.put(VC_EVIDENCE, List.of());
        List<UserIssuedCredentialsItem> userIssuedCredentialsItemList =
                List.of(
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1",
                                "ukPassport",
                                generateVerifiableCredential(credentialVcClaim),
                                LocalDateTime.parse("2022-01-25T12:28:56.414849")),
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1",
                                "fraud",
                                generateVerifiableCredential(credentialVcClaim),
                                LocalDateTime.parse("2022-01-25T12:28:56.414849")));

        when(mockDataStore.getItems(anyString())).thenReturn(userIssuedCredentialsItemList);

        Map<String, String> credentials =
                userIdentityService.getUserIssuedDebugCredentials("ipv-session-id-1");

        assertEquals(
                "{\"attributes\":{\"ipvSessionId\":\"ipv-session-id-1\",\"dateCreated\":\"2022-01-25T12:28:56.414849\"}}",
                credentials.get("ukPassport"));
        assertEquals(
                "{\"attributes\":{\"ipvSessionId\":\"ipv-session-id-1\",\"dateCreated\":\"2022-01-25T12:28:56.414849\"}}",
                credentials.get("fraud"));
    }

    @Test
    void shouldReturnDebugCredentialsEvenIfFailingToParseCredentialJson() {
        List<UserIssuedCredentialsItem> userIssuedCredentialsItemList =
                List.of(
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1",
                                "ukPassport",
                                "invalid-verifiable-credential",
                                LocalDateTime.parse("2022-01-25T12:28:56.414849")));

        when(mockDataStore.getItems(anyString())).thenReturn(userIssuedCredentialsItemList);

        Map<String, String> credentials =
                userIdentityService.getUserIssuedDebugCredentials("ipv-session-id-1");

        assertEquals(
                "{\"attributes\":{\"ipvSessionId\":\"ipv-session-id-1\",\"dateCreated\":\"2022-01-25T12:28:56.414849\"}}",
                credentials.get("ukPassport"));
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
                                "ukPassport",
                                generateVerifiableCredential(credentialVcClaim),
                                LocalDateTime.parse("2022-01-25T12:28:56.414849")));

        when(mockDataStore.getItems(anyString())).thenReturn(userIssuedCredentialsItemList);

        Map<String, String> credentials =
                userIdentityService.getUserIssuedDebugCredentials("ipv-session-id-1");

        assertEquals(
                "{\"attributes\":{\"ipvSessionId\":\"ipv-session-id-1\",\"dateCreated\":\"2022-01-25T12:28:56.414849\"}}",
                credentials.get("ukPassport"));
    }

    @Test
    void shouldSetVotClaimToP2OnSuccessfulIdentityCheck() {
        List<UserIssuedCredentialsItem> userIssuedCredentialsItemList =
                List.of(
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1", "ukPassport", SIGNED_VC_1, LocalDateTime.now()),
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1", "fraud", SIGNED_VC_2, LocalDateTime.now()),
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1", "kbv", SIGNED_VC_3, LocalDateTime.now()));

        when(mockDataStore.getItems(anyString())).thenReturn(userIssuedCredentialsItemList);

        UserIdentity credentials =
                userIdentityService.generateUserIdentity("ipv-session-id-1", "test-sub");

        assertEquals(VectorOfTrust.P2.toString(), credentials.getVot());
    }

    @Test
    void shouldSetVotClaimToP0OnMissingRequiredVC() {
        List<UserIssuedCredentialsItem> userIssuedCredentialsItemList =
                List.of(
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1", "ukPassport", SIGNED_VC_1, LocalDateTime.now()),
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1", "fraud", SIGNED_VC_2, LocalDateTime.now()));

        when(mockDataStore.getItems(anyString())).thenReturn(userIssuedCredentialsItemList);

        UserIdentity credentials =
                userIdentityService.generateUserIdentity("ipv-session-id-1", "test-sub");

        assertEquals(VectorOfTrust.P0.toString(), credentials.getVot());
    }

    @Test
    void shouldSetIdenityClaimWhenVotIsP2() {
        List<UserIssuedCredentialsItem> userIssuedCredentialsItemList =
                List.of(
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1", "ukPassport", SIGNED_VC_1, LocalDateTime.now()),
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1", "fraud", SIGNED_VC_2, LocalDateTime.now()),
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1", "kbv", SIGNED_VC_3, LocalDateTime.now()));

        when(mockDataStore.getItems(anyString())).thenReturn(userIssuedCredentialsItemList);

        UserIdentity credentials =
                userIdentityService.generateUserIdentity("ipv-session-id-1", "test-sub");

        IdentityClaim identityClaim = credentials.getIdentityClaim();

        assertEquals("GivenName", identityClaim.getName().get(0).getNameParts().get(0).getType());
        assertEquals("Paul", identityClaim.getName().get(0).getNameParts().get(0).getValue());

        assertEquals("2020-02-03", identityClaim.getBirthDate().get(0).getValue());
    }

    @Test
    void shouldNotSetIdenityClaimWhenVotIsP0() {
        List<UserIssuedCredentialsItem> userIssuedCredentialsItemList =
                List.of(
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1", "ukPassport", SIGNED_VC_1, LocalDateTime.now()),
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1", "fraud", SIGNED_VC_2, LocalDateTime.now()));

        when(mockDataStore.getItems(anyString())).thenReturn(userIssuedCredentialsItemList);

        UserIdentity credentials =
                userIdentityService.generateUserIdentity("ipv-session-id-1", "test-sub");

        assertNull(credentials.getIdentityClaim());
    }

    @Test
    void shouldSetsubClaimOnUserIdentity() {
        when(mockConfigurationService.getCoreVtmClaim()).thenReturn("mock-vtm-claim");

        UserIdentity credentials =
                userIdentityService.generateUserIdentity("ipv-session-id-1", "test-sub");

        assertEquals("test-sub", credentials.getSub());
    }

    @Test
    void shouldSetVotClaimToP0OnFailedIdentityCheck() {
        List<UserIssuedCredentialsItem> userIssuedCredentialsItemList =
                List.of(
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1", "ukPassport", SIGNED_VC_1, LocalDateTime.now()),
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1", "fraud", SIGNED_VC_2, LocalDateTime.now()),
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1", "kbv", SIGNED_VC_4, LocalDateTime.now()));

        when(mockDataStore.getItems(anyString())).thenReturn(userIssuedCredentialsItemList);

        UserIdentity credentials =
                userIdentityService.generateUserIdentity("ipv-session-id-1", "test-sub");

        assertEquals(VectorOfTrust.P0.toString(), credentials.getVot());
    }

    @Test
    void shouldSetVtmClaimOnUserIdentity() {
        when(mockConfigurationService.getCoreVtmClaim()).thenReturn("mock-vtm-claim");

        UserIdentity credentials =
                userIdentityService.generateUserIdentity("ipv-session-id-1", "test-sub");

        assertEquals("mock-vtm-claim", credentials.getVtm());
    }

    @Test
    void shouldReturnListOfVcsForSharedAttributes() {
        List<UserIssuedCredentialsItem> userIssuedCredentialsItemList =
                List.of(
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1", "ukPassport", SIGNED_VC_1, LocalDateTime.now()),
                        createUserIssuedCredentialsItem(
                                "ipv-session-id-1", "fraud", SIGNED_VC_2, LocalDateTime.now()));

        when(mockDataStore.getItems(anyString())).thenReturn(userIssuedCredentialsItemList);

        List<String> vcList = userIdentityService.getUserIssuedCredentials("ipv-session-id-1");

        assertEquals(SIGNED_VC_1, vcList.get(0));
        assertEquals(SIGNED_VC_2, vcList.get(1));
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
