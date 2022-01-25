package uk.gov.di.ipv.core.library.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SharedAttributesDto {
    private final String name;

    public SharedAttributesDto(@JsonProperty(value = "name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
