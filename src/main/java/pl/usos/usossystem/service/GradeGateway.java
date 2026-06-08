package pl.usos.usossystem.service;

public interface GradeGateway {
    void addOcena(int studentId, int przedmiotId, int semestrId, double ocena);
}
