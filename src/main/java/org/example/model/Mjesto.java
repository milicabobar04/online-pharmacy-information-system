package org.example.model;

public class Mjesto {
    private int posta;
    private String naziv;

    public Mjesto(int posta, String naziv) {
        this.posta = posta;
        this.naziv = naziv;
    }

    public int getPosta()    { return posta; }
    public String getNaziv() { return naziv; }

    @Override
    public String toString() { return posta + " - " + naziv; }
}