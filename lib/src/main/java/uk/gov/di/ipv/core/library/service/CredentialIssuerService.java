package uk.gov.di.ipv.core.library.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.PrivateKeyJWT;
import com.nimbusds.oauth2.sdk.client.ClientReadRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import uk.gov.di.ipv.core.library.annotations.ExcludeFromGeneratedCoverageReport;
import uk.gov.di.ipv.core.library.domain.ClientAuthClaims;
import uk.gov.di.ipv.core.library.domain.CredentialIssuerException;
import uk.gov.di.ipv.core.library.domain.ErrorResponse;
import uk.gov.di.ipv.core.library.dto.CredentialIssuerConfig;
import uk.gov.di.ipv.core.library.dto.CredentialIssuerRequestDto;
import uk.gov.di.ipv.core.library.helpers.JwtHelper;
import uk.gov.di.ipv.core.library.persistence.DataStore;
import uk.gov.di.ipv.core.library.persistence.item.AuthorizationCodeItem;
import uk.gov.di.ipv.core.library.persistence.item.UserIssuedCredentialsItem;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primarySortKey;

public class CredentialIssuerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CredentialIssuerService.class);

    private final DataStore<UserIssuedCredentialsItem> dataStore;
    private final ConfigurationService configurationService;
    private final JWSSigner signer;

    public static final TableSchema<UserIssuedCredentialsItem> USER_ISSUED_CREDENTIALS_ITEM_TABLE_SCHEMA = StaticTableSchema.builder(UserIssuedCredentialsItem.class)
            .newItemSupplier(UserIssuedCredentialsItem::new)
            .addAttribute(String.class, a -> a.name("ipvSessionId")
                    .getter(UserIssuedCredentialsItem::getIpvSessionId)
                    .setter(UserIssuedCredentialsItem::setIpvSessionId)
                    .tags(primaryPartitionKey()))
            .addAttribute(String.class, a -> a.name("credentialIssuer")
                    .getter(UserIssuedCredentialsItem::getCredentialIssuer)
                    .setter(UserIssuedCredentialsItem::setCredentialIssuer)
                    .tags(primarySortKey()))
            .addAttribute(String.class, a -> a.name("credential")
                    .getter(UserIssuedCredentialsItem::getCredential)
                    .setter(UserIssuedCredentialsItem::setCredential))
            .addAttribute(LocalDateTime.class, a -> a.name("dateCreated")
                    .getter(UserIssuedCredentialsItem::getDateCreated)
                    .setter(UserIssuedCredentialsItem::setDateCreated))
            .build();

    @ExcludeFromGeneratedCoverageReport
    public CredentialIssuerService(ConfigurationService configurationService, JWSSigner signer) {
        this.configurationService = configurationService;
        this.signer = signer;
        boolean isRunningLocally = this.configurationService.isRunningLocally();
        this.dataStore =
                new DataStore<>(
                        this.configurationService.getUserIssuedCredentialTableName(),
                        USER_ISSUED_CREDENTIALS_ITEM_TABLE_SCHEMA,
                        DataStore.getClient(isRunningLocally),
                        isRunningLocally);
    }

    // used for testing
    public CredentialIssuerService(
            DataStore<UserIssuedCredentialsItem> dataStore,
            ConfigurationService configurationService,
            JWSSigner signer) {
        this.configurationService = configurationService;
        this.signer = signer;
        this.dataStore = dataStore;
    }

    public BearerAccessToken exchangeCodeForToken(
            CredentialIssuerRequestDto request, CredentialIssuerConfig config) {

        AuthorizationCode authorizationCode = new AuthorizationCode(request.getAuthorizationCode());
        try {
            OffsetDateTime dateTime = OffsetDateTime.now();
            ClientAuthClaims clientAuthClaims =
                    new ClientAuthClaims(
                            config.getIpvClientId(),
                            config.getIpvClientId(),
                            config.getTokenUrl().toString(),
                            dateTime.plusSeconds(
                                            Long.parseLong(configurationService.getIpvTokenTtl()))
                                    .toEpochSecond());
            SignedJWT signedClientJwt =
                    JwtHelper.createSignedJwtFromObject(clientAuthClaims, signer);

            ClientAuthentication clientAuthentication = new PrivateKeyJWT(signedClientJwt);

            TokenRequest tokenRequest =
                    new TokenRequest(
                            config.getTokenUrl(),
                            clientAuthentication,
                            new AuthorizationCodeGrant(
                                    authorizationCode, URI.create(request.getRedirectUri())));

            HTTPResponse httpResponse = tokenRequest.toHTTPRequest().send();
            TokenResponse tokenResponse = parseTokenResponse(httpResponse);

            if (tokenResponse instanceof TokenErrorResponse) {
                TokenErrorResponse errorResponse = tokenResponse.toErrorResponse();
                ErrorObject errorObject =
                        Objects.requireNonNullElse(
                                errorResponse.getErrorObject(),
                                new ErrorObject("unknown", "unknown"));
                LOGGER.error(
                        "Failed to exchange token with credential issuer with ID '{}' at '{}'. Code: '{}', Description: {}, HttpStatus code: {}",
                        config.getId(),
                        config.getTokenUrl(),
                        errorObject.getCode(),
                        errorObject.getDescription(),
                        errorObject.getHTTPStatusCode());
                throw new CredentialIssuerException(
                        HTTPResponse.SC_BAD_REQUEST, ErrorResponse.INVALID_TOKEN_REQUEST);
            }
            return tokenResponse.toSuccessResponse().getTokens().getBearerAccessToken();
        } catch (IOException | ParseException | JOSEException e) {
            LOGGER.error("Error exchanging token: {}", e.getMessage(), e);
            throw new CredentialIssuerException(
                    HTTPResponse.SC_SERVER_ERROR,
                    ErrorResponse.FAILED_TO_EXCHANGE_AUTHORIZATION_CODE);
        }
    }

    public JSONObject getCredential(BearerAccessToken accessToken, CredentialIssuerConfig config) {
        ClientReadRequest credentialRequest =
                new ClientReadRequest(config.getCredentialUrl(), accessToken);

        try {
            HTTPResponse response = credentialRequest.toHTTPRequest().send();
            if (!response.indicatesSuccess()) {
                LOGGER.error(
                        "Error retrieving credential: {} - {}",
                        response.getStatusCode(),
                        response.getStatusMessage());
                throw new CredentialIssuerException(
                        HTTPResponse.SC_SERVER_ERROR,
                        ErrorResponse.FAILED_TO_GET_CREDENTIAL_FROM_ISSUER);
            }

            return response
                    .getContentAsJSONObject(); // In future we can use response.getContentAsJWT()
        } catch (IOException | ParseException e) {
            LOGGER.error("Error retrieving credential: {}", e.getMessage(), e);
            throw new CredentialIssuerException(
                    HTTPResponse.SC_SERVER_ERROR,
                    ErrorResponse.FAILED_TO_GET_CREDENTIAL_FROM_ISSUER);
        }
    }

    private String getClientId() {
        return Optional.ofNullable(System.getenv("IPV_CLIENT_ID")).orElse("DI IPV CLIENT");
    }

    private TokenResponse parseTokenResponse(HTTPResponse httpResponse) throws ParseException {
        return OIDCTokenResponseParser.parse(httpResponse);
    }

    public void persistUserCredentials(JSONObject credential, CredentialIssuerRequestDto request) {
        UserIssuedCredentialsItem userIssuedCredentials = new UserIssuedCredentialsItem();
        userIssuedCredentials.setIpvSessionId(request.getIpvSessionId());
        userIssuedCredentials.setCredentialIssuer(request.getCredentialIssuerId());
        userIssuedCredentials.setCredential(credential.toJSONString());
        userIssuedCredentials.setDateCreated(LocalDateTime.now());
        try {
            dataStore.create(userIssuedCredentials);
        } catch (UnsupportedOperationException e) {
            LOGGER.error("Error persisting user credential: {}", e.getMessage(), e);
            throw new CredentialIssuerException(
                    HTTPResponse.SC_SERVER_ERROR, ErrorResponse.FAILED_TO_SAVE_CREDENTIAL);
        }
    }
}
