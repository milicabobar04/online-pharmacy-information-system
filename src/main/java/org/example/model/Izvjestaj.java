package org.example.model;

import java.time.LocalDate;

public class Izvjestaj {
    private int id;
    private String tip;
    private LocalDate datum;
    private LocalDate periodOd;
    private LocalDate periodDo;
    private String sadrzaj;

    public Izvjestaj(int id, String tip, LocalDate datum, LocalDate periodOd, LocalDate periodDo, String sadrzaj) {
        this.id = id;
        this.tip = tip;
        this.datum = datum;
        this.periodOd = periodOd;
        this.periodDo = periodDo;
        this.sadrzaj = sadrzaj;
    }

    public int getId()             { return id; }
    public String getTip()         { return tip; }
    public LocalDate getDatum()    { return datum; }
    public LocalDate getPeriodOd() { return periodOd; }
    public LocalDate getPeriodDo() { return periodDo; }
    public String getSadrzaj()     { return sadrzaj; }
}