package uk.gov.di.ipv.core.library.service;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.di.ipv.core.library.dto.ClientSessionDetailsDto;
import uk.gov.di.ipv.core.library.helpers.SecureTokenHelper;
import uk.gov.di.ipv.core.library.persistence.DataStore;
import uk.gov.di.ipv.core.library.persistence.item.IpvSessionItem;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IpvSessionServiceTest {
    private static final String INITIAL_IPV_JOURNEY_STATE = "INITIAL_IPV_JOURNEY";
    private static final String DEBUG_PAGE_STATE = "DEBUG_PAGE";
    private static final String FAILED_CLIENT_JAR_STATE = "FAILED_CLIENT_JAR";
    private static final String IPV_SUCCESS_PAGE_STATE = "IPV_SUCCESS_PAGE";

    @Mock private DataStore<IpvSessionItem> mockDataStore;

    @Mock private ConfigurationService mockConfigurationService;

    @InjectMocks private IpvSessionService ipvSessionService;

    @Test
    void shouldReturnSessionItem() {
        String ipvSessionID = SecureTokenHelper.generate();

        IpvSessionItem ipvSessionItem = new IpvSessionItem();
        ipvSessionItem.setIpvSessionId(ipvSessionID);
        ipvSessionItem.setUserState(INITIAL_IPV_JOURNEY_STATE);
        ipvSessionItem.setCreationDateTime(new Date().toString());

        when(mockDataStore.getItem(ipvSessionID)).thenReturn(ipvSessionItem);

        IpvSessionItem result = ipvSessionService.getIpvSession(ipvSessionID);

        ArgumentCaptor<String> ipvSessionIDArgumentCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockDataStore).getItem(ipvSessionIDArgumentCaptor.capture());
        assertEquals(ipvSessionID, ipvSessionIDArgumentCaptor.getValue());
        assertEquals(ipvSessionItem.getIpvSessionId(), result.getIpvSessionId());
        assertEquals(ipvSessionItem.getUserState(), result.getUserState());
        assertEquals(ipvSessionItem.getCreationDateTime(), result.getCreationDateTime());
    }

    @Test
    void shouldCreateSessionItem() {
        IpvSessionItem ipvSessionItem =
                ipvSessionService.generateIpvSession(
                        new ClientSessionDetailsDto(
                                "jwt",
                                "test-client",
                                "http://example.come",
                                "test-state",
                                "test-user-id",
                                false),
                        null);

        ArgumentCaptor<IpvSessionItem> ipvSessionItemArgumentCaptor =
                ArgumentCaptor.forClass(IpvSessionItem.class);
        verify(mockDataStore).create(ipvSessionItemArgumentCaptor.capture());
        assertNotNull(ipvSessionItemArgumentCaptor.getValue().getIpvSessionId());
        assertNotNull(ipvSessionItemArgumentCaptor.getValue().getCreationDateTime());

        assertEquals(
                ipvSessionItemArgumentCaptor.getValue().getIpvSessionId(),
                ipvSessionItem.getIpvSessionId());
        assertEquals(
                INITIAL_IPV_JOURNEY_STATE, ipvSessionItemArgumentCaptor.getValue().getUserState());
    }

    @Test
    void shouldCreateSessionItemForDebugJourney() {
        IpvSessionItem ipvSessionItem =
                ipvSessionService.generateIpvSession(
                        new ClientSessionDetailsDto(
                                "jwt",
                                "test-client",
                                "http://example.come",
                                "test-state",
                                "test-user-id",
                                true),
                        null);

        ArgumentCaptor<IpvSessionItem> ipvSessionItemArgumentCaptor =
                ArgumentCaptor.forClass(IpvSessionItem.class);
        verify(mockDataStore).create(ipvSessionItemArgumentCaptor.capture());
        assertNotNull(ipvSessionItemArgumentCaptor.getValue().getIpvSessionId());
        assertNotNull(ipvSessionItemArgumentCaptor.getValue().getCreationDateTime());

        assertEquals(
                ipvSessionItemArgumentCaptor.getValue().getIpvSessionId(),
                ipvSessionItem.getIpvSessionId());
        assertEquals(DEBUG_PAGE_STATE, ipvSessionItemArgumentCaptor.getValue().getUserState());
    }

    @Test
    void shouldCreateSessionItemWithErrorObject() {
        ErrorObject testErrorObject = new ErrorObject("server_error", "Test error");
        IpvSessionItem ipvSessionItem =
                ipvSessionService.generateIpvSession(
                        new ClientSessionDetailsDto(
                                "jwt",
                                "test-client",
                                "http://example.come",
                                "test-state",
                                "test-user-id",
                                false),
                        testErrorObject);

        ArgumentCaptor<IpvSessionItem> ipvSessionItemArgumentCaptor =
                ArgumentCaptor.forClass(IpvSessionItem.class);
        verify(mockDataStore).create(ipvSessionItemArgumentCaptor.capture());
        assertNotNull(ipvSessionItemArgumentCaptor.getValue().getIpvSessionId());
        assertNotNull(ipvSessionItemArgumentCaptor.getValue().getCreationDateTime());
        assertEquals(
                ipvSessionItemArgumentCaptor.getValue().getIpvSessionId(),
                ipvSessionItem.getIpvSessionId());
        assertEquals(
                FAILED_CLIENT_JAR_STATE, ipvSessionItemArgumentCaptor.getValue().getUserState());
        assertEquals(
                testErrorObject.getCode(), ipvSessionItemArgumentCaptor.getValue().getErrorCode());
        assertEquals(
                testErrorObject.getDescription(),
                ipvSessionItemArgumentCaptor.getValue().getErrorDescription());
    }

    @Test
    void shouldUpdateSessionItem() {
        IpvSessionItem ipvSessionItem = new IpvSessionItem();
        ipvSessionItem.setIpvSessionId(SecureTokenHelper.generate());
        ipvSessionItem.setUserState(INITIAL_IPV_JOURNEY_STATE);
        ipvSessionItem.setCreationDateTime(new Date().toString());

        ipvSessionService.updateIpvSession(ipvSessionItem);

        verify(mockDataStore).update(ipvSessionItem);
    }

    @Test
    void shouldSetAuthorizationCodeAndMetadataOnSessionItem() {
        AuthorizationCode testCode = new AuthorizationCode();
        IpvSessionItem ipvSessionItem = new IpvSessionItem();
        ipvSessionItem.setIpvSessionId(SecureTokenHelper.generate());
        ipvSessionItem.setUserState(IPV_SUCCESS_PAGE_STATE);
        ipvSessionItem.setCreationDateTime(new Date().toString());

        ipvSessionService.setAuthorizationCode(
                ipvSessionItem, testCode.getValue(), "http://example.com");

        ArgumentCaptor<IpvSessionItem> ipvSessionItemArgumentCaptor =
                ArgumentCaptor.forClass(IpvSessionItem.class);
        verify(mockDataStore).update(ipvSessionItemArgumentCaptor.capture());
        assertNotNull(ipvSessionItemArgumentCaptor.getValue().getAuthorizationCode());
        assertNotNull(ipvSessionItemArgumentCaptor.getValue().getAuthorizationCodeMetadata());
    }

    @Test
    void shouldSetAccessTokenAndMetadataOnSessionItem() {
        BearerAccessToken accessToken = new BearerAccessToken("test-access-token");
        IpvSessionItem ipvSessionItem = new IpvSessionItem();
        ipvSessionItem.setIpvSessionId(SecureTokenHelper.generate());
        ipvSessionItem.setUserState(IPV_SUCCESS_PAGE_STATE);
        ipvSessionItem.setCreationDateTime(new Date().toString());

        ipvSessionService.setAccessToken(ipvSessionItem, accessToken);

        ArgumentCaptor<IpvSessionItem> ipvSessionItemArgumentCaptor =
                ArgumentCaptor.forClass(IpvSessionItem.class);
        verify(mockDataStore).update(ipvSessionItemArgumentCaptor.capture());
        assertNotNull(ipvSessionItemArgumentCaptor.getValue().getAccessToken());
        assertNotNull(ipvSessionItemArgumentCaptor.getValue().getAccessTokenMetadata());
    }
}
