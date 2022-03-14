package uk.gov.di.ipv.core.library.service;

import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticTableSchema;
import uk.gov.di.ipv.core.library.annotations.ExcludeFromGeneratedCoverageReport;
import uk.gov.di.ipv.core.library.domain.UserStates;
import uk.gov.di.ipv.core.library.persistence.DataStore;
import uk.gov.di.ipv.core.library.persistence.item.IpvSessionItem;
import uk.gov.di.ipv.core.library.persistence.item.UserIssuedCredentialsItem;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primarySortKey;

public class IpvSessionService {

    private final DataStore<IpvSessionItem> dataStore;
    private final ConfigurationService configurationService;

    static final TableSchema<IpvSessionItem> IPV_SESSION_ITEM_TABLE_SCHEMA = StaticTableSchema.builder(IpvSessionItem.class)
            .newItemSupplier(IpvSessionItem::new)
            .addAttribute(String.class, a -> a.name("ipvSessionId")
                    .getter(IpvSessionItem::getIpvSessionId)
                    .setter(IpvSessionItem::setIpvSessionId)
                    .tags(primaryPartitionKey()))
            .addAttribute(String.class, a -> a.name("creationDateTime")
                    .getter(IpvSessionItem::getCreationDateTime)
                    .setter(IpvSessionItem::setCreationDateTime))
            .build();

    @ExcludeFromGeneratedCoverageReport
    public IpvSessionService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        boolean isRunningLocally = this.configurationService.isRunningLocally();
        dataStore =
                new DataStore<>(
                        this.configurationService.getIpvSessionTableName(),
                        IPV_SESSION_ITEM_TABLE_SCHEMA,
                        DataStore.getClient(isRunningLocally),
                        isRunningLocally);
    }

    public IpvSessionService(
            DataStore<IpvSessionItem> dataStore, ConfigurationService configurationService) {
        this.dataStore = dataStore;
        this.configurationService = configurationService;
    }

    public IpvSessionItem getIpvSession(String ipvSessionId) {
        return dataStore.getItem(ipvSessionId);
    }

    public String generateIpvSession() {
        IpvSessionItem ipvSessionItem = new IpvSessionItem();
        ipvSessionItem.setIpvSessionId(UUID.randomUUID().toString());
        ipvSessionItem.setUserState(UserStates.INITIAL_IPV_JOURNEY.toString());
        ipvSessionItem.setCreationDateTime(Instant.now().toString());
        dataStore.create(ipvSessionItem);

        return ipvSessionItem.getIpvSessionId();
    }

    public void updateIpvSession(IpvSessionItem updatedIpvSessionItem) {
        dataStore.update(updatedIpvSessionItem);
    }
}
