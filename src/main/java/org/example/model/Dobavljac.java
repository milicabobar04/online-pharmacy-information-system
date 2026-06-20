package org.example.model;

public class Dobavljac {
    private int idDOBAVLJAC;
    private String naziv;
    private String adresa;
    private String telefon;
    private String email;
    private int mjestoPosta;

    public Dobavljac() {}

    public Dobavljac(int idDOBAVLJAC, String naziv, String adresa, String telefon, String email, int mjestoPosta) {
        this.idDOBAVLJAC = idDOBAVLJAC;
        this.naziv = naziv;
        this.adresa = adresa;
        this.telefon = telefon;
        this.email = email;
        this.mjestoPosta = mjestoPosta;
    }

    public int getIdDOBAVLJAC()        { return idDOBAVLJAC; }
    public void setIdDOBAVLJAC(int id) { this.idDOBAVLJAC = id; }
    public String getNaziv()           { return naziv; }
    public void setNaziv(String n)     { this.naziv = n; }
    public String getAdresa()          { return adresa; }
    public void setAdresa(String a)    { this.adresa = a; }
    public String getTelefon()         { return telefon; }
    public void setTelefon(String t)   { this.telefon = t; }
    public String getEmail()           { return email; }
    public void setEmail(String e)     { this.email = e; }
    public int getMjestoPosta()        { return mjestoPosta; }
    public void setMjestoPosta(int m)  { this.mjestoPosta = m; }

    @Override
    public String toString() { return naziv; }
}