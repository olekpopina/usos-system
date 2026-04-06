package pl.usos.usossystem.model;

public class Semestr {
    private int id;
    private int numer;
    private String nazwa;

    public Semestr(int id, int numer, String nazwa) {
        this.id = id;
        this.numer = numer;
        this.nazwa = nazwa;
    }

    public int getId() {
        return id;
    }

    public int getNumer() {
        return numer;
    }

    public String getNazwa() {
        return nazwa;
    }

    @Override
    public String toString() {
        return nazwa;
    }
}