package uk.gov.di.ipv.core.library.exceptions;

import uk.gov.di.ipv.core.library.annotations.ExcludeFromGeneratedCoverageReport;

@ExcludeFromGeneratedCoverageReport
public class HttpResponseExceptionWithErrorBody extends Exception {
    private final int responseCode;

    public HttpResponseExceptionWithErrorBody(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
