package pl.usos.usossystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.usos.usossystem.model.Semestr;
import pl.usos.usossystem.model.SemesterProgressView;
import pl.usos.usossystem.model.Student;
import pl.usos.usossystem.model.StudentCourseRecord;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SemesterWorkflowServiceTest {

    private FakeStudentGateway studentGateway;
    private FakeSemesterGateway semesterGateway;
    private FakeStudentSemesterGateway studentSemesterGateway;
    private FakeGradeGateway gradeGateway;
    private SemesterWorkflowService workflowService;

    @BeforeEach
    void setUp() {
        studentGateway = new FakeStudentGateway();
        semesterGateway = new FakeSemesterGateway();
        studentSemesterGateway = new FakeStudentSemesterGateway();
        gradeGateway = new FakeGradeGateway(studentSemesterGateway);
        workflowService = new SemesterWorkflowService(
                studentGateway,
                semesterGateway,
                studentSemesterGateway,
                gradeGateway,
                new SemesterCompletionService()
        );

        studentGateway.put(new Student(1, "Jan", "Kowalski", 123456, "student", null, SemesterStatus.W_TRAKCIE));
        semesterGateway.put(new Semestr(1, 1, "Semestr 1"));
        semesterGateway.put(new Semestr(2, 2, "Semestr 2"));

        studentSemesterGateway.addRequiredCourse(1, 101, "Matematyka", 5);
        studentSemesterGateway.addRequiredCourse(1, 102, "Programowanie", 4);
        studentSemesterGateway.addRequiredCourse(1, 103, "Bazy danych", 4);
        studentSemesterGateway.addRequiredCourse(2, 201, "Algorytmy", 5);
        studentSemesterGateway.addRequiredCourse(2, 202, "Sieci", 4);
    }

    @Test
    void assigningCurrentSemesterAutomaticallyAssignsRequiredSubjects() {
        SemesterProgressView progressView = workflowService.assignCurrentSemester(1, 1);

        assertEquals(1, studentGateway.getStudentById(1).getAktualnySemestrId());
        assertEquals(3, progressView.getCourseRecords().size());
        assertEquals(SemesterStatus.W_TRAKCIE, progressView.getStatus());
        assertEquals(13, progressView.getRequiredEcts());
    }

    @Test
    void allPassedSubjectsProduceCompletedSemester() {
        workflowService.assignCurrentSemester(1, 1);
        workflowService.saveGradeAndRecalculate(1, 1, 101, 4.0);
        workflowService.saveGradeAndRecalculate(1, 1, 102, 3.0);
        SemesterProgressView progressView = workflowService.saveGradeAndRecalculate(1, 1, 103, 5.0);

        assertEquals(SemesterStatus.ZALICZONY, progressView.getStatus());
        assertTrue(progressView.isCanRegisterNextSemester());
    }

    @Test
    void oneFailedSubjectWithEnoughEctsProducesConditionalSemester() {
        workflowService.assignCurrentSemester(1, 1);
        workflowService.saveGradeAndRecalculate(1, 1, 101, 4.0);
        workflowService.saveGradeAndRecalculate(1, 1, 102, 3.0);
        SemesterProgressView progressView = workflowService.saveGradeAndRecalculate(1, 1, 103, 2.0);

        assertEquals(SemesterStatus.WARUNKOWY, progressView.getStatus());
        assertEquals(9, progressView.getEarnedEcts());
        assertEquals(8, progressView.getConditionalEctsThreshold());
        assertTrue(progressView.isCanRegisterNextSemester());
    }

    @Test
    void twoFailedSubjectsProduceNotCompletedSemester() {
        workflowService.assignCurrentSemester(1, 1);
        workflowService.saveGradeAndRecalculate(1, 1, 101, 4.0);
        workflowService.saveGradeAndRecalculate(1, 1, 102, 2.0);
        SemesterProgressView progressView = workflowService.saveGradeAndRecalculate(1, 1, 103, 2.0);

        assertEquals(SemesterStatus.NIEZALICZONY, progressView.getStatus());
        assertEquals(2, progressView.getFailedSubjectsCount());
    }

    @Test
    void missingGradesKeepSemesterInProgress() {
        workflowService.assignCurrentSemester(1, 1);
        SemesterProgressView progressView = workflowService.saveGradeAndRecalculate(1, 1, 101, 5.0);

        assertEquals(SemesterStatus.W_TRAKCIE, progressView.getStatus());
        assertEquals(2, progressView.getMissingGradesCount());
    }

    @Test
    void registeringForNextSemesterAssignsItsRequiredSubjects() {
        workflowService.assignCurrentSemester(1, 1);
        workflowService.saveGradeAndRecalculate(1, 1, 101, 4.0);
        workflowService.saveGradeAndRecalculate(1, 1, 102, 3.0);
        workflowService.saveGradeAndRecalculate(1, 1, 103, 2.0);

        SemesterProgressView nextProgress = workflowService.registerForNextSemester(1);

        assertEquals(2, studentGateway.getStudentById(1).getAktualnySemestrId());
        assertEquals("Semestr 2", nextProgress.getSemestrNazwa());
        assertEquals(2, nextProgress.getCourseRecords().size());
        assertEquals(SemesterStatus.W_TRAKCIE, nextProgress.getStatus());
    }

    @Test
    void registrationIsBlockedWhenCurrentSemesterIsStillInProgress() {
        workflowService.assignCurrentSemester(1, 1);
        workflowService.saveGradeAndRecalculate(1, 1, 101, 4.0);

        assertThrows(IllegalStateException.class, () -> workflowService.registerForNextSemester(1));
    }

    @Test
    void registrationIsBlockedWhenSemesterIsNotCompleted() {
        workflowService.assignCurrentSemester(1, 1);
        workflowService.saveGradeAndRecalculate(1, 1, 101, 4.0);
        workflowService.saveGradeAndRecalculate(1, 1, 102, 2.0);
        workflowService.saveGradeAndRecalculate(1, 1, 103, 2.0);

        assertThrows(IllegalStateException.class, () -> workflowService.registerForNextSemester(1));
    }

    @Test
    void manualSemesterPassOverridesCurrentComputedStatus() {
        workflowService.assignCurrentSemester(1, 1);
        workflowService.saveGradeAndRecalculate(1, 1, 101, 4.0);

        SemesterProgressView progressView = workflowService.markCurrentSemesterPassedManually(1);

        assertEquals(SemesterStatus.ZALICZONY, progressView.getStatus());
        assertTrue(progressView.isCanRegisterNextSemester());
    }

    @Test
    void repairSemesterAssignmentsAddsMissingRequiredSubjects() {
        workflowService.assignCurrentSemester(1, 1);
        studentSemesterGateway.removeCourse(1, 1, 103);

        SemesterProgressView progressView = workflowService.repairSemesterAssignments(1, 1);

        assertEquals(3, progressView.getCourseRecords().size());
        assertTrue(progressView.getCourseRecords().stream()
                .anyMatch(record -> record.getPrzedmiotId() == 103));
    }

    @Test
    void registrationFailsWhenNextSemesterDoesNotExist() {
        workflowService.assignCurrentSemester(1, 2);
        workflowService.saveGradeAndRecalculate(1, 2, 201, 4.0);
        workflowService.saveGradeAndRecalculate(1, 2, 202, 4.0);

        assertThrows(IllegalStateException.class, () -> workflowService.registerForNextSemester(1));
    }

    private static class FakeStudentGateway implements StudentGateway {
        private final Map<Integer, Student> students = new HashMap<>();

        void put(Student student) {
            students.put(student.getId(), student);
        }

        @Override
        public Student getStudentById(int studentId) {
            return students.get(studentId);
        }

        @Override
        public void setStudentSemestr(int studentId, int semestrId) {
            Student current = students.get(studentId);
            students.put(studentId, copyStudent(current, semestrId, SemesterStatus.W_TRAKCIE, null));
        }

        @Override
        public void setStatusSemestru(int studentId, SemesterStatus status) {
            Student current = students.get(studentId);
            students.put(studentId, copyStudent(current, current.getAktualnySemestrId(), status, current.getAktualnySemestrNazwa()));
        }

        private Student copyStudent(Student source, Integer semestrId, SemesterStatus status, String semestrName) {
            Student copy = new Student(
                    source.getId(),
                    source.getImie(),
                    source.getNazwisko(),
                    source.getIndeks(),
                    source.getHaslo(),
                    semestrId,
                    status
            );
            copy.setAktualnySemestrNazwa(semestrName);
            return copy;
        }
    }

    private static class FakeSemesterGateway implements SemesterGateway {
        private final Map<Integer, Semestr> semesters = new HashMap<>();

        void put(Semestr semestr) {
            semesters.put(semestr.getId(), semestr);
        }

        @Override
        public Semestr getSemestrById(int semestrId) {
            return semesters.get(semestrId);
        }

        @Override
        public Semestr getNextSemestr(int currentNumer) {
            return semesters.values().stream()
                    .filter(semestr -> semestr.getNumer() == currentNumer + 1)
                    .findFirst()
                    .orElse(null);
        }
    }

    private static class FakeStudentSemesterGateway implements StudentSemesterGateway {
        private final Map<Integer, List<CourseDefinition>> requiredCourses = new HashMap<>();
        private final Map<Integer, List<StudentCourseRecord>> studentCourses = new HashMap<>();

        void addRequiredCourse(int semesterId, int courseId, String name, int ects) {
            requiredCourses.computeIfAbsent(semesterId, ignored -> new ArrayList<>())
                    .add(new CourseDefinition(courseId, name, ects));
        }

        @Override
        public void przypiszPrzedmiotySemestruStudentowi(int studentId, int semestrId) {
            List<StudentCourseRecord> records = studentCourses.computeIfAbsent(studentId, ignored -> new ArrayList<>());
            for (CourseDefinition definition : requiredCourses.getOrDefault(semestrId, List.of())) {
                boolean exists = records.stream()
                        .anyMatch(record -> record.getSemestrId() == semestrId && record.getPrzedmiotId() == definition.courseId());
                if (!exists) {
                    records.add(new StudentCourseRecord(
                            definition.courseId(),
                            definition.name(),
                            definition.ects(),
                            semestrId,
                            "Semestr " + semestrId,
                            null,
                            false
                    ));
                }
            }
            records.sort(Comparator.comparingInt(StudentCourseRecord::getSemestrId)
                    .thenComparingInt(StudentCourseRecord::getPrzedmiotId));
        }

        @Override
        public void synchronizujZaliczeniePrzedmiotow(int studentId, int semestrId) {
            List<StudentCourseRecord> updated = new ArrayList<>();
            for (StudentCourseRecord record : studentCourses.getOrDefault(studentId, List.of())) {
                if (record.getSemestrId() == semestrId) {
                    boolean passed = record.getOcena() != null && record.getOcena() >= 3.0;
                    updated.add(new StudentCourseRecord(
                            record.getPrzedmiotId(),
                            record.getPrzedmiot(),
                            record.getEcts(),
                            record.getSemestrId(),
                            record.getSemestr(),
                            record.getOcena(),
                            passed
                    ));
                } else {
                    updated.add(record);
                }
            }
            studentCourses.put(studentId, updated);
        }

        @Override
        public List<StudentCourseRecord> getStudentCourseRecords(int studentId) {
            return new ArrayList<>(studentCourses.getOrDefault(studentId, List.of()));
        }

        @Override
        public List<StudentCourseRecord> getStudentCourseRecordsForSemester(int studentId, int semestrId) {
            return studentCourses.getOrDefault(studentId, List.of()).stream()
                    .filter(record -> record.getSemestrId() == semestrId)
                    .toList();
        }

        void upsertGrade(int studentId, int semestrId, int przedmiotId, double grade) {
            List<StudentCourseRecord> updated = new ArrayList<>();
            for (StudentCourseRecord record : studentCourses.getOrDefault(studentId, List.of())) {
                if (record.getSemestrId() == semestrId && record.getPrzedmiotId() == przedmiotId) {
                    updated.add(new StudentCourseRecord(
                            record.getPrzedmiotId(),
                            record.getPrzedmiot(),
                            record.getEcts(),
                            record.getSemestrId(),
                            record.getSemestr(),
                            grade,
                            grade >= 3.0
                    ));
                } else {
                    updated.add(record);
                }
            }
            studentCourses.put(studentId, updated);
        }

        void removeCourse(int studentId, int semestrId, int przedmiotId) {
            List<StudentCourseRecord> updated = studentCourses.getOrDefault(studentId, List.of()).stream()
                    .filter(record -> !(record.getSemestrId() == semestrId && record.getPrzedmiotId() == przedmiotId))
                    .toList();
            studentCourses.put(studentId, new ArrayList<>(updated));
        }
    }

    private static class FakeGradeGateway implements GradeGateway {
        private final FakeStudentSemesterGateway studentSemesterGateway;

        private FakeGradeGateway(FakeStudentSemesterGateway studentSemesterGateway) {
            this.studentSemesterGateway = studentSemesterGateway;
        }

        @Override
        public void addOcena(int studentId, int przedmiotId, int semestrId, double ocena) {
            studentSemesterGateway.upsertGrade(studentId, semestrId, przedmiotId, ocena);
        }
    }

    private record CourseDefinition(int courseId, String name, int ects) {
    }
}
