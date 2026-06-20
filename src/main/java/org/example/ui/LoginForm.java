package org.example.ui;

import org.example.dao.KorisnikDAO;
import org.example.model.Korisnik;
import org.example.ui.kupac.KupacFrame;
import org.example.ui.menadzer.MenadzerFrame;
import org.example.ui.zaposleni.ZaposleniFrame;

import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {

    private JTextField txtKorisnickoIme;
    private JPasswordField txtLozinka;
    private JButton btnLogin;
    private JButton btnKupac;
    private KorisnikDAO korisnikDAO = new KorisnikDAO();

    public LoginForm(){
        setTitle("Online Apoteka");
        setSize(400, 280);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // zatvara aplikaciju pri izlazu
        setLocationRelativeTo(null);                    // centrira przor na ekran
        setResizable(false);                            // omogucava promjenu velicine
        initUI();
    }

    private void initUI() {
        JPanel glavni = new JPanel(new BorderLayout(10, 10)); // glavni panel
        glavni.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30)); // margine

        // Naslov
        JLabel lblNaslov = new JLabel("Online Apoteka", SwingConstants.CENTER);
        lblNaslov.setFont(new Font("Arial", Font.BOLD, 20));
        glavni.add(lblNaslov, BorderLayout.NORTH);

        // Forma
        JPanel forma = new JPanel(new GridLayout(2, 2, 10, 10)); // mrezni raspored 2x2

        forma.add(new JLabel("Korisničko ime:"));
        txtKorisnickoIme = new JTextField();
        forma.add(txtKorisnickoIme);

        forma.add(new JLabel("Lozinka:"));
        txtLozinka = new JPasswordField();
        forma.add(txtLozinka);

        glavni.add(forma, BorderLayout.CENTER);

        // Dugmad
        JPanel dugmad = new JPanel(new GridLayout(2, 1, 5, 5));

        btnLogin = new JButton("Prijava (Osoblje)");
        btnLogin.setBackground(new Color(70, 130, 180));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.addActionListener(e -> login());
        dugmad.add(btnLogin);

        btnKupac = new JButton("Nastavi kao Kupac");
        btnKupac.setBackground(new Color(60, 179, 113));
        btnKupac.setForeground(Color.WHITE);
        btnKupac.setFocusPainted(false);
        btnKupac.addActionListener(e -> otvoriKupacEkran());
        dugmad.add(btnKupac);

        glavni.add(dugmad, BorderLayout.SOUTH);

        add(glavni);
    }

    private void login(){
        String korisnickoIme = txtKorisnickoIme.getText().trim();
        String lozinka = new String(txtLozinka.getPassword()).trim();

        if(korisnickoIme.isEmpty() || lozinka.isEmpty()){
            JOptionPane.showMessageDialog(this,
                    "Unesite korisničko ime i lozinku.",
                    "Upozorenje", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Korisnik korisnik = korisnikDAO.login(korisnickoIme, lozinka);

        if(korisnik == null){
            JOptionPane.showMessageDialog(this,
                    "Pogrešno korisničko ime ili lozinka.",
                    "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }
        dispose();

        switch(korisnik.getUloga()){
            case "ZAPOSLENI" -> new ZaposleniFrame(korisnik).setVisible(true);
            case "MENADZER"  -> new MenadzerFrame(korisnik).setVisible(true);
            default -> JOptionPane.showMessageDialog(this, "Nepoznata uloga.");
        }
    }
    private void otvoriKupacEkran() {
        dispose();
        new KupacFrame().setVisible(true);
    }
}
