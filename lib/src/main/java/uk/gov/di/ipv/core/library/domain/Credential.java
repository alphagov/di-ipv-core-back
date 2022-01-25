package uk.gov.di.ipv.core.library.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Credential {

    private final String[] forenames;

    @JsonCreator
    public Credential(@JsonProperty(value = "forenames", required = false) String[] forenames) {
        this.forenames = forenames;
    }
}
