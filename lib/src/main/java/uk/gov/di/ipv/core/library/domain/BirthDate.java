package uk.gov.di.ipv.core.library.domain;

public class BirthDate {
    private String value;

    public BirthDate() {

    }
    public BirthDate(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
