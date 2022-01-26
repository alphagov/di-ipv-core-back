package uk.gov.di.ipv.core.library.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = SharedAttributesDeserializer.class)
public class SharedAttributes {
    private List<String> givenNames;
    private String familyName;
    private String dateOfBirth;

    private Map<String, String> address;
    private List<Map<String, String>> addressHistory;

    private SharedAttributes() {}

    public SharedAttributes(
            List<String> forenames,
            String familyName,
            String dateOfBirth,
            Map<String, String> address,
            List<Map<String, String>> addressHistory) {
        this.givenNames = forenames;
        this.familyName = familyName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.addressHistory = addressHistory;
    }

    public static SharedAttributes empty() {
        return new SharedAttributes();
    }

    public Optional<List<String>> getGivenNames() {
        return Optional.ofNullable(givenNames);
    }

    public Optional<String> getFamilyName() {
        return Optional.ofNullable(familyName);
    }

    public Optional<String> getDateOfBirth() {
        return Optional.ofNullable(dateOfBirth);
    }

    public Optional<Map<String, String>> getAddress() {
        return Optional.ofNullable(address);
    }

    public Optional<List<Map<String, String>>> getAddressHistory() {
        return Optional.ofNullable(addressHistory);
    }

    public static class Builder {

        private List<String> givenNames;
        private String familyName;
        private String dateOfBirth;
        private Map<String, String> address;
        private List<Map<String, String>> addressHistory;

        public Builder setGivenNames(List<String> givenNames) {
            this.givenNames = givenNames;
            return this;
        }

        public Builder setFamilyName(String familyName) {
            this.familyName = familyName;
            return this;
        }

        public Builder setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder setAddress(Map<String, String> address) {
            this.address = address;
            return this;
        }

        public Builder setAddressHistory(List<Map<String, String>> addressHistory) {
            this.addressHistory = addressHistory;
            return this;
        }

        public SharedAttributes build() {
            return new SharedAttributes(
                    givenNames, familyName, dateOfBirth, address, addressHistory);
        }
    }
}
