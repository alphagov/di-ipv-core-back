package uk.gov.di.ipv.gpg45;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Gpg45ScoresTest {

    @Test
    public void shouldBuild(){
        var scores = new Gpg45Scores.Builder()
                .withEvidence(new Gpg45Scores.Evidence(2, 2))
                .withActivity(1)
                .withFraud(1)
                .withVerification(1)
                .build();
        assertEquals(new Gpg45Scores(2,2,1,1,1), scores);
    }

    @Test
    public void shouldProduceReadableToString(){
        var scores = new Gpg45Scores.Builder()
                .withEvidence(new Gpg45Scores.Evidence(2, 2))
                .withEvidence(new Gpg45Scores.Evidence(3, 2))
                .withActivity(1)
                .withFraud(2)
                .withVerification(3)
                .build();

        assertEquals("[[32, 22], 1, 2, 3]", scores.toString());
    }

}
