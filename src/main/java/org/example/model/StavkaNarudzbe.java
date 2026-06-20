package org.example.model;

public class StavkaNarudzbe {
    private String naziv;
    private int kolicina;
    private double cijena;
    private double ukupno;

    public StavkaNarudzbe(String naziv, int kolicina, double cijena, double ukupno) {
        this.naziv = naziv;
        this.kolicina = kolicina;
        this.cijena = cijena;
        this.ukupno = ukupno;
    }

    public String getNaziv()   { return naziv; }
    public int getKolicina()   { return kolicina; }
    public double getCijena()  { return cijena; }
    public double getUkupno()  { return ukupno; }
}