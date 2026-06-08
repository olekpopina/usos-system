package pl.usos.usossystem.service;

import pl.usos.usossystem.model.SemesterProgressView;
import pl.usos.usossystem.model.StudentCourseRecord;

import java.util.List;

public class SemesterCompletionService {

    public boolean isSubjectPassed(Double grade) {
        return grade != null && grade >= 3.0;
    }

    public int calculateRequiredEcts(List<StudentCourseRecord> courseRecords) {
        return courseRecords.stream()
                .mapToInt(StudentCourseRecord::getEcts)
                .sum();
    }

    public int calculateEarnedEcts(List<StudentCourseRecord> courseRecords) {
        return courseRecords.stream()
                .filter(StudentCourseRecord::isZaliczony)
                .mapToInt(StudentCourseRecord::getEcts)
                .sum();
    }

    public int calculateFailedSubjectsCount(List<StudentCourseRecord> courseRecords) {
        return (int) courseRecords.stream()
                .filter(record -> record.getOcena() != null && !record.isZaliczony())
                .count();
    }

    public int calculateMissingGradesCount(List<StudentCourseRecord> courseRecords) {
        return (int) courseRecords.stream()
                .filter(record -> record.getOcena() == null)
                .count();
    }

    public int calculateConditionalEctsThreshold(List<StudentCourseRecord> courseRecords) {
        int requiredEcts = calculateRequiredEcts(courseRecords);
        int maxSingleSubjectEcts = courseRecords.stream()
                .mapToInt(StudentCourseRecord::getEcts)
                .max()
                .orElse(0);
        return Math.max(0, requiredEcts - maxSingleSubjectEcts);
    }

    public SemesterStatus evaluateStatus(List<StudentCourseRecord> courseRecords) {
        if (courseRecords.isEmpty()) {
            return SemesterStatus.W_TRAKCIE;
        }

        int missingGrades = calculateMissingGradesCount(courseRecords);
        if (missingGrades > 0) {
            return SemesterStatus.W_TRAKCIE;
        }

        int failedSubjects = calculateFailedSubjectsCount(courseRecords);
        if (failedSubjects == 0) {
            return SemesterStatus.ZALICZONY;
        }

        int earnedEcts = calculateEarnedEcts(courseRecords);
        int conditionalThreshold = calculateConditionalEctsThreshold(courseRecords);
        if (failedSubjects == 1 && earnedEcts >= conditionalThreshold) {
            return SemesterStatus.WARUNKOWY;
        }

        return SemesterStatus.NIEZALICZONY;
    }

    public boolean canRegisterNextSemester(SemesterProgressView progressView) {
        return progressView.getStatus().canRegisterNextSemester();
    }
}
