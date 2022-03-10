package uk.gov.di.ipv.core.library.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = SharedAttributesDeserializerNew.class)
public class SharedAttributesNew {

    private List<Map<String,List<NameParts>>> name ;
    private Set<BirthDate> birthDate;
    private Set<Address> address;

    private SharedAttributesNew(){

    }

    public SharedAttributesNew(List<Map<String,List<NameParts>>> name, Set<BirthDate> birthDate, Set<Address> address) {
        this.name = name;
        this.birthDate = birthDate;
        this.address = address;
    }

    public static SharedAttributesNew empty() {
        return new SharedAttributesNew();
    }

    public Optional<List<Map<String,List<NameParts>>>> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<Set<BirthDate>> getBirthDate() {
        return Optional.ofNullable(birthDate);
    }

    public Optional<Set<Address>> getAddress() {
        return Optional.ofNullable(address);
    }

    public static class Builder {
        private List<Map<String,List<NameParts>>> name ;
        private Set<BirthDate> birthDate;
        private Set<Address> address;

        public Builder setBirthDate(Set<BirthDate> birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder setAddress(Set<Address> address) {
            this.address = address;
            return this;
        }

        public Builder setName(List<Map<String,List<NameParts>>> name) {
            this.name = name;
            return this;
        }

        public SharedAttributesNew build() {
            return new SharedAttributesNew(name, birthDate, address);
        }
    }
}
