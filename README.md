# Mini-USOS

Prosty projekt semestralny JavaFX + MySQL przygotowany pod wymagania na ocene 4.0.

## Wymagania

- JDK 17
- MySQL Server 8.x
- Maven Wrapper z repozytorium (`mvnw.cmd`)

Uwaga: projekt nalezy uruchamiac na JDK 17. Na nowszych wersjach Javy konfiguracja JavaFX moze dzialac niestabilnie.

## Konfiguracja bazy danych

1. Uruchom lokalny serwer MySQL.
2. Domyslne dane polaczenia w aplikacji:
   - host: `127.0.0.1`
   - port: `3306`
   - baza: `usos_db`
   - user: `root`
   - haslo: `usos`
3. Wykonaj skrypt `database/init.sql`.

Jesli chcesz uzyc innych danych logowania do MySQL, zmien ustawienia w `src/main/java/pl/usos/usossystem/config/DatabaseConnection.java`.

Od tej wersji aplikacja przy pierwszym polaczeniu sama probuje utworzyc baze `usos_db` i tabele na podstawie `database/init.sql`, jesli baza jeszcze nie istnieje.

## Uruchomienie

### IntelliJ IDEA

1. Otworz projekt z katalogu `Code/usos-system`.
2. Ustaw Project SDK na JDK 17.
3. Uruchom klase `src/main/java/pl/usos/usossystem/LoginApp.java`.

### Maven

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd javafx:run
```

## Testowe dane

Mozesz dodac gotowych studentow i oceny skryptem Python:

```powershell
pip install mysql-connector-python
python scripts/seed_test_data.py
```

Skrypt dodaje kilka kont testowych z roznymi statusami:
- student z kompletem ocen
- student warunkowy
- student z wiecej niz jednym dlugiem

## Logowanie

- admin: `admin` / `admin`
- student: `numer_indeksu` / `student`

## Co zostalo dodane

- ECTS w przedmiotach
- przypisanie przedmiotow do semestrow z widokiem w panelu admina
- logika zaliczenia semestru:
  - 0 dlugow -> `Zaliczony`
  - 1 dlug -> `Warunkowy`
  - 2+ dlugow -> brak rejestracji na kolejny semestr
- testy jednostkowe dla logiki zaliczenia semestru
