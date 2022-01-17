package uk.gov.di.ipv.core.credentialissuer;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import net.minidev.json.JSONObject;
import uk.gov.di.ipv.core.library.annotations.ExcludeFromGeneratedCoverageReport;
import uk.gov.di.ipv.core.library.domain.CredentialIssuerException;
import uk.gov.di.ipv.core.library.domain.ErrorResponse;
import uk.gov.di.ipv.core.library.dto.CredentialIssuerConfig;
import uk.gov.di.ipv.core.library.dto.CredentialIssuerRequestDto;
import uk.gov.di.ipv.core.library.dto.CredentialIssuers;
import uk.gov.di.ipv.core.library.helpers.ApiGatewayResponseGenerator;
import uk.gov.di.ipv.core.library.helpers.RequestHelper;
import uk.gov.di.ipv.core.library.service.ConfigurationService;
import uk.gov.di.ipv.core.library.service.CredentialIssuerService;

import java.util.Collections;
import java.util.Optional;

public class CredentialIssuerHandler
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final CredentialIssuerService credentialIssuerService;
    private final ConfigurationService configurationService;

    private CredentialIssuers credentialIssuers;

    static {
        // Set the default synchronous HTTP client to UrlConnectionHttpClient
        System.setProperty(
                "software.amazon.awssdk.http.service.impl",
                "software.amazon.awssdk.http.urlconnection.UrlConnectionSdkHttpService");
    }

    public CredentialIssuerHandler(
            CredentialIssuerService credentialIssuerService,
            ConfigurationService configurationService) {
        this.credentialIssuerService = credentialIssuerService;
        this.configurationService = configurationService;
        this.credentialIssuers = configurationService.getCredentialIssuers(credentialIssuers);
    }

    @ExcludeFromGeneratedCoverageReport
    public CredentialIssuerHandler() {
        this.credentialIssuerService = new CredentialIssuerService();
        this.configurationService = new ConfigurationService();
        this.credentialIssuers = configurationService.getCredentialIssuers(credentialIssuers);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent input, Context context) {

        CredentialIssuerRequestDto request =
                RequestHelper.convertRequest(input, CredentialIssuerRequestDto.class);

        var errorResponse = validate(request);
        if (errorResponse.isPresent()) {
            return ApiGatewayResponseGenerator.proxyJsonResponse(400, errorResponse.get());
        }
        CredentialIssuerConfig credentialIssuerConfig = getCredentialIssuerConfig(request);

        try {
            BearerAccessToken accessToken =
                    credentialIssuerService.exchangeCodeForToken(request, credentialIssuerConfig);
            JSONObject credential =
                    credentialIssuerService.getCredential(accessToken, credentialIssuerConfig);
            credentialIssuerService.persistUserCredentials(credential, request);
            return ApiGatewayResponseGenerator.proxyJsonResponse(200, Collections.emptyMap());
        } catch (CredentialIssuerException e) {
            return ApiGatewayResponseGenerator.proxyJsonResponse(
                    e.getHttpStatusCode(), e.getErrorResponse());
        }
    }

    private Optional<ErrorResponse> validate(CredentialIssuerRequestDto request) {
        if (StringUtils.isBlank(request.getAuthorizationCode())) {
            return Optional.of(ErrorResponse.MISSING_AUTHORIZATION_CODE);
        }

        if (StringUtils.isBlank(request.getCredentialIssuerId())) {
            return Optional.of(ErrorResponse.MISSING_CREDENTIAL_ISSUER_ID);
        }

        if (StringUtils.isBlank(request.getIpvSessionId())) {
            return Optional.of(ErrorResponse.MISSING_IPV_SESSION_ID);
        }

        if (getCredentialIssuerConfig(request) == null) {
            return Optional.of(ErrorResponse.INVALID_CREDENTIAL_ISSUER_ID);
        }
        return Optional.empty();
    }

    private CredentialIssuerConfig getCredentialIssuerConfig(CredentialIssuerRequestDto request) {
        return configurationService
                .getCredentialIssuers(credentialIssuers)
                .getCredentialIssuerConfigs()
                .stream()
                .filter(config -> request.getCredentialIssuerId().equals(config.getId()))
                .findFirst()
                .orElse(null);
    }
}