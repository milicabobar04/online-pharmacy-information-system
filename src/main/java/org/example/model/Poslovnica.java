package org.example.model;

public class Poslovnica {
    private int idPOSLOVNICA;
    private String naziv;
    private String adresa;
    private String telefon;
    private String email;
    private String radiOd;
    private String radiDo;
    private int mjestoPosta;

    public Poslovnica() {}

    public Poslovnica(int idPOSLOVNICA, String naziv, String adresa,
                      String telefon, String email,
                      String radiOd, String radiDo, int mjestoPosta) {
        this.idPOSLOVNICA = idPOSLOVNICA;
        this.naziv = naziv;
        this.adresa = adresa;
        this.telefon = telefon;
        this.email = email;
        this.radiOd = radiOd;
        this.radiDo = radiDo;
        this.mjestoPosta = mjestoPosta;
    }

    public int getIdPOSLOVNICA()          { return idPOSLOVNICA; }
    public void setIdPOSLOVNICA(int id)   { this.idPOSLOVNICA = id; }
    public String getNaziv()              { return naziv; }
    public void setNaziv(String n)        { this.naziv = n; }
    public String getAdresa()             { return adresa; }
    public void setAdresa(String a)       { this.adresa = a; }
    public String getTelefon()            { return telefon; }
    public void setTelefon(String t)      { this.telefon = t; }
    public String getEmail()              { return email; }
    public void setEmail(String e)        { this.email = e; }
    public String getRadiOd()             { return radiOd; }
    public void setRadiOd(String r)       { this.radiOd = r; }
    public String getRadiDo()             { return radiDo; }
    public void setRadiDo(String r)       { this.radiDo = r; }
    public int getMjestoPosta()           { return mjestoPosta; }
    public void setMjestoPosta(int m)     { this.mjestoPosta = m; }

    @Override
    public String toString() { return naziv; }
}