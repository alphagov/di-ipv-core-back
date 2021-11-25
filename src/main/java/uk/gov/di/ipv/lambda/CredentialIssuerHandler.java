package uk.gov.di.ipv.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.di.ipv.domain.ErrorResponse;
import uk.gov.di.ipv.dto.CredentialIssuerConfig;
import uk.gov.di.ipv.dto.CredentialIssuerRequestDto;
import uk.gov.di.ipv.helpers.ApiGatewayResponseGenerator;
import uk.gov.di.ipv.helpers.RequestHelper;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class CredentialIssuerHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialIssuerHandler.class);

    private Set<CredentialIssuerConfig> credentialIssuers;

    public CredentialIssuerHandler(Set<CredentialIssuerConfig> credentialIssuerConfig) {
        this.credentialIssuers = Objects.requireNonNull(credentialIssuerConfig);
    }

    public CredentialIssuerHandler() {
        CredentialIssuerConfig passportIssuer = new CredentialIssuerConfig("PassportIssuer", URI.create("http://www.example.com"));
        CredentialIssuerConfig fraudIssuer = new CredentialIssuerConfig("FraudIssuer", URI.create("http://www.example.com"));
        this.credentialIssuers = Set.of(passportIssuer, fraudIssuer);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        Map<String, String> body = RequestHelper.parseRequestBody(input.getBody());
        ObjectMapper objectMapper = new ObjectMapper();
        CredentialIssuerRequestDto request = objectMapper.convertValue(body, CredentialIssuerRequestDto.class);

        var errorResponse = validate(request);
        if (errorResponse.isPresent()) {
            return errorResponse.get();
        }

        CredentialIssuerConfig credentialIssuerConfig = getCredentialIssuerConfig(request).get();

        AuthorizationCode authorizationCode = new AuthorizationCode(request.getAuthorization_code());
        TokenRequest tokenRequest = new TokenRequest(
                credentialIssuerConfig.getTokenUrl(),
                new ClientID("IPV_CLIENT_1"),
                new AuthorizationCodeGrant(authorizationCode, URI.create(request.getRedirect_uri()))
        );

        HTTPResponse httpResponse = sendHttpRequest(tokenRequest.toHTTPRequest());

        return ApiGatewayResponseGenerator.proxyJsonResponse(200, Collections.EMPTY_MAP);


    }

    private Optional<APIGatewayProxyResponseEvent> validate(CredentialIssuerRequestDto request) {
        if (StringUtils.isBlank(request.getAuthorization_code())) {
            return Optional.of(ApiGatewayResponseGenerator.proxyJsonResponse(400, ErrorResponse.MissingAuthorizationCode));
        }

        if (StringUtils.isBlank(request.getCredential_issuer_id())) {
            return Optional.of(ApiGatewayResponseGenerator.proxyJsonResponse(400, ErrorResponse.MissingCredentialIssuerId));
        }

        Optional<CredentialIssuerConfig> first = getCredentialIssuerConfig(request);
        if (first.isEmpty()) {
            return Optional.of(ApiGatewayResponseGenerator.proxyJsonResponse(400, ErrorResponse.InvalidCredentialIssuerId));
        }
        //todo check that the redirect_uri is in config list
        return Optional.empty();
    }

    private Optional<CredentialIssuerConfig> getCredentialIssuerConfig(CredentialIssuerRequestDto request) {
        Optional<CredentialIssuerConfig> first = credentialIssuers.stream()
                .filter(config -> request.getCredential_issuer_id().equals(config.getId()))
                .findFirst();
        return first;
    }

    private HTTPResponse sendHttpRequest(HTTPRequest httpRequest) {
        try {
            return httpRequest.send();
        } catch (IOException | SerializeException exception) {
            LOGGER.error("Failed to send a http request", exception);
            // todo what error to throw, and how to handle
            throw new RuntimeException("Failed to send a http request", exception);
        }
    }

}
