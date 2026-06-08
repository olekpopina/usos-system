#!/usr/bin/env python3
"""
Usuwa baze `usos_db`, odtwarza ja z `database/init.sql`
i opcjonalnie seeduje testowe dane.

Przyklad:
    python scripts/reset_database.py --force
    python scripts/reset_database.py --force --seed
"""

from __future__ import annotations

import argparse
import subprocess
import sys
from pathlib import Path

try:
    import mysql.connector
    from mysql.connector import Error
except ImportError as exc:  # pragma: no cover - helper message for local usage
    print("Brakuje pakietu mysql-connector-python. Zainstaluj go poleceniem:")
    print("pip install mysql-connector-python")
    raise SystemExit(1) from exc


ROOT_DIR = Path(__file__).resolve().parents[1]
INIT_SQL_PATH = ROOT_DIR / "database" / "init.sql"
SEED_SCRIPT_PATH = Path(__file__).resolve().with_name("seed_test_data.py")


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Reset bazy danych mini-USOS")
    parser.add_argument("--host", default="127.0.0.1")
    parser.add_argument("--port", type=int, default=3306)
    parser.add_argument("--user", default="root")
    parser.add_argument("--password", default="usos")
    parser.add_argument("--database", default="usos_db")
    parser.add_argument("--seed", action="store_true", help="Po resecie uruchom skrypt z testowymi danymi.")
    parser.add_argument(
        "--force",
        action="store_true",
        help="Potwierdza usuniecie calej bazy danych przed odtworzeniem.",
    )
    return parser.parse_args()


def split_sql_statements(sql_script: str) -> list[str]:
    statements: list[str] = []
    current: list[str] = []

    for line in sql_script.splitlines():
        stripped = line.strip()
        if not stripped or stripped.startswith("--"):
            continue

        current.append(line)
        if stripped.endswith(";"):
            statement = "\n".join(current).strip()
            statements.append(statement[:-1])
            current.clear()

    if current:
        statements.append("\n".join(current).strip())

    return statements


def main() -> int:
    args = parse_args()

    if not args.force:
        print("Ten skrypt usuwa cala baze danych. Uruchom go z --force, jesli tego chcesz.")
        return 1

    if not INIT_SQL_PATH.exists():
        print(f"Nie znaleziono pliku init.sql: {INIT_SQL_PATH}")
        return 1

    try:
        connection = mysql.connector.connect(
            host=args.host,
            port=args.port,
            user=args.user,
            password=args.password,
        )
    except Error as exc:
        print(f"Nie udalo sie polaczyc z serwerem MySQL: {exc}")
        return 1

    try:
        with connection.cursor() as cursor:
            cursor.execute(f"DROP DATABASE IF EXISTS `{args.database}`")

            sql_script = INIT_SQL_PATH.read_text(encoding="utf-8")
            for statement in split_sql_statements(sql_script):
                cursor.execute(statement)

        connection.commit()
        print(f"Baza {args.database} zostala utworzona od nowa.")
    except Exception as exc:  # pragma: no cover - helper script
        connection.rollback()
        print(f"Reset bazy nie powiodl sie: {exc}")
        return 1
    finally:
        connection.close()

    if args.seed:
        command = [
            sys.executable,
            str(SEED_SCRIPT_PATH),
            "--host",
            args.host,
            "--port",
            str(args.port),
            "--user",
            args.user,
            "--password",
            args.password,
            "--database",
            args.database,
        ]
        result = subprocess.run(command, cwd=ROOT_DIR)
        return result.returncode

    return 0


if __name__ == "__main__":
    sys.exit(main())
