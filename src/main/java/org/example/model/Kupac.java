package org.example.model;

public class Kupac {
    private String ime;
    private String prezime;
    private String email;
    private String telefon;
    private String adresa;
    private String brojKartice;
    private int narudzbaId;

    public Kupac() {}

    public Kupac(String ime, String prezime, String email,
                 String telefon, String adresa,
                 String brojKartice, int narudzbaId) {
        this.ime = ime;
        this.prezime = prezime;
        this.email = email;
        this.telefon = telefon;
        this.adresa = adresa;
        this.brojKartice = brojKartice;
        this.narudzbaId = narudzbaId;
    }

    public String getIme()                { return ime; }
    public void setIme(String i)          { this.ime = i; }
    public String getPrezime()            { return prezime; }
    public void setPrezime(String p)      { this.prezime = p; }
    public String getEmail()              { return email; }
    public void setEmail(String e)        { this.email = e; }
    public String getTelefon()            { return telefon; }
    public void setTelefon(String t)      { this.telefon = t; }
    public String getAdresa()             { return adresa; }
    public void setAdresa(String a)       { this.adresa = a; }
    public String getBrojKartice()        { return brojKartice; }
    public void setBrojKartice(String b)  { this.brojKartice = b; }
    public int getNarudzbaId()            { return narudzbaId; }
    public void setNarudzbaId(int n)      { this.narudzbaId = n; }
}