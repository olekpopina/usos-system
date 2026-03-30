package pl.usos.usossystem.model;

public class Student {
    private int id;
    private String imie;
    private String nazwisko;
    private int indeks;

    public Student(int id, String imie, String nazwisko, int indeks) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.indeks = indeks;
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

    public void setId(int id) {
        this.id = id;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public void setIndeks(int indeks) {
        this.indeks = indeks;
    }

    @Override
    public String toString() {
        return imie + " " + nazwisko + " (" + indeks + ")";
    }
}