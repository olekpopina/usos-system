package pl.usos.usossystem.model;

public class OcenaView {
    private int id;
    private String imie;
    private String nazwisko;
    private String przedmiot;
    private double ocena;

    public OcenaView(int id, String imie, String nazwisko, String przedmiot, double ocena) {
        this.id = id;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.przedmiot = przedmiot;
        this.ocena = ocena;
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

    public String getPrzedmiot() {
        return przedmiot;
    }

    public double getOcena() {
        return ocena;
    }
}