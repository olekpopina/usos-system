package pl.usos.usossystem.service;

import org.junit.jupiter.api.Test;
import pl.usos.usossystem.model.StudentCourseRecord;

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
        SemesterStatus decision = service.evaluateStatus(List.of(
                course(1, "Matematyka", 5, 4.0),
                course(2, "Programowanie", 6, 3.0),
                course(3, "Bazy danych", 4, 5.0)
        ));

        assertEquals(SemesterStatus.ZALICZONY, decision);
    }

    @Test
    void semesterWithOneDebtAndEnoughEctsIsConditional() {
        List<StudentCourseRecord> records = List.of(
                course(1, "Matematyka", 5, 4.0),
                course(2, "Programowanie", 4, 3.0),
                course(3, "Fizyka", 4, 2.0)
        );

        assertEquals(13, service.calculateRequiredEcts(records));
        assertEquals(9, service.calculateEarnedEcts(records));
        assertEquals(8, service.calculateConditionalEctsThreshold(records));
        assertEquals(SemesterStatus.WARUNKOWY, service.evaluateStatus(records));
    }

    @Test
    void semesterWithTwoDebtsIsNotCompleted() {
        SemesterStatus decision = service.evaluateStatus(List.of(
                course(1, "Matematyka", 5, 4.0),
                course(2, "Programowanie", 4, 2.0),
                course(3, "Fizyka", 4, 2.0)
        ));

        assertEquals(SemesterStatus.NIEZALICZONY, decision);
    }

    @Test
    void semesterWithMissingGradeStaysInProgress() {
        SemesterStatus decision = service.evaluateStatus(List.of(
                course(1, "Matematyka", 5, 4.0),
                course(2, "Programowanie", 4, null)
        ));

        assertEquals(SemesterStatus.W_TRAKCIE, decision);
    }

    @Test
    void registrationIsAllowedOnlyForCompletedOrConditionalSemester() {
        assertTrue(SemesterStatus.ZALICZONY.canRegisterNextSemester());
        assertTrue(SemesterStatus.WARUNKOWY.canRegisterNextSemester());
        assertFalse(SemesterStatus.NIEZALICZONY.canRegisterNextSemester());
        assertFalse(SemesterStatus.W_TRAKCIE.canRegisterNextSemester());
    }

    private StudentCourseRecord course(int id, String name, int ects, Double grade) {
        return new StudentCourseRecord(id, name, ects, 1, "Semestr 1", grade, service.isSubjectPassed(grade));
    }
}
