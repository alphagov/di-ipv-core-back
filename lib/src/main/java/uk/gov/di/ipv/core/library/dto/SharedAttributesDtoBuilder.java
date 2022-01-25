package uk.gov.di.ipv.core.library.dto;

public class SharedAttributesDtoBuilder {

    private String name;

    public SharedAttributesDtoBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public SharedAttributesDto build() {
        return new SharedAttributesDto(name);
    }
}
