package org.example.model;

import java.time.LocalDate;

public class Zaposleni {
    private int idZAPOSLENI;
    private String ime;
    private String prezime;
    private String pozicija;
    private double plata;
    private LocalDate datumZaposlenja;
    private String jmbg;

    public Zaposleni() {}

    public Zaposleni(int idZAPOSLENI, String ime, String prezime,
                     String pozicija, double plata,
                     LocalDate datumZaposlenja, String jmbg) {
        this.idZAPOSLENI = idZAPOSLENI;
        this.ime = ime;
        this.prezime = prezime;
        this.pozicija = pozicija;
        this.plata = plata;
        this.datumZaposlenja = datumZaposlenja;
        this.jmbg = jmbg;
    }

    public int getIdZAPOSLENI()              { return idZAPOSLENI; }
    public void setIdZAPOSLENI(int id)       { this.idZAPOSLENI = id; }
    public String getIme()                   { return ime; }
    public void setIme(String i)             { this.ime = i; }
    public String getPrezime()               { return prezime; }
    public void setPrezime(String p)          { this.prezime = p; }
    public String getPozicija()              { return pozicija; }
    public void setPozicija(String p)        { this.pozicija = p; }
    public double getPlata()                 { return plata; }
    public void setPlata(double p)           { this.plata = p; }
    public LocalDate getDatumZaposlenja()    { return datumZaposlenja; }
    public void setDatumZaposlenja(LocalDate d) { this.datumZaposlenja = d; }
    public String getJmbg()                  { return jmbg; }
    public void setJmbg(String j)            { this.jmbg = j; }

    @Override
    public String toString() { return ime + " " + prezime; }
}