package pl.usos.usossystem.service;

import pl.usos.usossystem.model.Semestr;

public interface SemesterGateway {
    Semestr getSemestrById(int semestrId);

    Semestr getNextSemestr(int currentNumer);
}
