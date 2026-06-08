package pl.usos.usossystem.service;

import pl.usos.usossystem.model.StudentCourseRecord;

import java.util.List;

public interface StudentSemesterGateway {
    void przypiszPrzedmiotySemestruStudentowi(int studentId, int semestrId);

    void synchronizujZaliczeniePrzedmiotow(int studentId, int semestrId);

    List<StudentCourseRecord> getStudentCourseRecords(int studentId);

    List<StudentCourseRecord> getStudentCourseRecordsForSemester(int studentId, int semestrId);

    int countConfiguredSubjectsForSemester(int semestrId);
}
