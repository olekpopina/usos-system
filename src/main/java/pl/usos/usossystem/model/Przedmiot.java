package pl.usos.usossystem.model;

public class Przedmiot {
    private int id;
    private String nazwa;
    private int ects;
    private String semestry;

    public Przedmiot(int id, String nazwa, int ects) {
        this(id, nazwa, ects, "-");
    }

    public Przedmiot(int id, String nazwa, int ects, String semestry) {
        this.id = id;
        this.nazwa = nazwa;
        this.ects = ects;
        this.semestry = semestry;
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

    public String getSemestry() {
        return semestry;
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

    public void setSemestry(String semestry) {
        this.semestry = semestry;
    }

    @Override
    public String toString() {
        return nazwa + " (" + ects + " ECTS)";
    }
}
