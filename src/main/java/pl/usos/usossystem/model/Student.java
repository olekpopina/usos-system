package pl.usos.usossystem.model;

public class Student {
    private int id;
    private String imie;
    private String nazwisko;
    private int indeks;
    private String haslo;
    private Integer aktualnySemestrId;
    private String aktualnySemestrNazwa;
    private String statusSemestru;

    public Student(int id, String imie, String nazwisko, int indeks) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.indeks = indeks;
    }

    public Student(int id, String imie, String nazwisko, int indeks,
                   String haslo, Integer aktualnySemestrId, String statusSemestru) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.indeks = indeks;
        this.haslo = haslo;
        this.aktualnySemestrId = aktualnySemestrId;
        this.statusSemestru = statusSemestru;
    }

    public int getId() {
        return id;
    }

    public String getImie() {
        return imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public int getIndeks() {
        return indeks;
    }

    public String getHaslo() {
        return haslo;
    }

    public Integer getAktualnySemestrId() {
        return aktualnySemestrId;
    }

    public String getAktualnySemestrNazwa() {
        return aktualnySemestrNazwa;
    }

    public String getStatusSemestru() {
        return statusSemestru;
    }

    public void setAktualnySemestrNazwa(String aktualnySemestrNazwa) {
        this.aktualnySemestrNazwa = aktualnySemestrNazwa;
    }

    public void setStatusSemestru(String statusSemestru) {
        this.statusSemestru = statusSemestru;
    }

    @Override
    public String toString() {
        return imie + " " + nazwisko + " (" + indeks + ")";
    }
}