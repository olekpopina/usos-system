package pl.usos.usossystem.model;

public class Przedmiot {
    private int id;
    private String nazwa;
    private int ects;

    public Przedmiot(int id, String nazwa, int ects) {
        this.id = id;
        this.nazwa = nazwa;
        this.ects = ects;
    }

    public int getId() {
        return id;
    }

    public String getNazwa() {
        return nazwa;
    }

    public int getEcts() {
        return ects;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public void setEcts(int ects) {
        this.ects = ects;
    }

    @Override
    public String toString() {
        return nazwa + " (" + ects + " ECTS)";
    }
}
