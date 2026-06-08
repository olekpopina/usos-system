#!/usr/bin/env python3
"""
Wypelnia baze `usos_db` testowymi studentami i ocenami.

Wymaga:
    pip install mysql-connector-python

Przyklad:
    python scripts/seed_test_data.py
"""

from __future__ import annotations

import argparse
import sys
from dataclasses import dataclass

try:
    import mysql.connector
    from mysql.connector import Error
except ImportError as exc:  # pragma: no cover - helper message for local usage
    print("Brakuje pakietu mysql-connector-python. Zainstaluj go poleceniem:")
    print("pip install mysql-connector-python")
    raise SystemExit(1) from exc


@dataclass(frozen=True)
class StudentSeed:
    indeks: int
    imie: str
    nazwisko: str
    semestr_numer: int
    grades: list[float | None]


TEST_STUDENTS = [
    StudentSeed(910001, "Jan", "Kowalski", 1, [5.0, 4.0, 3.0]),
    StudentSeed(910002, "Anna", "Nowak", 1, [5.0, 2.0, 3.0]),
    StudentSeed(910003, "Piotr", "Wisniewski", 1, [2.0, 2.0, 4.0]),
    StudentSeed(910004, "Maria", "Wojcik", 1, [5.0, None, 3.0]),
    StudentSeed(910005, "Kamil", "Lewandowski", 2, [4.5, 4.0, 3.5]),
    StudentSeed(910006, "Ola", "Kaminska", 2, [5.0, 2.0, 3.0]),
    StudentSeed(910007, "Tomasz", "Zielinski", 2, [2.0, 2.0, 4.0]),
    StudentSeed(910008, "Julia", "Mazur", 2, [4.0, None, 5.0]),
]


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Seed test data for mini-USOS")
    parser.add_argument("--host", default="127.0.0.1")
    parser.add_argument("--port", type=int, default=3306)
    parser.add_argument("--user", default="root")
    parser.add_argument("--password", default="usos")
    parser.add_argument("--database", default="usos_db")
    return parser.parse_args()


def calculate_status(grades: list[float | None], ects_values: list[int]) -> str:
    missing_grades = sum(grade is None for grade in grades)
    if missing_grades > 0:
        return "W trakcie"

    failed_subjects = sum(grade < 3.0 for grade in grades if grade is not None)
    if failed_subjects == 0:
        return "Zaliczony"

    earned_ects = sum(ects for grade, ects in zip(grades, ects_values, strict=True) if grade is not None and grade >= 3.0)
    required_ects = sum(ects_values)
    conditional_threshold = max(0, required_ects - max(ects_values, default=0))

    if failed_subjects == 1 and earned_ects >= conditional_threshold:
        return "Warunkowy"

    return "Niezaliczony"


def main() -> int:
    args = parse_args()

    try:
        connection = mysql.connector.connect(
            host=args.host,
            port=args.port,
            user=args.user,
            password=args.password,
            database=args.database,
        )
    except Error as exc:
        print(f"Nie udalo sie polaczyc z baza: {exc}")
        return 1

    try:
        connection.start_transaction()
        with connection.cursor(dictionary=True) as cursor:
            cursor.execute("SELECT id, numer FROM semestr ORDER BY numer")
            semestr_map = {row["numer"]: row["id"] for row in cursor.fetchall()}

            cursor.execute("""
                SELECT sp.semestr_id, p.id AS przedmiot_id, p.nazwa, p.ects
                FROM semestr_przedmiot sp
                JOIN przedmiot p ON p.id = sp.przedmiot_id
                JOIN semestr s ON s.id = sp.semestr_id
                ORDER BY s.numer, sp.przedmiot_id
            """)
            subjects_by_semestr: dict[int, list[dict[str, int | str]]] = {}
            for row in cursor.fetchall():
                subjects_by_semestr.setdefault(row["semestr_id"], []).append(row)

            for student in TEST_STUDENTS:
                semestr_id = semestr_map.get(student.semestr_numer)
                if semestr_id is None:
                    raise RuntimeError(f"Brak semestru nr {student.semestr_numer} w bazie.")

                subject_rows = subjects_by_semestr.get(semestr_id, [])
                if len(subject_rows) < len(student.grades):
                    raise RuntimeError(
                        f"Semestr {student.semestr_numer} ma za malo przypisanych przedmiotow do seedowania."
                    )

                used_subjects = subject_rows[:len(student.grades)]
                ects_values = [int(subject["ects"]) for subject in used_subjects]
                status = calculate_status(student.grades, ects_values)

                cursor.execute("DELETE FROM student WHERE indeks = %s", (student.indeks,))
                cursor.execute(
                    """
                    INSERT INTO student (imie, nazwisko, indeks, haslo, aktualny_semestr_id, status_semestru)
                    VALUES (%s, %s, %s, %s, %s, %s)
                    """,
                    (
                        student.imie,
                        student.nazwisko,
                        student.indeks,
                        "student",
                        semestr_id,
                        status,
                    ),
                )
                student_id = cursor.lastrowid

                for subject, grade in zip(used_subjects, student.grades, strict=True):
                    przedmiot_id = int(subject["przedmiot_id"])
                    cursor.execute(
                        """
                        INSERT INTO student_przedmiot (student_id, przedmiot_id, semestr_id, zaliczony)
                        VALUES (%s, %s, %s, %s)
                        ON DUPLICATE KEY UPDATE zaliczony = VALUES(zaliczony)
                        """,
                        (student_id, przedmiot_id, semestr_id, grade is not None and grade >= 3.0),
                    )

                    if grade is not None:
                        cursor.execute(
                            """
                            INSERT INTO ocena (student_id, przedmiot_id, semestr_id, ocena)
                            VALUES (%s, %s, %s, %s)
                            ON DUPLICATE KEY UPDATE ocena = VALUES(ocena)
                            """,
                            (student_id, przedmiot_id, semestr_id, grade),
                        )

        connection.commit()
        print("Dodano 8 testowych studentow i oceny do usos_db.")
        return 0

    except Exception as exc:  # pragma: no cover - local helper script
        connection.rollback()
        print(f"Seedowanie nie powiodlo sie: {exc}")
        return 1
    finally:
        connection.close()


if __name__ == "__main__":
    sys.exit(main())
