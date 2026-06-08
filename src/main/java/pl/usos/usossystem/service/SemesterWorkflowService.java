package pl.usos.usossystem.service;

import pl.usos.usossystem.model.Semestr;
import pl.usos.usossystem.model.SemesterProgressView;
import pl.usos.usossystem.model.Student;
import pl.usos.usossystem.model.StudentCourseRecord;

import java.util.List;

public class SemesterWorkflowService {
    private final StudentGateway studentGateway;
    private final SemesterGateway semesterGateway;
    private final StudentSemesterGateway studentSemesterGateway;
    private final GradeGateway gradeGateway;
    private final SemesterCompletionService completionService;

    public SemesterWorkflowService(
            StudentGateway studentGateway,
            SemesterGateway semesterGateway,
            StudentSemesterGateway studentSemesterGateway,
            GradeGateway gradeGateway,
            SemesterCompletionService completionService
    ) {
        this.studentGateway = studentGateway;
        this.semesterGateway = semesterGateway;
        this.studentSemesterGateway = studentSemesterGateway;
        this.gradeGateway = gradeGateway;
        this.completionService = completionService;
    }

    public SemesterProgressView assignCurrentSemester(int studentId, int semestrId) {
        studentGateway.setStudentSemestr(studentId, semestrId);
        studentSemesterGateway.przypiszPrzedmiotySemestruStudentowi(studentId, semestrId);
        studentSemesterGateway.synchronizujZaliczeniePrzedmiotow(studentId, semestrId);
        return recalculateAndPersist(studentId, semestrId);
    }

    public SemesterProgressView repairSemesterAssignments(int studentId, int semestrId) {
        studentSemesterGateway.przypiszPrzedmiotySemestruStudentowi(studentId, semestrId);
        studentSemesterGateway.synchronizujZaliczeniePrzedmiotow(studentId, semestrId);
        return recalculateAndPersist(studentId, semestrId);
    }

    public SemesterProgressView saveGradeAndRecalculate(int studentId, int semestrId, int przedmiotId, double ocena) {
        gradeGateway.addOcena(studentId, przedmiotId, semestrId, ocena);
        studentSemesterGateway.synchronizujZaliczeniePrzedmiotow(studentId, semestrId);
        return recalculateAndPersist(studentId, semestrId);
    }

    public SemesterProgressView markCurrentSemesterPassedManually(int studentId) {
        Student student = requireStudent(studentId);
        if (student.getAktualnySemestrId() == null) {
            throw new IllegalStateException("Student nie ma ustawionego aktualnego semestru.");
        }

        studentGateway.setStatusSemestru(studentId, SemesterStatus.ZALICZONY);
        return getCurrentSemesterProgress(studentId);
    }

    public SemesterProgressView getCurrentSemesterProgress(int studentId) {
        Student student = requireStudent(studentId);
        if (student.getAktualnySemestrId() == null) {
            return new SemesterProgressView(
                    studentId,
                    0,
                    "Brak",
                    0,
                    0,
                    0,
                    0,
                    0,
                    SemesterStatus.W_TRAKCIE,
                    false,
                    List.of()
            );
        }

        return buildProgressView(studentId, student.getAktualnySemestrId());
    }

    public SemesterProgressView registerForNextSemester(int studentId) {
        Student student = requireStudent(studentId);
        if (student.getAktualnySemestrId() == null) {
            throw new IllegalStateException("Student nie ma ustawionego aktualnego semestru.");
        }

        SemesterProgressView currentProgress = buildProgressView(studentId, student.getAktualnySemestrId());
        if (!completionService.canRegisterNextSemester(currentProgress)) {
            throw new IllegalStateException("Student nie spelnia warunkow rejestracji na kolejny semestr.");
        }

        Semestr currentSemestr = semesterGateway.getSemestrById(student.getAktualnySemestrId());
        if (currentSemestr == null) {
            throw new IllegalStateException("Nie znaleziono aktualnego semestru studenta.");
        }

        Semestr nextSemestr = semesterGateway.getNextSemestr(currentSemestr.getNumer());
        if (nextSemestr == null) {
            throw new IllegalStateException("Brak kolejnego semestru w bazie.");
        }

        return assignCurrentSemester(studentId, nextSemestr.getId());
    }

    public List<StudentCourseRecord> getStudentCourseHistory(int studentId) {
        return studentSemesterGateway.getStudentCourseRecords(studentId);
    }

    private SemesterProgressView recalculateAndPersist(int studentId, int semestrId) {
        SemesterProgressView progressView = buildProgressView(studentId, semestrId);
        studentGateway.setStatusSemestru(studentId, progressView.getStatus());
        return buildProgressView(studentId, semestrId);
    }

    private SemesterProgressView buildProgressView(int studentId, int semestrId) {
        List<StudentCourseRecord> records = studentSemesterGateway.getStudentCourseRecordsForSemester(studentId, semestrId);
        Student student = studentGateway.getStudentById(studentId);
        Semestr semestr = semesterGateway.getSemestrById(semestrId);
        String semestrNazwa = semestr == null ? "Brak" : semestr.getNazwa();
        int requiredEcts = completionService.calculateRequiredEcts(records);
        int earnedEcts = completionService.calculateEarnedEcts(records);
        int conditionalThreshold = completionService.calculateConditionalEctsThreshold(records);
        int failedSubjectsCount = completionService.calculateFailedSubjectsCount(records);
        int missingGradesCount = completionService.calculateMissingGradesCount(records);
        SemesterStatus computedStatus = completionService.evaluateStatus(records);
        SemesterStatus finalStatus = computedStatus;

        if (student != null
                && student.getAktualnySemestrId() != null
                && student.getAktualnySemestrId() == semestrId
                && student.getSemesterStatus() == SemesterStatus.ZALICZONY
                && computedStatus != SemesterStatus.ZALICZONY) {
            finalStatus = SemesterStatus.ZALICZONY;
        }

        return new SemesterProgressView(
                studentId,
                semestrId,
                semestrNazwa,
                requiredEcts,
                earnedEcts,
                conditionalThreshold,
                failedSubjectsCount,
                missingGradesCount,
                finalStatus,
                finalStatus.canRegisterNextSemester(),
                records
        );
    }

    private Student requireStudent(int studentId) {
        Student student = studentGateway.getStudentById(studentId);
        if (student == null) {
            throw new IllegalStateException("Nie znaleziono studenta.");
        }
        return student;
    }
}
