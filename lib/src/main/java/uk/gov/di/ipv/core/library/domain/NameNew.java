package uk.gov.di.ipv.core.library.domain;

import java.util.List;

public class NameNew {
    private final List<NameParts> nameParts;

    public NameNew(List<NameParts> nameParts) {
        this.nameParts = nameParts;
    }

    public List<NameParts> getNameParts() {
        return nameParts;
    }
}
