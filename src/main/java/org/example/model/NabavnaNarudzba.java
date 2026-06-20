package org.example.model;

import java.time.LocalDate;

public class NabavnaNarudzba {
    private int idNabavnaNarudzba;
    private LocalDate datumNarudzbe;
    private LocalDate datumIsporuke;
    private double ukupnaVrijednost;
    private String status;
    private String napomena;
    private String dobavljac;
    private int poslovnicaId;

    public NabavnaNarudzba(int idNabavnaNarudzba, LocalDate datumNarudzbe, LocalDate datumIsporuke,
                           double ukupnaVrijednost, String status, String napomena,
                           String dobavljac, int poslovnicaId) {
        this.idNabavnaNarudzba = idNabavnaNarudzba;
        this.datumNarudzbe = datumNarudzbe;
        this.datumIsporuke = datumIsporuke;
        this.ukupnaVrijednost = ukupnaVrijednost;
        this.status = status;
        this.napomena = napomena;
        this.dobavljac = dobavljac;
        this.poslovnicaId = poslovnicaId;
    }

    public int getIdNabavnaNarudzba()   { return idNabavnaNarudzba; }
    public LocalDate getDatumNarudzbe() { return datumNarudzbe; }
    public LocalDate getDatumIsporuke() { return datumIsporuke; }
    public double getUkupnaVrijednost() { return ukupnaVrijednost; }
    public String getStatus()           { return status; }
    public String getNapomena()         { return napomena; }
    public String getDobavljac()        { return dobavljac; }
    public int getPoslovnicaId()        { return poslovnicaId; }
}