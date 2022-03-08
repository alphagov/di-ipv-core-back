package uk.gov.di.ipv.core.library.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SharedAttributesResponseNew {
    private  Set<NameParts> names;
    private  Set<BirthDate> dateOfBirths;
    private Set<Map<String, String>> addresses;
    private Set<Map<String, String>> addressHistory;

    public SharedAttributesResponseNew() {

    }

    public SharedAttributesResponseNew(Set<NameParts> names, Set<BirthDate> dateOfBirths, Set<Map<String, String>> addresses, Set<Map<String, String>> addressHistory) {
        this.names = names;
        this.dateOfBirths = dateOfBirths;
        this.addresses = addresses;
        this.addressHistory = addressHistory;
    }

    public static Set<NameParts> buildNameParts(Set<Name> nameSet) {
        Set<NameParts> nameParts = new HashSet<>();
        // List<JSONObject> givenNames = new ArrayList<>();
        nameSet.forEach(
                name -> {
                    List<String> givenNames = name.getGivenNames();
                    for (String givenName : givenNames) {
                        nameParts.add(new NameParts(givenName, "GivenName"));
                    }
                    for (String familyName : name.getFamilyName().split(",")) {
                        nameParts.add(new NameParts(familyName, "FamilyName"));
                    }
                });
        return nameParts;
    }

    public static Set<BirthDate> buildBirthDates(Set<String> birthday) {
        Set<BirthDate> birthDateList = new HashSet<>();
        birthday.forEach(
                bday ->
                        birthDateList.add(new BirthDate(bday)));

        return birthDateList;
    }
}
