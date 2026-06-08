CREATE DATABASE IF NOT EXISTS usos_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE usos_db;

CREATE TABLE IF NOT EXISTS semestr (
    id INT AUTO_INCREMENT PRIMARY KEY,
    numer INT NOT NULL UNIQUE,
    nazwa VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS przedmiot (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nazwa VARCHAR(150) NOT NULL UNIQUE,
    ects INT NOT NULL
);

CREATE TABLE IF NOT EXISTS student (
    id INT AUTO_INCREMENT PRIMARY KEY,
    imie VARCHAR(100) NOT NULL,
    nazwisko VARCHAR(100) NOT NULL,
    indeks INT NOT NULL UNIQUE,
    haslo VARCHAR(100) NOT NULL,
    aktualny_semestr_id INT NULL,
    status_semestru VARCHAR(100) NULL,
    CONSTRAINT fk_student_semestr
        FOREIGN KEY (aktualny_semestr_id) REFERENCES semestr(id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS semestr_przedmiot (
    semestr_id INT NOT NULL,
    przedmiot_id INT NOT NULL,
    PRIMARY KEY (semestr_id, przedmiot_id),
    CONSTRAINT fk_sp_semestr
        FOREIGN KEY (semestr_id) REFERENCES semestr(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_sp_przedmiot
        FOREIGN KEY (przedmiot_id) REFERENCES przedmiot(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS student_przedmiot (
    student_id INT NOT NULL,
    przedmiot_id INT NOT NULL,
    semestr_id INT NOT NULL,
    zaliczony BOOLEAN NOT NULL DEFAULT FALSE,
    PRIMARY KEY (student_id, przedmiot_id, semestr_id),
    CONSTRAINT fk_stpr_student
        FOREIGN KEY (student_id) REFERENCES student(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_stpr_przedmiot
        FOREIGN KEY (przedmiot_id) REFERENCES przedmiot(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_stpr_semestr
        FOREIGN KEY (semestr_id) REFERENCES semestr(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS ocena (
    id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    przedmiot_id INT NOT NULL,
    semestr_id INT NOT NULL,
    ocena DECIMAL(3,1) NOT NULL,
    UNIQUE KEY uq_ocena (student_id, przedmiot_id, semestr_id),
    CONSTRAINT fk_ocena_student
        FOREIGN KEY (student_id) REFERENCES student(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_ocena_przedmiot
        FOREIGN KEY (przedmiot_id) REFERENCES przedmiot(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_ocena_semestr
        FOREIGN KEY (semestr_id) REFERENCES semestr(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

INSERT INTO semestr (numer, nazwa) VALUES
    (1, 'Semestr 1'),
    (2, 'Semestr 2'),
    (3, 'Semestr 3'),
    (4, 'Semestr 4'),
    (5, 'Semestr 5'),
    (6, 'Semestr 6')
ON DUPLICATE KEY UPDATE nazwa = VALUES(nazwa);

INSERT INTO przedmiot (nazwa, ects) VALUES
    ('Matematyka', 6),
    ('Programowanie', 5),
    ('Bazy danych', 5),
    ('Algorytmy', 4),
    ('Sieci komputerowe', 4),
    ('Inzynieria oprogramowania', 5),
    ('Statystyka', 4),
    ('Systemy operacyjne', 5),
    ('Architektura komputerow', 4),
    ('Java', 5),
    ('Analiza matematyczna', 6),
    ('Metody numeryczne', 4),
    ('Aplikacje webowe', 5),
    ('Programowanie mobilne', 4),
    ('Bezpieczenstwo sieci', 4),
    ('Sztuczna inteligencja', 5),
    ('Hurtownie danych', 4),
    ('Projekt zespolowy', 6)
ON DUPLICATE KEY UPDATE
    ects = VALUES(ects);

INSERT INTO semestr_przedmiot (semestr_id, przedmiot_id)
SELECT s.id, p.id
FROM semestr s
JOIN przedmiot p
WHERE (s.numer = 1 AND p.nazwa IN ('Matematyka', 'Programowanie', 'Bazy danych'))
   OR (s.numer = 2 AND p.nazwa IN ('Algorytmy', 'Sieci komputerowe', 'Inzynieria oprogramowania'))
   OR (s.numer = 3 AND p.nazwa IN ('Statystyka', 'Systemy operacyjne', 'Architektura komputerow'))
   OR (s.numer = 4 AND p.nazwa IN ('Java', 'Analiza matematyczna', 'Metody numeryczne'))
   OR (s.numer = 5 AND p.nazwa IN ('Aplikacje webowe', 'Programowanie mobilne', 'Bezpieczenstwo sieci'))
   OR (s.numer = 6 AND p.nazwa IN ('Sztuczna inteligencja', 'Hurtownie danych', 'Projekt zespolowy'))
ON DUPLICATE KEY UPDATE
    przedmiot_id = VALUES(przedmiot_id);
