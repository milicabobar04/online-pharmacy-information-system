package org.example.model;

public class Kategorija {
    private int idKategorija;
    private String naziv;
    private String opis;

    public Kategorija(int idKategorija, String naziv, String opis) {
        this.idKategorija = idKategorija;
        this.naziv = naziv;
        this.opis = opis;
    }

    public int getIdKategorija() { return idKategorija; }
    public String getNaziv()     { return naziv; }
    public String getOpis()      { return opis; }

    @Override
    public String toString() { return naziv; }
}