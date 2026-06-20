package org.example.model;

public class Korisnik {
    private int idKORISNIK;
    private String korisnickoIme;
    private String lozinka;
    private String uloga;
    private int zaposleniId;
    private Zaposleni zaposleni; // za laksi pristup podacima

    public Korisnik() {}

    public Korisnik(int idKORISNIK, String korisnickoIme,
                    String lozinka, String uloga, int zaposleniId) {
        this.idKORISNIK = idKORISNIK;
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
        this.uloga = uloga;
        this.zaposleniId = zaposleniId;
    }

    public int getIdKORISNIK()              { return idKORISNIK; }
    public void setIdKORISNIK(int id)       { this.idKORISNIK = id; }
    public String getKorisnickoIme()        { return korisnickoIme; }
    public void setKorisnickoIme(String k)  { this.korisnickoIme = k; }
    public String getLozinka()              { return lozinka; }
    public void setLozinka(String l)        { this.lozinka = l; }
    public String getUloga()                { return uloga; }
    public void setUloga(String u)          { this.uloga = u; }
    public int getZaposleniId()             { return zaposleniId; }
    public void setZaposleniId(int z)       { this.zaposleniId = z; }
    public Zaposleni getZaposleni()         { return zaposleni; }
    public void setZaposleni(Zaposleni z)   { this.zaposleni = z; }
}