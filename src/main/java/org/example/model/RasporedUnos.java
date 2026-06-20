package org.example.model;

import java.time.LocalDate;

public class RasporedUnos {
    private int poslovnicaId;
    private String poslovnicaNaziv;
    private LocalDate datum;
    private int smjena;

    public RasporedUnos(int poslovnicaId, String poslovnicaNaziv, LocalDate datum, int smjena) {
        this.poslovnicaId = poslovnicaId;
        this.poslovnicaNaziv = poslovnicaNaziv;
        this.datum = datum;
        this.smjena = smjena;
    }

    public int getPoslovnicaId()       { return poslovnicaId; }
    public String getPoslovnicaNaziv() { return poslovnicaNaziv; }
    public LocalDate getDatum()        { return datum; }
    public int getSmjena()             { return smjena; }
}