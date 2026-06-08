package pl.usos.usossystem.service;

import java.util.List;
import java.util.Objects;

public class SemesterCompletionService {

    public boolean isSubjectPassed(Double grade) {
        return grade != null && grade >= 3.0;
    }

    public SemesterDecision evaluateSemester(List<Double> grades) {
        long debts = grades.stream()
                .filter(grade -> !isSubjectPassed(grade))
                .count();

        if (debts == 0) {
            return SemesterDecision.ZALICZONY;
        }
        if (debts == 1) {
            return SemesterDecision.WARUNKOWY;
        }
        return SemesterDecision.NIEZALICZONY;
    }

    public boolean canRegisterNextSemester(SemesterDecision decision) {
        return decision == SemesterDecision.ZALICZONY || decision == SemesterDecision.WARUNKOWY;
    }

    public String getStatusForStudent(List<Double> grades) {
        if (grades.isEmpty() || grades.stream().allMatch(Objects::isNull)) {
            return "W trakcie";
        }

        return switch (evaluateSemester(grades)) {
            case ZALICZONY -> "Zaliczony";
            case WARUNKOWY -> "Warunkowy";
            case NIEZALICZONY -> "W trakcie";
        };
    }
}
