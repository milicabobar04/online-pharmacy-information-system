package org.example.model;

import java.time.LocalDate;

public class NarudzbaPregled {
    private int idNarudzba;
    private LocalDate datum;
    private String status;
    private double ukupno;
    private Integer zaposleniId; // null ako nije preuzeta
    private String kupacIme;
    private String kupacPrezime;
    private String kupacTelefon;
    private String kupacAdresa;

    public NarudzbaPregled(int idNarudzba, LocalDate datum, String status, double ukupno,
                           Integer zaposleniId, String kupacIme, String kupacPrezime,
                           String kupacTelefon, String kupacAdresa) {
        this.idNarudzba = idNarudzba;
        this.datum = datum;
        this.status = status;
        this.ukupno = ukupno;
        this.zaposleniId = zaposleniId;
        this.kupacIme = kupacIme;
        this.kupacPrezime = kupacPrezime;
        this.kupacTelefon = kupacTelefon;
        this.kupacAdresa = kupacAdresa;
    }

    public int getIdNarudzba()       { return idNarudzba; }
    public LocalDate getDatum()      { return datum; }
    public String getStatus()        { return status; }
    public double getUkupno()        { return ukupno; }
    public Integer getZaposleniId()  { return zaposleniId; }
    public String getKupacIme()      { return kupacIme; }
    public String getKupacPrezime()  { return kupacPrezime; }
    public String getKupacTelefon()  { return kupacTelefon; }
    public String getKupacAdresa()   { return kupacAdresa; }
}