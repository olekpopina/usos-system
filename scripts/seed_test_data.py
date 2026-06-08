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
    StudentSeed(910003, "Piotr", "Wisniewski", 1, [2.0, None, 3.0]),
    StudentSeed(910004, "Maria", "Wojcik", 2, [4.5, 4.0, 3.5]),
]


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Seed test data for mini-USOS")
    parser.add_argument("--host", default="127.0.0.1")
    parser.add_argument("--port", type=int, default=3306)
    parser.add_argument("--user", default="root")
    parser.add_argument("--password", default="usos")
    parser.add_argument("--database", default="usos_db")
    return parser.parse_args()


def status_from_grades(grades: list[float | None]) -> str:
    debts = sum(grade is None or grade < 3.0 for grade in grades)
    if debts == 0:
        return "Zaliczony"
    if debts == 1:
        return "Warunkowy"
    return "W trakcie"


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
                SELECT sp.semestr_id, sp.przedmiot_id
                FROM semestr_przedmiot sp
                JOIN semestr s ON s.id = sp.semestr_id
                ORDER BY s.numer, sp.przedmiot_id
            """)
            subjects_by_semestr: dict[int, list[int]] = {}
            for row in cursor.fetchall():
                subjects_by_semestr.setdefault(row["semestr_id"], []).append(row["przedmiot_id"])

            for student in TEST_STUDENTS:
                semestr_id = semestr_map.get(student.semestr_numer)
                if semestr_id is None:
                    raise RuntimeError(f"Brak semestru nr {student.semestr_numer} w bazie.")

                przedmiot_ids = subjects_by_semestr.get(semestr_id, [])
                if len(przedmiot_ids) < len(student.grades):
                    raise RuntimeError(
                        f"Semestr {student.semestr_numer} ma za malo przypisanych przedmiotow do seedowania."
                    )

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
                        status_from_grades(student.grades),
                    ),
                )
                student_id = cursor.lastrowid

                for przedmiot_id, grade in zip(przedmiot_ids, student.grades, strict=True):
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
        print("Dodano testowych studentow i oceny do usos_db.")
        return 0

    except Exception as exc:  # pragma: no cover - local helper script
        connection.rollback()
        print(f"Seedowanie nie powiodlo sie: {exc}")
        return 1
    finally:
        connection.close()


if __name__ == "__main__":
    sys.exit(main())
