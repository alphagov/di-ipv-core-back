package uk.gov.di.ipv.core.library.domain;

import uk.gov.di.ipv.core.library.annotations.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
public class NameParts {
    private String value;
    private String type;
   /* private String validFrom;
    private String validUntil;*/

   /* NameParts() {

    }*/
   // todo - do we receive validFrom & validUntil
    public NameParts(String value, String type, String validFrom, String validUntil) {
        this.value = value;
        this.type = type;
      /*  this.validFrom = validFrom;
        this.validUntil = validUntil;*/
    }

    public NameParts(String value, String type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

   /* public String getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(String validFrom) {
        this.validFrom = validFrom;
    }

    public String getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(String validUntil) {
        this.validUntil = validUntil;*/


    @Override
    public String toString() {
        return "NameParts{" +
                "value='" + value + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

    //todo for testing 7 needs removing


