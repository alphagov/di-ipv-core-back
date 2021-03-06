package uk.gov.di.ipv.core.library.service;

import com.nimbusds.oauth2.sdk.AccessTokenResponse;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.OAuth2Error;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.token.AccessToken;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.Tokens;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import uk.gov.di.ipv.core.library.annotations.ExcludeFromGeneratedCoverageReport;
import uk.gov.di.ipv.core.library.persistence.DataStore;
import uk.gov.di.ipv.core.library.persistence.item.AccessTokenItem;
import uk.gov.di.ipv.core.library.validation.ValidationResult;

import java.time.Instant;
import java.util.Objects;

import static uk.gov.di.ipv.core.library.config.EnvironmentVariable.ACCESS_TOKENS_TABLE_NAME;

public class AccessTokenService {
    protected static final Scope DEFAULT_SCOPE = new Scope("user-credentials");
    private final DataStore<AccessTokenItem> dataStore;
    private final ConfigurationService configurationService;

    @ExcludeFromGeneratedCoverageReport
    public AccessTokenService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        boolean isRunningLocally = this.configurationService.isRunningLocally();
        this.dataStore =
                new DataStore<>(
                        this.configurationService.getEnvironmentVariable(ACCESS_TOKENS_TABLE_NAME),
                        AccessTokenItem.class,
                        DataStore.getClient(isRunningLocally),
                        isRunningLocally,
                        configurationService);
    }

    public AccessTokenService(
            DataStore<AccessTokenItem> dataStore, ConfigurationService configurationService) {
        this.dataStore = dataStore;
        this.configurationService = configurationService;
    }

    public TokenResponse generateAccessToken() {
        AccessToken accessToken =
                new BearerAccessToken(
                        configurationService.getBearerAccessTokenTtl(), DEFAULT_SCOPE);
        return new AccessTokenResponse(new Tokens(accessToken, null));
    }

    public ValidationResult<ErrorObject> validateAuthorizationGrant(AuthorizationGrant authGrant) {
        if (!authGrant.getType().equals(GrantType.AUTHORIZATION_CODE)) {
            return new ValidationResult<>(false, OAuth2Error.UNSUPPORTED_GRANT_TYPE);
        }
        return ValidationResult.createValidResult();
    }

    public AccessTokenItem getAccessToken(String accessToken) {
        return dataStore.getItem(DigestUtils.sha256Hex(accessToken));
    }

    public void persistAccessToken(AccessTokenResponse tokenResponse, String ipvSessionId) {
        AccessTokenItem accessTokenItem = new AccessTokenItem();
        BearerAccessToken accessToken = tokenResponse.getTokens().getBearerAccessToken();
        accessTokenItem.setAccessToken(DigestUtils.sha256Hex(accessToken.getValue()));
        accessTokenItem.setIpvSessionId(ipvSessionId);
        accessTokenItem.setExpiryDateTime(toExpiryDateTime(accessToken.getLifetime()));
        dataStore.create(accessTokenItem);
    }

    public void revokeAccessToken(AccessTokenItem accessTokenItem) throws IllegalArgumentException {
        if (StringUtils.isBlank(accessTokenItem.getRevokedAtDateTime())) {
            accessTokenItem.setRevokedAtDateTime(Instant.now().toString());
            dataStore.update(accessTokenItem);
        }
    }

    public void revokeAccessToken(String accessToken) throws IllegalArgumentException {
        AccessTokenItem accessTokenItem = dataStore.getItem(accessToken);

        if (Objects.nonNull(accessTokenItem)) {
            revokeAccessToken(accessTokenItem);
        } else {
            throw new IllegalArgumentException(
                    "Failed to revoke access token - access token could not be found in DynamoDB");
        }
    }

    private String toExpiryDateTime(long expirySeconds) {
        return Instant.now().plusSeconds(expirySeconds).toString();
    }
}
