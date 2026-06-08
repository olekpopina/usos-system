package pl.usos.usossystem.model;

public class StudentCourseRecord {
    private final int przedmiotId;
    private final String przedmiot;
    private final int ects;
    private final int semestrId;
    private final String semestr;
    private final Double ocena;
    private final boolean zaliczony;

    public StudentCourseRecord(int przedmiotId, String przedmiot, int ects, int semestrId, String semestr, Double ocena, boolean zaliczony) {
        this.przedmiotId = przedmiotId;
        this.przedmiot = przedmiot;
        this.ects = ects;
        this.semestrId = semestrId;
        this.semestr = semestr;
        this.ocena = ocena;
        this.zaliczony = zaliczony;
    }

    public int getPrzedmiotId() {
        return przedmiotId;
    }

    public String getPrzedmiot() {
        return przedmiot;
    }

    public int getEcts() {
        return ects;
    }

    public int getSemestrId() {
        return semestrId;
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

    @Override
    public String toString() {
        return przedmiot + " (" + ects + " ECTS)";
    }
}
