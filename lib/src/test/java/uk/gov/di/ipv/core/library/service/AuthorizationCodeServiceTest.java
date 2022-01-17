package uk.gov.di.ipv.core.library.service;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import uk.gov.di.ipv.core.library.persistence.DataStore;
import uk.gov.di.ipv.core.library.persistence.item.AuthorizationCodeItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthorizationCodeServiceTest {

    private DataStore<AuthorizationCodeItem> mockDataStore;
    private ConfigurationService mockConfigurationService;

    private AuthorizationCodeService authorizationCodeService;

    @BeforeEach
    void setUp() {
        mockDataStore = mock(DataStore.class);
        mockConfigurationService = mock(ConfigurationService.class);
        when(mockConfigurationService.getAuthCodesTableName()).thenReturn("test-auth-code-table");

        authorizationCodeService =
                new AuthorizationCodeService(mockDataStore, mockConfigurationService);
    }

    @Test
    void shouldReturnAnAuthorisationCode() {
        AuthorizationCode result = authorizationCodeService.generateAuthorizationCode();

        assertNotNull(result);
    }

    @Test
    void shouldCreateAuthorizationCodeInDataStore() {
        AuthorizationCode testCode = new AuthorizationCode();
        String ipvSessionId = "session-12345";
        authorizationCodeService.persistAuthorizationCode(testCode.getValue(), ipvSessionId);

        ArgumentCaptor<AuthorizationCodeItem> authorizationCodeItemArgumentCaptor =
                ArgumentCaptor.forClass(AuthorizationCodeItem.class);
        verify(mockDataStore).create(authorizationCodeItemArgumentCaptor.capture());
        assertEquals(
                ipvSessionId, authorizationCodeItemArgumentCaptor.getValue().getIpvSessionId());
        assertEquals(
                testCode.getValue(), authorizationCodeItemArgumentCaptor.getValue().getAuthCode());
    }

    @Test
    void shouldGetSessionIdByAuthCodeWhenValidAuthCodeProvided() {
        AuthorizationCode testCode = new AuthorizationCode();
        String ipvSessionId = "session-12345";

        AuthorizationCodeItem testItem = new AuthorizationCodeItem();
        testItem.setIpvSessionId(ipvSessionId);

        when(mockDataStore.getItem(testCode.getValue())).thenReturn(testItem);

        String resultIpvSessionid =
                authorizationCodeService.getIpvSessionIdByAuthorizationCode(testCode.getValue());

        verify(mockDataStore).getItem(testCode.getValue());
        assertEquals(ipvSessionId, resultIpvSessionid);
    }

    @Test
    void shouldReturnNullWhenInvalidAuthCodeProvided() {
        AuthorizationCode testCode = new AuthorizationCode();

        when(mockDataStore.getItem(testCode.getValue())).thenReturn(null);

        String resultIpvSessionid =
                authorizationCodeService.getIpvSessionIdByAuthorizationCode(testCode.getValue());

        verify(mockDataStore).getItem(testCode.getValue());
        assertNull(resultIpvSessionid);
    }

    @Test
    void shouldCallDeleteWithAuthCode() {
        AuthorizationCode testCode = new AuthorizationCode();

        authorizationCodeService.revokeAuthorizationCode(testCode.getValue());

        verify(mockDataStore).delete(testCode.getValue());
    }
}