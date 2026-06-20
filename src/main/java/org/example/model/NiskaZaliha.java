package org.example.model;

public class NiskaZaliha {
    private int idLijek;
    private String naziv;
    private int trenutnaZaliha;
    private int minimalnaKolicina;
    private int nedostaje;

    public NiskaZaliha(int idLijek, String naziv, int trenutnaZaliha, int minimalnaKolicina, int nedostaje) {
        this.idLijek = idLijek;
        this.naziv = naziv;
        this.trenutnaZaliha = trenutnaZaliha;
        this.minimalnaKolicina = minimalnaKolicina;
        this.nedostaje = nedostaje;
    }

    public int getIdLijek()           { return idLijek; }
    public String getNaziv()          { return naziv; }
    public int getTrenutnaZaliha()    { return trenutnaZaliha; }
    public int getMinimalnaKolicina() { return minimalnaKolicina; }
    public int getNedostaje()         { return nedostaje; }
}