package uk.gov.di.ipv.core.library.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = SharedAttributesDeserializer.class)
public class SharedAttributes {
    private String[] forenames;

    public SharedAttributes(String[] forenames) {
        this.forenames = forenames;
    }

    public String[] getForenames() {
        return forenames;
    }
}
