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
3. Aplikacja przy pierwszym polaczeniu sama probuje utworzyc baze `usos_db`, tabele i dane startowe z `database/init.sql`.

Jesli chcesz uzyc innych danych logowania do MySQL, zmien ustawienia w `src/main/java/pl/usos/usossystem/config/DatabaseConnection.java`.

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

Skrypt dodaje 8 kont testowych z roznymi statusami:
- student z kompletem ocen
- student warunkowy
- student niezaliczony
- student w trakcie z brakujaca ocena
- dane na semestrze 1 i 2

## Reset bazy danych

Jesli komus baza "rozjechala sie" po starszych wersjach projektu, najprosciej wyczyscic ja i odtworzyc od zera.
Po ostatniej przebudowie workflow semestrow to jest tez zalecany krok przed demo albo wspolnym testowaniem na innym komputerze.

### Wariant 1: Python

```powershell
python scripts/reset_database.py --force
```

Jesli od razu chcesz dorzucic dane testowe:

```powershell
python scripts/reset_database.py --force --seed
```

### Wariant 2: gotowe pliki .bat

- `scripts\reset_db.bat` - resetuje baze od zera
- `scripts\reset_db_with_seed.bat` - resetuje baze i od razu dodaje dane testowe

W obu przypadkach trzeba miec:
- uruchomiony MySQL Server
- zainstalowany pakiet `mysql-connector-python`

## Logowanie

- admin: `admin` / `admin`
- student: `numer_indeksu` / `student`

## Minimalny workflow aplikacji

1. Zaloguj sie jako `admin`.
2. W zakladce `Przedmioty i semestry` przypisz wymagane przedmioty do semestrow.
3. W zakladce `Studenci` dodaj studenta.
4. W zakladce `Przebieg studiow` wybierz studenta i ustaw mu aktualny semestr.
5. Aplikacja automatycznie przypisze studentowi wszystkie wymagane przedmioty tego semestru.
6. Wpisuj oceny dla przedmiotow z aktualnego semestru.
7. System sam przelicza ECTS, status semestru i mozliwosc rejestracji na kolejny semestr.
8. Dla statusu `Zaliczony` albo `Warunkowy` mozna zarejestrowac studenta na kolejny semestr.

## Co zostalo dodane

- ECTS w przedmiotach
- automatyczne przypisanie studentowi przedmiotow po ustawieniu aktualnego semestru
- przypisanie przedmiotow do semestrow z widokiem w panelu admina
- logika zaliczenia semestru:
  - brak wszystkich ocen -> `W trakcie`
  - 0 dlugow -> `Zaliczony`
  - 1 dlug i odpowiedni prog ECTS -> `Warunkowy`
  - 2+ dlugow -> `Niezaliczony`
- przeliczanie `earned ECTS / required ECTS / prog warunkowy`
- testy jednostkowe dla logiki zaliczenia i przebiegu studiow
