package uk.gov.di.ipv.core.library.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import uk.gov.di.ipv.core.library.annotations.ExcludeFromGeneratedCoverageReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@ExcludeFromGeneratedCoverageReport
public class SharedAttributesDeserializerNew extends StdDeserializer<SharedAttributesNew> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    SharedAttributesDeserializerNew() {
        this(null);
    }

    protected SharedAttributesDeserializerNew(Class<?> vc) {
        super(vc);
    }

    @Override
    public SharedAttributesNew deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        SharedAttributesNew.Builder sharedAttributesNewBuilder = new SharedAttributesNew.Builder();

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        if (node.isEmpty()) {
            return SharedAttributesNew.empty();
        }

        JsonNode nameNode = node.get("name");
        if (nameNode != null) {
            List<Map<String, List<NameParts>>> nameList = new ArrayList<>();
            List<NameParts> namePartsList = new ArrayList<>();
            Map<String, List<NameParts>> mapOfName = new HashMap<>();
            for (JsonNode jo : nameNode) {
                JsonNode nameParts = jo.get("nameParts");
                nameParts.forEach(namePart -> namePartsList.add(objectMapper.convertValue(namePart, NameParts.class)));
            }
            mapOfName.put("name", namePartsList);
            nameList.add(mapOfName);
            sharedAttributesNewBuilder.setName(nameList);
        }

        JsonNode dateOfBirth = node.get("birthDate");
        if (dateOfBirth != null) {
            Set<BirthDate> dateList = new HashSet<>();
            for (JsonNode jo : dateOfBirth) {
                dateList.add(objectMapper.convertValue(jo, BirthDate.class));
            }
            sharedAttributesNewBuilder.setBirthDate(dateList);
        }

        JsonNode address = node.get("address");
        if (address != null) {
            Set<Address> addressList = new HashSet<>();
            for (JsonNode jo : address) {
                addressList.add(objectMapper.convertValue(jo, Address.class));
            }
            sharedAttributesNewBuilder.setAddress(addressList);
        }

        return sharedAttributesNewBuilder.build();

    }
}
