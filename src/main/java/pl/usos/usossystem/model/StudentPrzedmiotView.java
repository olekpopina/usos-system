package pl.usos.usossystem.model;

public class StudentPrzedmiotView {
    private String przedmiot;
    private int ects;
    private String semestr;
    private Double ocena;
    private boolean zaliczony;

    public StudentPrzedmiotView(String przedmiot, int ects, String semestr, Double ocena, boolean zaliczony) {
        this.przedmiot = przedmiot;
        this.ects = ects;
        this.semestr = semestr;
        this.ocena = ocena;
        this.zaliczony = zaliczony;
    }

    public String getPrzedmiot() {
        return przedmiot;
    }

    public int getEcts() {
        return ects;
    }

    public String getSemestr() {
        return semestr;
    }

    public Double getOcena() {
        return ocena;
    }

    public boolean isZaliczony() {
        return zaliczony;
    }

    public String getStatus() {
        return zaliczony ? "Tak" : "Nie";
    }
}
