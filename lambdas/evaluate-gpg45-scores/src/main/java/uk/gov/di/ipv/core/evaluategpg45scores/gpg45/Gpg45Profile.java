package uk.gov.di.ipv.core.evaluategpg45scores.gpg45;

import static uk.gov.di.ipv.core.evaluategpg45scores.gpg45.Gpg45Scores.EV_11;
import static uk.gov.di.ipv.core.evaluategpg45scores.gpg45.Gpg45Scores.EV_22;
import static uk.gov.di.ipv.core.evaluategpg45scores.gpg45.Gpg45Scores.EV_32;
import static uk.gov.di.ipv.core.evaluategpg45scores.gpg45.Gpg45Scores.EV_33;
import static uk.gov.di.ipv.core.evaluategpg45scores.gpg45.Gpg45Scores.EV_42;
import static uk.gov.di.ipv.core.evaluategpg45scores.gpg45.Gpg45Scores.EV_43;
import static uk.gov.di.ipv.core.evaluategpg45scores.gpg45.Gpg45Scores.EV_44;

/** Enumeration of all GPG 45 profiles along with their reference Gpg45Scores value. */
public enum Gpg45Profile {
    L1A("L1A", new Gpg45Scores(EV_22, 0, 1, 1)),
    L1B("L1B", new Gpg45Scores(EV_32, 0, 0, 1)),
    L1C("L1C", new Gpg45Scores(EV_11, 3, 2, 2)),
    L2A("L2A", new Gpg45Scores(EV_11, EV_11, 2, 1, 2)),
    L2B("L2B", new Gpg45Scores(EV_11, EV_11, 2, 2, 1)),
    L3A("L3A", new Gpg45Scores(EV_11, EV_11, EV_11, 2, 1, 1)),
    M1A("M1A", new Gpg45Scores(EV_42, 0, 1, 2)),
    M1B("M1B", new Gpg45Scores(EV_32, 1, 2, 2)),
    M1C("M1C", new Gpg45Scores(EV_33, 0, 0, 3)),
    M1D("M1D", new Gpg45Scores(EV_22, 2, 1, 3)),
    M2A("M2A", new Gpg45Scores(EV_22, EV_22, 3, 2, 2)),
    M2B("M2B", new Gpg45Scores(EV_32, EV_22, 1, 1, 2)),
    M2C("M2C", new Gpg45Scores(EV_32, EV_22, 0, 1, 3)),
    M3A("M3A", new Gpg45Scores(EV_22, EV_22, EV_22, 2, 2, 2)),
    H1A("H1A", new Gpg45Scores(EV_43, 0, 1, 3)),
    H1B("H1B", new Gpg45Scores(EV_33, 2, 1, 3)),
    H1C("H1C", new Gpg45Scores(EV_43, 0, 0, 4)),
    H2A("H2A", new Gpg45Scores(EV_22, EV_22, 3, 2, 3)),
    H2B("H2B", new Gpg45Scores(EV_42, EV_32, 0, 2, 3)),
    H2C("H2C", new Gpg45Scores(EV_33, EV_22, 1, 1, 3)),
    H2D("H2D", new Gpg45Scores(EV_33, EV_22, 0, 1, 3)),
    H2E("H2E", new Gpg45Scores(EV_43, EV_33, 0, 0, 3)),
    H3A("H3A", new Gpg45Scores(EV_22, EV_22, EV_22, 2, 2, 3)),
    V1A("V1A", new Gpg45Scores(EV_43, 0, 3, 3)),
    V1B("V1B", new Gpg45Scores(EV_44, 0, 1, 3)),
    V1C("V1C", new Gpg45Scores(EV_43, 1, 1, 4)),
    V1D("V1D", new Gpg45Scores(EV_44, 0, 0, 4)),
    V2A("V2A", new Gpg45Scores(EV_33, EV_33, 3, 2, 3)),
    V2B("V2B", new Gpg45Scores(EV_43, EV_33, 0, 2, 3)),
    V2C("V2C", new Gpg45Scores(EV_43, EV_22, 2, 2, 3)),
    V2D("V2D", new Gpg45Scores(EV_44, EV_44, 0, 0, 3)),
    V3A("V3A", new Gpg45Scores(EV_33, EV_22, EV_22, 3, 3, 3));

    public final String label;
    public final Gpg45Scores scores;

    Gpg45Profile(String label, Gpg45Scores scores) {
        this.label = label;
        this.scores = scores;
    }

    public String getLabel() {
        return label;
    }

    public Gpg45Scores getScores() {
        return scores;
    }

    public String code() {
        return String.format("%1$s%2$s", label, scores.toString());
    }

    public Gpg45Scores difference(Gpg45Scores target) {
        return scores.difference(target);
    }

    /**
     * Checks if that the provided {@code Gpg45Scores} satisfy the {@code Gpg45Scores} for this
     * profile.
     *
     * @param target The {@code Gpg45Scores} to compare.
     * @return {@code true} if the elements in the provided {@code Gpg45Scores} are all equal or
     *     greater than the profile's `{@code Gpg45Scores}.
     */
    public boolean isSatisfiedBy(Gpg45Scores target) {
        return satisfactoryEvidence(target)
                && (scores.activity() <= target.activity())
                && (scores.fraud() <= target.fraud())
                && (scores.verification() <= target.verification());
    }

    private boolean satisfactoryEvidence(Gpg45Scores target) {
        if (scores.evidences().size() > target.evidences().size()) {
            return false;
        }

        for (int i = 0; i < scores.evidences().size(); i++) {
            var sourceEvidence = scores.getEvidence(i);
            var targetEvidence = target.getEvidence(i);

            if ((targetEvidence.strength() < sourceEvidence.strength())
                    || (targetEvidence.validity() < sourceEvidence.validity())) {
                return false;
            }
        }

        return true;
    }
}
