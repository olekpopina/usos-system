package pl.usos.usossystem.service;

import pl.usos.usossystem.model.Student;

public interface StudentGateway {
    Student getStudentById(int studentId);

    void setStudentSemestr(int studentId, int semestrId);

    void setStatusSemestru(int studentId, SemesterStatus status);
}
