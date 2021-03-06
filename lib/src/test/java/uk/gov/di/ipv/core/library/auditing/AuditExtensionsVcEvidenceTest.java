package uk.gov.di.ipv.core.library.auditing;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class AuditExtensionsVcEvidenceTest {

    @Test
    public void shouldInitWithNullEvidence() throws JsonProcessingException {
        var auditExtensions = new AuditExtensionsVcEvidence("http://issuer.example.com", null);
        assertNull(auditExtensions.getEvidence());
    }
}
