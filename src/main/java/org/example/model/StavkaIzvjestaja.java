package org.example.model;

public class StavkaIzvjestaja {
    private String lijek;
    private int ukupnoProdano;
    private double ukupanPrihod;

    public StavkaIzvjestaja(String lijek, int ukupnoProdano, double ukupanPrihod) {
        this.lijek = lijek;
        this.ukupnoProdano = ukupnoProdano;
        this.ukupanPrihod = ukupanPrihod;
    }

    public String getLijek()        { return lijek; }
    public int getUkupnoProdano()   { return ukupnoProdano; }
    public double getUkupanPrihod() { return ukupanPrihod; }
}