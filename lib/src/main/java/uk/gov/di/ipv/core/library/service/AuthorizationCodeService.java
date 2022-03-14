package uk.gov.di.ipv.core.library.service;

import com.nimbusds.oauth2.sdk.AuthorizationCode;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import uk.gov.di.ipv.core.library.annotations.ExcludeFromGeneratedCoverageReport;
import uk.gov.di.ipv.core.library.persistence.DataStore;
import uk.gov.di.ipv.core.library.persistence.item.AccessTokenItem;
import uk.gov.di.ipv.core.library.persistence.item.AuthorizationCodeItem;

import java.util.Optional;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

public class AuthorizationCodeService {
    private final DataStore<AuthorizationCodeItem> dataStore;
    private final ConfigurationService configurationService;

    public static final TableSchema<AuthorizationCodeItem> AUTHORIZATION_CODE_ITEM_TABLE_SCHEMA = StaticTableSchema.builder(AuthorizationCodeItem.class)
            .newItemSupplier(AuthorizationCodeItem::new)
            .addAttribute(String.class, a -> a.name("authCode")
                    .getter(AuthorizationCodeItem::getAuthCode)
                    .setter(AuthorizationCodeItem::setAuthCode)
                    .tags(primaryPartitionKey()))
            .addAttribute(String.class, a -> a.name("ipvSessionId")
                    .getter(AuthorizationCodeItem::getIpvSessionId)
                    .setter(AuthorizationCodeItem::setIpvSessionId))
            .addAttribute(String.class, a -> a.name("redirectUrl")
                    .getter(AuthorizationCodeItem::getRedirectUrl)
                    .setter(AuthorizationCodeItem::setRedirectUrl))
            .build();

    @ExcludeFromGeneratedCoverageReport
    public AuthorizationCodeService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        boolean isRunningLocally = this.configurationService.isRunningLocally();
        this.dataStore =
                new DataStore<>(
                        this.configurationService.getAuthCodesTableName(),
                        AUTHORIZATION_CODE_ITEM_TABLE_SCHEMA,
                        DataStore.getClient(isRunningLocally),
                        isRunningLocally);
    }

    public AuthorizationCodeService(
            DataStore<AuthorizationCodeItem> dataStore, ConfigurationService configurationService) {
        this.configurationService = configurationService;
        this.dataStore = dataStore;
    }

    public AuthorizationCode generateAuthorizationCode() {
        return new AuthorizationCode();
    }

    public Optional<AuthorizationCodeItem> getAuthorizationCodeItem(String authorizationCode) {
        AuthorizationCodeItem authorizationCodeItem = dataStore.getItem(authorizationCode);
        return Optional.ofNullable(authorizationCodeItem);
    }

    public void persistAuthorizationCode(
            String authorizationCode, String ipvSessionId, String redirectUrl) {
        AuthorizationCodeItem authorizationCodeItem = new AuthorizationCodeItem();
        authorizationCodeItem.setAuthCode(authorizationCode);
        authorizationCodeItem.setIpvSessionId(ipvSessionId);
        authorizationCodeItem.setRedirectUrl(redirectUrl);

        dataStore.create(authorizationCodeItem);
    }

    public void revokeAuthorizationCode(String authorizationCode) {
        dataStore.delete(authorizationCode);
    }
}
