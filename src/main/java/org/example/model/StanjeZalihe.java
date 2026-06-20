package org.example.model;

public class StanjeZalihe {
    private int idLijek;
    private String naziv;
    private int zaliha;
    private int minimalnaKolicina;
    private String status;

    public StanjeZalihe(int idLijek, String naziv, int zaliha, int minimalnaKolicina, String status) {
        this.idLijek = idLijek;
        this.naziv = naziv;
        this.zaliha = zaliha;
        this.minimalnaKolicina = minimalnaKolicina;
        this.status = status;
    }

    public int getIdLijek()             { return idLijek; }
    public String getNaziv()            { return naziv; }
    public int getZaliha()              { return zaliha; }
    public int getMinimalnaKolicina()   { return minimalnaKolicina; }
    public String getStatus()           { return status; }
}