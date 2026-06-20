package org.example.model;

public class Lijek {
    private int idLIJEK;
    private String naziv;
    private String opis;
    private boolean naRecept;
    private int kategorijaId;
    private String mjera;
    private double cijena;

    public Lijek() {}

    public Lijek(int idLIJEK, String naziv, String opis, boolean naRecept,
                 int kategorijaId, String mjera, double cijena) {
        this.idLIJEK = idLIJEK;
        this.naziv = naziv;
        this.opis = opis;
        this.naRecept = naRecept;
        this.kategorijaId = kategorijaId;
        this.mjera = mjera;
        this.cijena = cijena;
    }

    public int getIdLIJEK()           { return idLIJEK; }
    public void setIdLIJEK(int id)    { this.idLIJEK = id; }
    public String getNaziv()          { return naziv; }
    public void setNaziv(String n)    { this.naziv = n; }
    public String getOpis()           { return opis; }
    public void setOpis(String o)     { this.opis = o; }
    public boolean isNaRecept()       { return naRecept; }
    public void setNaRecept(boolean r){ this.naRecept = r; }
    public int getKategorijaId()      { return kategorijaId; }
    public void setKategorijaId(int k){ this.kategorijaId = k; }
    public String getMjera()          { return mjera; }
    public void setMjera(String m)    { this.mjera = m; }
    public double getCijena()         { return cijena; }
    public void setCijena(double c)   { this.cijena = c; }

    @Override
    public String toString() { return naziv; }
}