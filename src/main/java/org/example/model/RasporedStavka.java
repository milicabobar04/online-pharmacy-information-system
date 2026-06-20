
package org.example.model;

import java.time.LocalDate;

public class RasporedStavka {
    private LocalDate datum;
    private String poslovnica;
    private int smjena;

    public RasporedStavka(LocalDate datum, String poslovnica, int smjena) {
        this.datum = datum;
        this.poslovnica = poslovnica;
        this.smjena = smjena;
    }

    public LocalDate getDatum()    { return datum; }
    public String getPoslovnica()  { return poslovnica; }
    public int getSmjena()         { return smjena; }
}