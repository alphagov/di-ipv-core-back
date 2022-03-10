package uk.gov.di.ipv.core.library.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class SharedAttributesResponseNew {

    private final List<Map<String,List<NameParts>>> name ;
    private final Set<BirthDate> birthDate;
    private final Set<Address> address;

    public SharedAttributesResponseNew(List<Map<String,List<NameParts>>> name, Set<BirthDate> birthDate, Set<Address> address) {
        this.name = name;
        this.birthDate = birthDate;
        this.address = address;
    }

    public Optional<List<Map<String,List<NameParts>>>> getName() {

        return Optional.ofNullable(name);
    }

    public Set<BirthDate> getBirthDate() {
        return birthDate;
    }

    public Set<Address> getAddress() {
        return address;
    }


    public static SharedAttributesResponseNew from(List<SharedAttributesNew> sharedAttributes) {
        List<Map<String,List<NameParts>>> name = new ArrayList<>();
        Set<BirthDate> birthDate = new HashSet<>();
        Set<Address> address =  new HashSet<>();;

        sharedAttributes.forEach(
                sharedAttribute -> {
                    sharedAttribute.getName().ifPresent(name::addAll);
                    sharedAttribute.getBirthDate().ifPresent(birthDate::addAll);
                    sharedAttribute.getAddress().ifPresent(address::addAll);
                });

        return new SharedAttributesResponseNew(name, birthDate, address);
    }
}
