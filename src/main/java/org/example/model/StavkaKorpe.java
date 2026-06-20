package org.example.model;

public class StavkaKorpe {
    private int idLijek;
    private String naziv;
    private double cijena;
    private int kolicina;

    public StavkaKorpe(int idLijek, String naziv, double cijena, int kolicina) {
        this.idLijek = idLijek;
        this.naziv = naziv;
        this.cijena = cijena;
        this.kolicina = kolicina;
    }

    public int getIdLijek()    { return idLijek; }
    public String getNaziv()   { return naziv; }
    public double getCijena()  { return cijena; }
    public int getKolicina()   { return kolicina; }
    public double getUkupno()  { return cijena * kolicina; }
}