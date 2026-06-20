package org.example.model;

import java.time.LocalDate;

public class NabavnaStavka {
    private String lijek;
    private int kolicina;
    private double jedinicnaCijena;
    private double ukupnaCijena;
    private LocalDate rokTrajanja;

    public NabavnaStavka(String lijek, int kolicina, double jedinicnaCijena, double ukupnaCijena, LocalDate rokTrajanja) {
        this.lijek = lijek;
        this.kolicina = kolicina;
        this.jedinicnaCijena = jedinicnaCijena;
        this.ukupnaCijena = ukupnaCijena;
        this.rokTrajanja = rokTrajanja;
    }

    public String getLijek()           { return lijek; }
    public int getKolicina()           { return kolicina; }
    public double getJedinicnaCijena() { return jedinicnaCijena; }
    public double getUkupnaCijena()    { return ukupnaCijena; }
    public LocalDate getRokTrajanja()  { return rokTrajanja; }
}