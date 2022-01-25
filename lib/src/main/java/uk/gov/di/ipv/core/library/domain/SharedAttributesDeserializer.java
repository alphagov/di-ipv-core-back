package uk.gov.di.ipv.core.library.domain;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SharedAttributesDeserializer extends StdDeserializer<SharedAttributes> {

    SharedAttributesDeserializer() {
        this(null);
    }

    protected SharedAttributesDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public SharedAttributes deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException, JacksonException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        JsonNode attributes = node.get("attributes");
        JsonNode names = attributes.get("names");
        String familyName = names.get("familyName").asText();
        List<String> givenNames = new ArrayList<>();
        for (JsonNode name : names.get("givenNames")) {
            givenNames.add(name.asText());
        }
        return new SharedAttributes(givenNames.toArray(new String[givenNames.size()]));
    }
}
