package pl.usos.usossystem.service;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SemesterCompletionServiceTest {

    private final SemesterCompletionService service = new SemesterCompletionService();

    @Test
    void subjectIsPassedForThreeAndHigher() {
        assertTrue(service.isSubjectPassed(3.0));
        assertTrue(service.isSubjectPassed(5.0));
    }

    @Test
    void subjectIsNotPassedForTwoOrMissingGrade() {
        assertFalse(service.isSubjectPassed(2.0));
        assertFalse(service.isSubjectPassed(null));
    }

    @Test
    void semesterWithAllPassedSubjectsIsCompleted() {
        SemesterDecision decision = service.evaluateSemester(List.of(3.0, 4.0, 5.0));

        assertEquals(SemesterDecision.ZALICZONY, decision);
    }

    @Test
    void semesterWithOneDebtIsConditional() {
        SemesterDecision decision = service.evaluateSemester(Arrays.asList(5.0, 4.0, null));

        assertEquals(SemesterDecision.WARUNKOWY, decision);
    }

    @Test
    void semesterWithTwoDebtsIsNotCompleted() {
        SemesterDecision decision = service.evaluateSemester(Arrays.asList(5.0, 2.0, null));

        assertEquals(SemesterDecision.NIEZALICZONY, decision);
    }

    @Test
    void registrationIsAllowedOnlyForCompletedOrConditionalSemester() {
        assertTrue(service.canRegisterNextSemester(SemesterDecision.ZALICZONY));
        assertTrue(service.canRegisterNextSemester(SemesterDecision.WARUNKOWY));
        assertFalse(service.canRegisterNextSemester(SemesterDecision.NIEZALICZONY));
    }
}
