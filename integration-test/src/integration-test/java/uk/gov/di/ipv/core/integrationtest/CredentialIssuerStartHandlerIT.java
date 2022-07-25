package uk.gov.di.ipv.core.integrationtest;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.KeyAttribute;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.oauth2.sdk.ResponseType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.gov.di.ipv.core.library.dto.ClientSessionDetailsDto;
import uk.gov.di.ipv.core.library.persistence.item.IpvSessionItem;

import java.time.Instant;
import java.util.UUID;

public class CredentialIssuerStartHandlerIT {

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper().registerModule(new JavaTimeModule());
    private static Table tableTestHarness;

    @BeforeAll
    public static void setUp() {
        String ipvSessionsTableName = System.getenv("IPV_SESSIONS_TABLE_NAME");
        if (ipvSessionsTableName == null) {
            throw new IllegalArgumentException(
                    "The environment variable 'IPV_SESSIONS_TABLE_NAME' must be provided to run this test");
        }


        AmazonDynamoDB independentClient =
                AmazonDynamoDBClient.builder().withRegion("eu-west-2").build();
        DynamoDB testClient = new DynamoDB(independentClient);
        tableTestHarness = testClient.getTable(ipvSessionsTableName);
    }

    @AfterAll
    public static void deleteTestItems() {
//        for (String id : createdItemIds) {
//            try {
//                tableTestHarness.deleteItem(new KeyAttribute(IPV_SESSION_ID, id));
//            } catch (Exception e) {
//                LOGGER.warn(
//                        String.format(
//                                "Failed to delete test data with %s of %s", IPV_SESSION_ID, id));
//            }
//        }
    }

    @Test
    public void criStartExampleTest() throws JsonProcessingException {
        // setup IpvSession DynamoDB table
        String ipvSessionId = UUID.randomUUID().toString();
        IpvSessionItem ipvSessionItem = new IpvSessionItem();
        ipvSessionItem.setIpvSessionId(ipvSessionId);
        ipvSessionItem.setCreationDateTime(Instant.now().toString());

        ClientSessionDetailsDto clientSessionDetailsDto = new ClientSessionDetailsDto(
                ResponseType.CODE.toString(),
                "orchestrator",
                "http://example.com",
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                false
        );
        ipvSessionItem.setClientSessionDetails(clientSessionDetailsDto);

        Item item = Item.fromJSON(OBJECT_MAPPER.writeValueAsString(ipvSessionItem));
        tableTestHarness.putItem(item);


        // call the handler method with a ipvSessionId as a header value
        // Test outcome
    }
}
