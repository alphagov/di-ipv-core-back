package uk.gov.di.ipv.core.sharedattributes;

import uk.gov.di.ipv.core.library.domain.SharedAttributes;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SharedAttributesResponse {


    private final Set<String> givenNames;
    private final Set<String> familyNames;
    private final Set<String> dateOfBirths;
    private final Set<Map<String, String>> addresses;
    private final Set<Map<String, String>> addressHistory;

    public SharedAttributesResponse(Set<String> givenNames,
                                    Set<String> familyNames,
                                    Set<String> dateOfBirths,
                                    Set<Map<String, String>> addresses,
                                    Set<Map<String, String>> addressHistory) {
        this.givenNames = givenNames;
        this.familyNames = familyNames;
        this.dateOfBirths = dateOfBirths;
        this.addresses = addresses;
        this.addressHistory = addressHistory;
    }

    public static SharedAttributesResponse from(List<SharedAttributes> sharedAttributes) {
        Set<String> givenNames = new HashSet<>();
        Set<String> familyNames = new HashSet<>();
        Set<String> dateOfBirths = new HashSet<>();
        Set<Map<String, String>> addresses = new HashSet<>();
        Set<Map<String,String>> addressHistory = new HashSet<>();
        sharedAttributes.forEach(sharedAttribute -> {
            sharedAttribute.getGivenNames().map(givenNames::addAll);
            sharedAttribute.getFamilyName().map(familyNames::add);
            sharedAttribute.getDateOfBirth().map(dateOfBirths::add);
            sharedAttribute.getAddress().map(addresses::add);
            sharedAttribute.getAddressHistory().map(addressHistory::addAll);
        } );

        return new SharedAttributesResponse(givenNames, familyNames, dateOfBirths, addresses, addressHistory);
    }

    public Set<String> getGivenNames() {
        return givenNames;
    }

    public Set<String> getFamilyNames() {
        return familyNames;
    }

    public Set<String> getDateOfBirths() {
        return dateOfBirths;
    }

    public Set<Map<String, String>> getAddresses() {
        return addresses;
    }

    public Set<Map<String, String>> getAddressHistory() {
        return addressHistory;
    }
}
