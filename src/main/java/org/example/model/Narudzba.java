package org.example.model;

import java.time.LocalDate;

public class Narudzba {
    private int idNARUDZBA;
    private LocalDate datum;
    private String status;
    private double ukupnaVrijednost;
    private int zaposleniId;
    private int poslovnicaId;

    public Narudzba() {}

    public Narudzba(int idNARUDZBA, LocalDate datum, String status,
                    double ukupnaVrijednost, int zaposleniId, int poslovnicaId) {
        this.idNARUDZBA = idNARUDZBA;
        this.datum = datum;
        this.status = status;
        this.ukupnaVrijednost = ukupnaVrijednost;
        this.zaposleniId = zaposleniId;
        this.poslovnicaId = poslovnicaId;
    }

    public int getIdNARUDZBA()               { return idNARUDZBA; }
    public void setIdNARUDZBA(int id)        { this.idNARUDZBA = id; }
    public LocalDate getDatum()              { return datum; }
    public void setDatum(LocalDate d)        { this.datum = d; }
    public String getStatus()                { return status; }
    public void setStatus(String s)          { this.status = s; }
    public double getUkupnaVrijednost()      { return ukupnaVrijednost; }
    public void setUkupnaVrijednost(double u){ this.ukupnaVrijednost = u; }
    public int getZaposleniId()              { return zaposleniId; }
    public void setZaposleniId(int z)        { this.zaposleniId = z; }
    public int getPoslovnicaId()             { return poslovnicaId; }
    public void setPoslovnicaId(int p)       { this.poslovnicaId = p; }
}