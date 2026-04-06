package pl.usos.usossystem;

import pl.usos.usossystem.repository.StudentRepository;
import pl.usos.usossystem.model.Student;

import java.util.List;

public class DatabaseTest {
    public static void main(String[] args) {

        StudentRepository repo = new StudentRepository();


        List<Student> students = repo.getAllStudents();

        for (Student s : students) {
            System.out.println(s);
        }
    }
}