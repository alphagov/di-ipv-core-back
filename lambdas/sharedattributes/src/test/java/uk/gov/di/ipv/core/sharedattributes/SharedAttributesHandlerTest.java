package uk.gov.di.ipv.core.sharedattributes;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.di.ipv.core.library.service.ConfigurationService;
import uk.gov.di.ipv.core.library.service.UserIdentityService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SharedAttributesHandlerTest {

    public static final String SESSION_ID = "the-session-id";
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock private Context context;
    @Mock private UserIdentityService userIdentityService;
    @Mock private ConfigurationService configurationService;
    private SharedAttributesHandler underTest;

    @BeforeEach
    void setUp() {
        underTest = new SharedAttributesHandler(userIdentityService);
    }

    @Test
    void shouldExtractSessionIdFromHeaderAndReturnSharedAttributesAndStatusOK()
            throws JsonProcessingException {

        Map<String, Object> credentialInput =
                Map.of(
                        "attributes",
                        Map.of(
                                "names",
                                Map.of(
                                        "givenNames",
                                        List.of("Dan", "Daniel"),
                                        "familyName",
                                        "Watson"),
                                "dateOfBirth",
                                "2021-03-01"));

        when(userIdentityService.getUserIssuedCredentials(SESSION_ID))
                .thenReturn(
                        Map.of(
                                "DcsPassportIssuer",
                                objectMapper.writeValueAsString(credentialInput)));

        APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent();
        input.setHeaders(Map.of("ipv-session-id", SESSION_ID));

        APIGatewayProxyResponseEvent response = underTest.handleRequest(input, context);

        JsonNode body = objectMapper.readTree(response.getBody());

        assertEquals(200, response.getStatusCode());
        assertEquals("Dan", body.get(0).get("forenames").get(0).asText());
    }

    @Test
    void shouldReturnOKIfZeroCredentialExists() {
        when(userIdentityService.getUserIssuedCredentials(SESSION_ID))
                .thenReturn(Collections.emptyMap());

        APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent();
        input.setHeaders(Map.of("ipv-session-id", SESSION_ID));

        APIGatewayProxyResponseEvent response = underTest.handleRequest(input, context);
        assertEquals(200, response.getStatusCode());
    }

    @Test
    void shouldReturnBadRequestIfSessionIdIsNotInTheHeader() {
        APIGatewayProxyRequestEvent input = new APIGatewayProxyRequestEvent();
        input.setHeaders(Map.of("not-ipv-session-header", "dummy-value"));

        APIGatewayProxyResponseEvent response = underTest.handleRequest(input, context);
        assertEquals(400, response.getStatusCode());
        assertEquals("ipv-session-id not present in header", response.getBody());
    }
}
