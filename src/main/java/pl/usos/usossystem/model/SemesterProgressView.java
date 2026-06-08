package pl.usos.usossystem.model;

import pl.usos.usossystem.service.SemesterStatus;

import java.util.List;

public class SemesterProgressView {
    private final int studentId;
    private final int semestrId;
    private final String semestrNazwa;
    private final int requiredEcts;
    private final int earnedEcts;
    private final int conditionalEctsThreshold;
    private final int failedSubjectsCount;
    private final int missingGradesCount;
    private final SemesterStatus status;
    private final boolean canRegisterNextSemester;
    private final List<StudentCourseRecord> courseRecords;

    public SemesterProgressView(
            int studentId,
            int semestrId,
            String semestrNazwa,
            int requiredEcts,
            int earnedEcts,
            int conditionalEctsThreshold,
            int failedSubjectsCount,
            int missingGradesCount,
            SemesterStatus status,
            boolean canRegisterNextSemester,
            List<StudentCourseRecord> courseRecords
    ) {
        this.studentId = studentId;
        this.semestrId = semestrId;
        this.semestrNazwa = semestrNazwa;
        this.requiredEcts = requiredEcts;
        this.earnedEcts = earnedEcts;
        this.conditionalEctsThreshold = conditionalEctsThreshold;
        this.failedSubjectsCount = failedSubjectsCount;
        this.missingGradesCount = missingGradesCount;
        this.status = status;
        this.canRegisterNextSemester = canRegisterNextSemester;
        this.courseRecords = courseRecords;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getSemestrId() {
        return semestrId;
    }

    public String getSemestrNazwa() {
        return semestrNazwa;
    }

    public int getRequiredEcts() {
        return requiredEcts;
    }

    public int getEarnedEcts() {
        return earnedEcts;
    }

    public int getConditionalEctsThreshold() {
        return conditionalEctsThreshold;
    }

    public int getFailedSubjectsCount() {
        return failedSubjectsCount;
    }

    public int getMissingGradesCount() {
        return missingGradesCount;
    }

    public SemesterStatus getStatus() {
        return status;
    }

    public String getStatusLabel() {
        return status.getDisplayName();
    }

    public boolean isCanRegisterNextSemester() {
        return canRegisterNextSemester;
    }

    public String getCanRegisterLabel() {
        return canRegisterNextSemester ? "Tak" : "Nie";
    }

    public List<StudentCourseRecord> getCourseRecords() {
        return courseRecords;
    }

    public String getEctsSummary() {
        return earnedEcts + " / " + requiredEcts;
    }
}
