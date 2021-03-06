package uk.gov.di.ipv.core.evaluategpg45scores.gpg45;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Gpg45Scores is a collection of values that represent the scores gathered when assessing identity
 * proofing using GPG 45.
 *
 * <p>Objects if this class are immutable.
 */
public class Gpg45Scores implements Comparable<Gpg45Scores> {

    public static final Evidence EV_11 = new Gpg45Scores.Evidence(1, 1);
    public static final Evidence EV_22 = new Gpg45Scores.Evidence(2, 2);
    public static final Evidence EV_32 = new Gpg45Scores.Evidence(3, 2);
    public static final Evidence EV_33 = new Gpg45Scores.Evidence(3, 3);
    public static final Evidence EV_42 = new Gpg45Scores.Evidence(4, 2);
    public static final Evidence EV_43 = new Gpg45Scores.Evidence(4, 3);
    public static final Evidence EV_44 = new Gpg45Scores.Evidence(4, 4);

    private final List<Evidence> evidences;
    private final int activity;
    private final int fraud;
    private final int verification;

    public Gpg45Scores(int strength, int validity, int activity, int fraud, int verification) {
        this.activity = activity;
        this.fraud = fraud;
        this.verification = verification;
        this.evidences = List.of(new Evidence(strength, validity));
    }

    public Gpg45Scores(Evidence evidence, int activity, int fraud, int verification) {
        this.activity = activity;
        this.fraud = fraud;
        this.verification = verification;
        this.evidences = List.of(evidence);
    }

    public Gpg45Scores(
            Evidence evidence1, Evidence evidence2, int activity, int fraud, int verification) {
        this(Arrays.asList(evidence1, evidence2), activity, fraud, verification);
    }

    public Gpg45Scores(
            Evidence evidence1,
            Evidence evidence2,
            Evidence evidence3,
            int activity,
            int fraud,
            int verification) {
        this(Arrays.asList(evidence1, evidence2, evidence3), activity, fraud, verification);
    }

    public Gpg45Scores(List<Evidence> evidence, int activity, int fraud, int verification) {
        this.activity = activity;
        this.fraud = fraud;
        this.verification = verification;
        this.evidences =
                evidence.stream()
                        .sorted(
                                Comparator.comparing(Evidence::strength)
                                        .thenComparing(Evidence::validity)
                                        .reversed())
                        .collect(Collectors.toList());
    }

    public int activity() {
        return activity;
    }

    public int fraud() {
        return fraud;
    }

    public int verification() {
        return verification;
    }

    public List<Evidence> evidences() {
        return evidences;
    }

    public Evidence getEvidence(int index) {
        if (evidences.size() < index) {
            return new Gpg45Scores.Evidence(0, 0);
        }

        return evidences.get(index);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "[" + evidences + ", " + activity + ", " + fraud + ", " + verification + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Gpg45Scores that = (Gpg45Scores) other;
        return activity == that.activity
                && fraud == that.fraud
                && verification == that.verification
                && evidences.equals(that.evidences);
    }

    public Gpg45Scores difference(Gpg45Scores other) {

        return new Gpg45Scores(
                diffEvidence(other),
                other.activity() - activity,
                other.fraud() - fraud,
                other.verification() - verification);
    }

    private List<Gpg45Scores.Evidence> diffEvidence(Gpg45Scores target) {

        var evidenceDiff = new ArrayList<Evidence>();
        var maxEvidence = Math.max(evidences.size(), target.evidences().size());

        for (int i = 0; i < maxEvidence; i++) {
            var sourceEvidence = getEvidence(i);
            var targetEvidence = target.getEvidence(i);

            evidenceDiff.add(
                    new Gpg45Scores.Evidence(
                            targetEvidence.strength() - sourceEvidence.strength(),
                            targetEvidence.validity() - sourceEvidence.validity()));
        }
        return evidenceDiff;
    }

    @Override
    public int hashCode() {
        return Objects.hash(evidences, activity, fraud, verification);
    }

    /**
     * Compares the score to another. A negative value indicates that there is some negative value
     * in the {@code difference} between this score and the other. A positive value indicates that
     * there is some positive value in the {@code difference}. A value of zero indicates that there
     * is no difference.
     *
     * @param other
     * @return integer
     */
    @Override
    public int compareTo(Gpg45Scores other) {
        var diff = difference(other);
        int negativeCount = 0;
        int positiveCount = 0;
        for (Evidence e : diff.evidences()) {
            if (e.strength() < 0) {
                negativeCount += e.strength();
            } else {
                positiveCount += e.strength();
            }
            if (e.validity() < 0) {
                negativeCount += e.validity();
            } else {
                positiveCount += e.validity();
            }
        }

        if (diff.activity() < 0) {
            negativeCount += diff.activity();
        } else {
            positiveCount += diff.activity();
        }

        if (diff.fraud() < 0) {
            negativeCount += diff.fraud();
        } else {
            positiveCount += diff.fraud();
        }

        if (diff.verification() < 0) {
            negativeCount += diff.verification();
        } else {
            positiveCount += diff.verification();
        }

        if (negativeCount < 0) {
            return negativeCount;
        }

        return positiveCount;
    }

    static class Builder {

        private List<Evidence> evidences = new ArrayList<>();
        private int activity;
        private int fraud;
        private int verification;

        public Builder withEvidence(Evidence evidence) {
            this.evidences.add(evidence);
            return this;
        }

        public Builder withEvidences(List<Evidence> evidencesList) {
            this.evidences =
                    Stream.concat(this.evidences.stream(), evidencesList.stream())
                            .collect(Collectors.toList());
            return this;
        }

        public Builder withActivity(int activity) {
            this.activity = activity;
            return this;
        }

        public Builder withFraud(int fraud) {
            this.fraud = fraud;
            return this;
        }

        public Builder withVerification(int verification) {
            this.verification = verification;
            return this;
        }

        public Gpg45Scores build() {
            return new Gpg45Scores(evidences, activity, fraud, verification);
        }
    }

    public static class Evidence {
        private final int strength;
        private final int validity;

        public Evidence(int strength, int validity) {
            this.strength = strength;
            this.validity = validity;
        }

        public int validity() {
            return validity;
        }

        public int strength() {
            return strength;
        }

        @Override
        public String toString() {
            return "" + strength + validity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Evidence evidence = (Evidence) o;
            return strength == evidence.strength && validity == evidence.validity;
        }

        @Override
        public int hashCode() {
            return Objects.hash(strength, validity);
        }
    }
}
