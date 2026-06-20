package org.example.ui.kupac;

import org.example.dao.KategorijaDAO;
import org.example.dao.LijekDAO;
import org.example.dao.NarudzbaDAO;
import org.example.dao.PoslovnicaDAO;
import org.example.model.Kategorija;
import org.example.model.LijekPoslovnica;
import org.example.model.Poslovnica;
import org.example.model.StavkaKorpe;
import org.example.ui.LoginForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class KupacFrame extends JFrame {

    // Objekti za rad sa bazom
    private final PoslovnicaDAO poslovnicaDAO = new PoslovnicaDAO();
    private final LijekDAO lijekDAO = new LijekDAO();
    private final NarudzbaDAO narudzbaDAO = new NarudzbaDAO();

    private final CardLayout cardLayout = new CardLayout();  // omogucava povezivanje vise panela
    private final JPanel mainPanel = new JPanel(cardLayout);

    // ODABIR POSLOVNICE
    private JComboBox<Poslovnica> comboPoslovnice;
    private int odabranaPoslovnicaId = -1;

    // LIJEKOVI I KORPA
    private DefaultTableModel modelLijekovi;    // veza izmedju liste i tabele
    private DefaultTableModel modelKorpa;
    private JTable tabelaLijekovi;             //prikaz za korisnika
    private JTable tabelaKorpa;
    private JLabel lblUkupno;
    private final List<StavkaKorpe> korpa = new ArrayList<>();
    private List<LijekPoslovnica> trenutniLijekovi = new ArrayList<>(); // podaci ucitani iz baze

    // PODACI O KUPCU
    private JTextField txtIme, txtPrezime, txtEmail, txtTelefon, txtAdresa, txtKartica;

    // POTVRDA
    private JLabel lblPotvrda;

    // KATEGORIJE I PRETRAGA
    private final KategorijaDAO kategorijaDAO = new KategorijaDAO();
    private JTextField txtPretraga;
    private JComboBox<Object> comboKategorije;


    public KupacFrame() {
        setTitle("Online Apoteka — Kupovina");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel.add(createPanelPoslovnica(), "poslovnica");
        mainPanel.add(createPanelLijekovi(), "lijekovi");
        mainPanel.add(createPanelPodaci(), "podaci");
        mainPanel.add(createPanelPotvrda(), "potvrda");

        add(mainPanel);
        cardLayout.show(mainPanel, "poslovnica");
    }

    /**
     * Kreira panel za odabir poslovnice.
     * Korisnik bira poslovnicu iz liste i prelazi na pregled lijekova.
     */
    private JPanel createPanelPoslovnica() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        // Naslov ekrana
        JLabel lblNaslov = new JLabel("Odaberite poslovnicu", SwingConstants.CENTER);
        lblNaslov.setFont(new Font("Arial", Font.BOLD, 20));
        panel.add(lblNaslov, BorderLayout.NORTH);

        //DOhvatanje poslovnice iz baze i stavljanje u combo box
        List<Poslovnica> poslovnice = poslovnicaDAO.getSvePoslovnice();
        comboPoslovnice = new JComboBox<>(poslovnice.toArray(new Poslovnica[0]));

        // Centar layout
        JPanel centar = new JPanel();
        centar.setLayout(new BoxLayout(centar, BoxLayout.Y_AXIS));
        comboPoslovnice.setMaximumSize(new Dimension(400, 30));
        centar.add(Box.createVerticalGlue());
        centar.add(comboPoslovnice);
        centar.add(Box.createVerticalStrut(20));

        // Dugme za prelazak na lijekove
        JButton btnDalje = new JButton("Pregledaj lijekove");
        btnDalje.addActionListener(e -> {
            Poslovnica odabrana = (Poslovnica) comboPoslovnice.getSelectedItem();
            if (odabrana == null) {
                JOptionPane.showMessageDialog(this, "Nema dostupnih poslovnica.");
                return;
            }

            // Cuvamo id i ucitavamo lijekove
            odabranaPoslovnicaId = odabrana.getIdPOSLOVNICA();
            ucitajLijekove();

            // Prelazak na panel lijekova
            cardLayout.show(mainPanel, "lijekovi");
        });
        btnDalje.setAlignmentX(Component.CENTER_ALIGNMENT);
        centar.add(btnDalje);
        centar.add(Box.createVerticalGlue());

        panel.add(centar, BorderLayout.CENTER);

        JButton btnNazad = new JButton("Nazad na Login");
        btnNazad.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });
        panel.add(btnNazad, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Kreira panel za prikaz lijekova i korpe.
     * Omogućava pretragu i filtriranje lijekova, dodavanje u korpu,
     * kao i pregled i upravljanje stavkama u korpi.
     */
    private JPanel createPanelLijekovi() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Naslov ekrana
        JLabel lblNaslov = new JLabel("Dostupni lijekovi", SwingConstants.CENTER);
        lblNaslov.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(lblNaslov, BorderLayout.NORTH);

        // Tabela lijekova
        modelLijekovi = new DefaultTableModel(
                new String[]{"ID", "Naziv", "Cijena", "Zaliha", "Mjera", "Na recept"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabelaLijekovi = new JTable(modelLijekovi);
        tabelaLijekovi.getColumnModel().getColumn(0).setMaxWidth(40);

        JPanel gornji = new JPanel(new BorderLayout());
        // Panel za pretragu i filtriranje
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Pretraga:"));
        txtPretraga = new JTextField(15);
        filterPanel.add(txtPretraga);

        filterPanel.add(new JLabel("Kategorija:"));
        comboKategorije = new JComboBox<>();
        // Popunjavamo kategorije iz baze
        comboKategorije.addItem("Sve kategorije");
        for (Kategorija k : kategorijaDAO.getSveKategorije()) {
            comboKategorije.addItem(k);
        }
        filterPanel.add(comboKategorije);

        // Dugme za pretragu
        JButton btnPretrazi = new JButton("Pretraži");
        btnPretrazi.addActionListener(e -> ucitajLijekove());
        filterPanel.add(btnPretrazi);

        // Reset filtera
        JButton btnReset = new JButton("Resetuj");
        btnReset.addActionListener(e -> {
            txtPretraga.setText("");
            comboKategorije.setSelectedIndex(0);
            ucitajLijekove();
        });
        filterPanel.add(btnReset);

        gornji.add(filterPanel, BorderLayout.NORTH);
        gornji.add(new JScrollPane(tabelaLijekovi), BorderLayout.CENTER); // skrol tabele lijekova

        // Dugme za dodavanje u korpu
        JButton btnDodaj = new JButton("Dodaj u korpu");
        btnDodaj.addActionListener(e -> dodajUKorpu());

        JPanel dodajPanel = new JPanel();
        dodajPanel.add(btnDodaj);
        gornji.add(dodajPanel, BorderLayout.SOUTH);
        gornji.setPreferredSize(new Dimension(900, 280));

        // Tabela korpe
        modelKorpa = new DefaultTableModel(
                new String[]{"Naziv", "Količina", "Cijena", "Ukupno"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tabelaKorpa = new JTable(modelKorpa);

        JPanel donji = new JPanel(new BorderLayout());
        donji.add(new JLabel("Korpa:"), BorderLayout.NORTH);
        donji.add(new JScrollPane(tabelaKorpa), BorderLayout.CENTER);

        // Uklanjanje stavke iz korpe
        JButton btnUkloni = new JButton("Ukloni odabrano");
        btnUkloni.addActionListener(e -> ukloniIzKorpe());

        // Ukupna cijena korpe
        lblUkupno = new JLabel("Ukupno: 0.00 KM");
        lblUkupno.setFont(new Font("Arial", Font.BOLD, 14));

        JPanel donjiAlati = new JPanel(new BorderLayout());
        donjiAlati.add(btnUkloni, BorderLayout.WEST);
        donjiAlati.add(lblUkupno, BorderLayout.EAST);
        donji.add(donjiAlati, BorderLayout.SOUTH);

        // Split prikaz: lijekovi (gore) + korpa (dolje)
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, gornji, donji);
        split.setResizeWeight(0.55);
        panel.add(split, BorderLayout.CENTER);

        // Navigacija
        JPanel nav = new JPanel(new BorderLayout());
        JButton btnNazad = new JButton("Nazad na poslovnice");
        btnNazad.addActionListener(e -> {
            korpa.clear();
            refreshKorpaTable();
            cardLayout.show(mainPanel, "poslovnica");
        });
        JButton btnDalje = new JButton("Nastavi na podatke");
        btnDalje.addActionListener(e -> {
            if (korpa.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Korpa je prazna.");
                return;
            }
            cardLayout.show(mainPanel, "podaci");
        });
        nav.add(btnNazad, BorderLayout.WEST);
        nav.add(btnDalje, BorderLayout.EAST);
        panel.add(nav, BorderLayout.SOUTH);

        return panel;
    }
    /**
     * Učitava listu lijekova za odabranu poslovnicu uz primjenu filtera
     * po kategoriji i tekstualnoj pretrazi.
     *
     * Tabela se prethodno čisti prije punjenja novim rezultatima.
     *
     * Ako pretraga ne vrati nijedan rezultat i korisnik je unio tekst,
     * metoda pokreće provjeru dostupnosti lijeka u drugim poslovnicama.
     */

    private void ucitajLijekove() {
        String pretraga = txtPretraga.getText().trim();
        Integer kategorijaId = null;
        Object odabranaKat = comboKategorije.getSelectedItem();
        if (odabranaKat instanceof Kategorija k) {
            kategorijaId = k.getIdKategorija();
        }

        trenutniLijekovi = lijekDAO.getLijekoviSaZalihom(odabranaPoslovnicaId, kategorijaId, pretraga);
        modelLijekovi.setRowCount(0);
        for (LijekPoslovnica lp : trenutniLijekovi) {
            modelLijekovi.addRow(new Object[]{
                    lp.getIdLijek(),
                    lp.getNaziv(),
                    String.format("%.2f KM", lp.getCijena()),
                    lp.getZaliha(),
                    lp.getMjera(),
                    lp.isNaRecept() ? "Da" : "Ne"
            });
        }

        // Ako pretraga ne daje rezultata, provjeri ima li lijeka u drugim poslovnicama
        if (trenutniLijekovi.isEmpty() && !pretraga.isEmpty()) {
            provjeriDrugePoslovnice(pretraga);
        }
    }
    /**
     * Provjerava dostupnost lijeka u drugim poslovnicama ako nije pronađen
     * u trenutno odabranoj poslovnici.
     *
     * Ako lijek postoji u drugim poslovnicama, korisniku se prikazuje lista
     * i omogućava izbor nove poslovnice.
     * Prilikom promjene poslovnice, korpa se briše i podaci se ponovo učitavaju.
     *
     * @param naziv naziv lijeka koji se traži u drugim poslovnicama
     */
    private void provjeriDrugePoslovnice(String naziv) {
        List<Poslovnica> alternative = lijekDAO.getPoslovniceSaLijekom(naziv, odabranaPoslovnicaId);

        if (alternative.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Lijek '" + naziv + "' trenutno nije dostupan ni u jednoj poslovnici.",
                    "Nema rezultata", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Poslovnica izbor = (Poslovnica) JOptionPane.showInputDialog(this,
                "Lijek '" + naziv + "' nije dostupan u odabranoj poslovnici.\n" +
                        "Dostupan je u sljedećim poslovnicama.\n" +
                        "Odaberite poslovnicu — napomena: korpa će biti ispražnjena i počinje nova kupovina.",
                "Lijek nije dostupan",
                JOptionPane.QUESTION_MESSAGE,
                null,
                alternative.toArray(new Poslovnica[0]),
                alternative.get(0));

        if (izbor != null) {
            odabranaPoslovnicaId = izbor.getIdPOSLOVNICA();
            korpa.clear();
            refreshKorpaTable();
            comboKategorije.setSelectedIndex(0);
            ucitajLijekove();
            JOptionPane.showMessageDialog(this,
                    "Poslovnica promijenjena na: " + izbor.getNaziv());
        }
    }
    /**
     * Dodaje odabrani lijek u korpu.
     *
     * Korisnik prvo bira lijek iz tabele, zatim unosi količinu.
     * Metoda provjerava validnost unosa (broj, opseg, dostupna zaliha).
     *
     * Ako lijek već postoji u korpi, povećava se količina stavke,
     * uz provjeru da ne prelazi dostupnu zalihu.
     *
     * Nakon izmjena, korpa se osvježava u tabeli.
     */
    private void dodajUKorpu() {
        int red = tabelaLijekovi.getSelectedRow();
        if (red == -1) {
            JOptionPane.showMessageDialog(this, "Odaberite lijek iz tabele.");
            return;
        }

        LijekPoslovnica lp = trenutniLijekovi.get(red);

        String unos = JOptionPane.showInputDialog(this,
                "Unesite količinu (dostupno: " + lp.getZaliha() + "):", "1");
        if (unos == null) return;

        int kolicina;
        try {
            kolicina = Integer.parseInt(unos.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Unesite ispravan broj.");
            return;
        }

        if (kolicina <= 0 || kolicina > lp.getZaliha()) {
            JOptionPane.showMessageDialog(this,
                    "Količina mora biti između 1 i " + lp.getZaliha() + ".");
            return;
        }

        // Ako lijek vec postoji u korpi, samo povecaj kolicinu
        for (StavkaKorpe s : korpa) {
            if (s.getIdLijek() == lp.getIdLijek()) {
                int novaKolicina = s.getKolicina() + kolicina;
                if (novaKolicina > lp.getZaliha()) {
                    JOptionPane.showMessageDialog(this, "Premašena dostupna zaliha.");
                    return;
                }
                korpa.remove(s);
                korpa.add(new StavkaKorpe(lp.getIdLijek(), lp.getNaziv(), lp.getCijena(), novaKolicina));
                refreshKorpaTable();
                return;
            }
        }

        korpa.add(new StavkaKorpe(lp.getIdLijek(), lp.getNaziv(), lp.getCijena(), kolicina));
        refreshKorpaTable();
    }
    /**
     * Uklanja odabranu stavku iz korpe.
     *
     * Korisnik mora selektovati red u tabeli korpe.
     * Nakon uklanjanja, tabela korpe se osvježava.
     */
    private void ukloniIzKorpe() {
        int red = tabelaKorpa.getSelectedRow();
        if (red == -1) {
            JOptionPane.showMessageDialog(this, "Odaberite stavku iz korpe.");
            return;
        }
        korpa.remove(red);
        refreshKorpaTable();
    }
    /**
     * Osvježava tabelu korpe i prikazuje trenutne stavke.
     *
     * Briše postojeće podatke iz tabele i ponovo ih puni
     * na osnovu liste korpa.
     * Takođe računa i prikazuje ukupnu cijenu svih stavki.
     */
    private void refreshKorpaTable() {
        modelKorpa.setRowCount(0);
        double ukupno = 0;
        for (StavkaKorpe s : korpa) {
            modelKorpa.addRow(new Object[]{
                    s.getNaziv(),
                    s.getKolicina(),
                    String.format("%.2f KM", s.getCijena()),
                    String.format("%.2f KM", s.getUkupno())
            });
            ukupno += s.getUkupno();
        }
        lblUkupno.setText(String.format("Ukupno: %.2f KM", ukupno));
    }

    /**
     * Kreira panel za unos podataka kupca za dostavu i plaćanje.
     *
     * Sadrži formu za unos ličnih podataka (ime, prezime, email, telefon),
     * adresu dostave i broj kartice.
     *
     * Takođe omogućava navigaciju nazad na korpu ili potvrdu narudžbe.
     *
     * @return JPanel koji predstavlja formu za unos podataka kupca
     */
    private JPanel createPanelPodaci() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JLabel lblNaslov = new JLabel("Podaci za dostavu i plaćanje", SwingConstants.CENTER);
        lblNaslov.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(lblNaslov, BorderLayout.NORTH);

        JPanel forma = new JPanel(new GridLayout(6, 2, 10, 10));

        txtIme = new JTextField();
        txtPrezime = new JTextField();
        txtEmail = new JTextField();
        txtTelefon = new JTextField();
        txtAdresa = new JTextField();
        txtKartica = new JTextField();

        forma.add(new JLabel("Ime:"));        forma.add(txtIme);
        forma.add(new JLabel("Prezime:"));    forma.add(txtPrezime);
        forma.add(new JLabel("Email:"));      forma.add(txtEmail);
        forma.add(new JLabel("Telefon:"));    forma.add(txtTelefon);
        forma.add(new JLabel("Adresa dostave:")); forma.add(txtAdresa);
        forma.add(new JLabel("Broj kartice:")); forma.add(txtKartica);

        panel.add(forma, BorderLayout.CENTER);

        JPanel nav = new JPanel(new BorderLayout());
        JButton btnNazad = new JButton("Nazad na korpu");
        btnNazad.addActionListener(e -> cardLayout.show(mainPanel, "lijekovi"));

        JButton btnPotvrdi = new JButton("Potvrdi narudžbu");
        btnPotvrdi.setBackground(new Color(60, 179, 113));
        btnPotvrdi.setForeground(Color.WHITE);
        btnPotvrdi.addActionListener(e -> potvrdiNarudzbu());

        nav.add(btnNazad, BorderLayout.WEST);
        nav.add(btnPotvrdi, BorderLayout.EAST);
        panel.add(nav, BorderLayout.SOUTH);

        return panel;
    }
    /**
     * Validira podatke kupca i kreira novu narudžbu u sistemu.
     *
     * Prvo se provjeravaju obavezna polja forme.
     * Zatim se kreira narudžba (header) u bazi putem DAO sloja.
     * Nakon toga se dodaju sve stavke iz korpe kao stavke narudžbe.
     *
     * Na kraju se prikazuje ekran potvrde sa brojem narudžbe i ukupnim iznosom.
     */
    private void potvrdiNarudzbu() {
        String ime = txtIme.getText().trim();
        String prezime = txtPrezime.getText().trim();
        String email = txtEmail.getText().trim();
        String telefon = txtTelefon.getText().trim();
        String adresa = txtAdresa.getText().trim();
        String kartica = txtKartica.getText().trim();

        if (ime.isEmpty() || prezime.isEmpty() || adresa.isEmpty() || kartica.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Popunite obavezna polja: ime, prezime, adresa, kartica.");
            return;
        }

        // 1. Kreiraj narudzbu (header)
        int narudzbaId = narudzbaDAO.kreirajNarudzbu(
                ime, prezime, email, telefon, adresa, kartica, odabranaPoslovnicaId);

        if (narudzbaId == -1) {
            JOptionPane.showMessageDialog(this,
                    "Greška prilikom kreiranja narudžbe. Pokušajte ponovo.",
                    "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Dodaj sve stavke iz korpe
        double ukupno = 0;
        for (StavkaKorpe s : korpa) {
            boolean ok = narudzbaDAO.dodajStavku(narudzbaId, s.getIdLijek(), s.getKolicina());
            if (!ok) {
                JOptionPane.showMessageDialog(this,
                        "Greška prilikom dodavanja stavke: " + s.getNaziv(),
                        "Greška", JOptionPane.ERROR_MESSAGE);
            }
            ukupno += s.getUkupno();
        }

        lblPotvrda.setText("<html><center>Narudžba broj <b>" + narudzbaId +
                "</b> je uspješno kreirana!<br><br>Ukupan iznos: " +
                String.format("%.2f KM", ukupno) +
                "<br><br>Hvala na kupovini!</center></html>");

        cardLayout.show(mainPanel, "potvrda");
    }

    /**
     * Kreira panel za prikaz potvrde narudžbe nakon uspješne kupovine.
     *
     * Panel prikazuje poruku o uspješno kreiranoj narudžbi i omogućava
     * korisniku da započne novu kupovinu ili se vrati na login ekran.
     *
     * Dugme "Nova kupovina" resetuje korpu i unosne podatke te vraća korisnika
     * na početni ekran za odabir poslovnice.
     *
     * Dugme "Povratak na Login" zatvara trenutni prozor i otvara login formu.
     *
     */
    private JPanel createPanelPotvrda() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));

        lblPotvrda = new JLabel("", SwingConstants.CENTER);
        lblPotvrda.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lblPotvrda, BorderLayout.CENTER);

        JPanel dugmad = new JPanel();

        JButton btnNova = new JButton("Nova kupovina");
        btnNova.addActionListener(e -> {
            korpa.clear();
            refreshKorpaTable();
            txtIme.setText(""); txtPrezime.setText(""); txtEmail.setText("");
            txtTelefon.setText(""); txtAdresa.setText(""); txtKartica.setText("");
            cardLayout.show(mainPanel, "poslovnica");
        });

        JButton btnKraj = new JButton("Povratak na Login");
        btnKraj.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });

        dugmad.add(btnNova);
        dugmad.add(btnKraj);
        panel.add(dugmad, BorderLayout.SOUTH);

        return panel;
    }
}