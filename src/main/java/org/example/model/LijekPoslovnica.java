package org.example.model;

public class LijekPoslovnica {
    private int idLijek;
    private String naziv;
    private double cijena;
    private String mjera;
    private boolean naRecept;
    private int zaliha;

    public LijekPoslovnica(int idLijek, String naziv, double cijena,
                           String mjera, boolean naRecept, int zaliha) {
        this.idLijek = idLijek;
        this.naziv = naziv;
        this.cijena = cijena;
        this.mjera = mjera;
        this.naRecept = naRecept;
        this.zaliha = zaliha;
    }

    public int getIdLijek()      { return idLijek; }
    public String getNaziv()     { return naziv; }
    public double getCijena()    { return cijena; }
    public String getMjera()     { return mjera; }
    public boolean isNaRecept()  { return naRecept; }
    public int getZaliha()       { return zaliha; }
}