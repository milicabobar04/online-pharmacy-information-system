package org.example.ui.menadzer;

import org.example.dao.*;
import org.example.model.*;
import org.example.ui.LoginForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class MenadzerFrame extends JFrame {

    private final ZaposleniDAO zaposleniDAO = new ZaposleniDAO();
    private final KorisnikDAO korisnikDAO = new KorisnikDAO();
    private final RasporedDAO rasporedDAO = new RasporedDAO();
    private final PoslovnicaDAO poslovnicaDAO = new PoslovnicaDAO();
    private final DobavljacDAO dobavljacDAO = new DobavljacDAO();
    private final MjestoDAO mjestoDAO = new MjestoDAO();
    private final NabavnaNarudzbaDAO nabavnaDAO = new NabavnaNarudzbaDAO();
    private final LijekDAO lijekDAO = new LijekDAO();
    private final ZalihaDAO zalihaDAO = new ZalihaDAO();
    private final int zaposleniId;
    private final IzvjestajDAO izvjestajDAO = new IzvjestajDAO();
    private final KategorijaDAO kategorijaDAO = new KategorijaDAO();

    private JComboBox<Poslovnica> comboPoslovnicaNabavka;
    private JComboBox<Poslovnica> comboPoslovnicaPregled;
    private JComboBox<Poslovnica> comboPoslovnicaPromet;
    private JComboBox<Poslovnica> comboPoslovnicaSacuvani;
    private JComboBox<Poslovnica> comboPoslovnicaRaspored;

    // Katalog lijekova
    private DefaultTableModel modelLijekovi;
    private JTable tabelaLijekovi;
    private List<Lijek> trenutniLijekoviSvi;
    private JTextField txtNazivL, txtOpisL, txtMjeraL, txtCijenaL;
    private JCheckBox chkNaRecept;
    private JComboBox<Kategorija> comboKategorijaL;

    // Promet izvjestaj
    private JSpinner spinnerPeriodOd, spinnerPeriodDo;
    private DefaultTableModel modelIzvjestajPromet;
    private JLabel lblUkupanPrihod;
    private List<StavkaIzvjestaja> trenutniIzvjestaj;

    // Sacuvani izvjestaji
    private DefaultTableModel modelSacuvaniIzvjestaji;
    private JTable tabelaSacuvaniIzvjestaji;
    private List<Izvjestaj> trenutniSacuvani;

    // Kreiranje nabavne narudzbe
    private DefaultTableModel modelNiskaZaliha;
    private JComboBox<Dobavljac> comboDobavljacNabavka;
    private JTextField txtNapomenaNabavka;
    private JLabel lblAktivnaNabavka;
    private int aktivnaNabavkaId = -1;
    private JComboBox<Lijek> comboLijekStavka;
    private JSpinner spinnerKolicinaStavka;
    private JSpinner spinnerRokTrajanja;
    private DefaultTableModel modelStavkeNabavke;
    private JTextField txtNovLijekNaziv;
    private JCheckBox chkNoviLijek;


    // Pregled nabavnih narudzba
    private DefaultTableModel modelNabavneNarudzbe;
    private JTable tabelaNabavneNarudzbe;
    private List<NabavnaNarudzba> trenutneNabavne;

    // Poslovnice
    private DefaultTableModel modelPoslovnice;
    private JTable tabelaPoslovnice;
    private List<Poslovnica> trenutnePoslovnice;
    private JTextField txtNazivP, txtAdresaP, txtTelefonP, txtEmailP, txtRadiOd, txtRadiDo;
    private JComboBox<Mjesto> comboMjestoP;

    // Dobavljaci
    private DefaultTableModel modelDobavljaci;
    private JTable tabelaDobavljaci;
    private List<Dobavljac> trenutniDobavljaci;
    private JTextField txtNazivD, txtAdresaD, txtTelefonD, txtEmailD;
    private JComboBox<Mjesto> comboMjestoD;

    // Spisak zaposlenih
    private DefaultTableModel modelZaposleni;
    private JTable tabelaZaposleni;
    private List<Zaposleni> trenutniZaposleni;
    private JTextField txtIme, txtPrezime, txtPozicija, txtPlata, txtJmbg;
    private JSpinner spinnerDatumZap;

    // Korisnicki nalozi
    private JComboBox<Zaposleni> comboZaposleniNalog;
    private JLabel lblPostojeciNalog;
    private JTextField txtKorisnickoIme;
    private JPasswordField txtLozinka;
    private JComboBox<String> comboUloga;

    // Raspored
    private JComboBox<Zaposleni> comboZaposleniRaspored;
    private JSpinner spinnerDatumRaspored;
    private JComboBox<Integer> comboSmjena;
    private DefaultTableModel modelRaspored;
    private JTable tabelaRaspored;
    private List<RasporedUnos> trenutniRaspored;

    public MenadzerFrame(Korisnik korisnik) {
        this.zaposleniId = korisnik.getZaposleniId();
        setTitle("Online Apoteka — Menadžer: " +
                korisnik.getZaposleni().getIme() + " " + korisnik.getZaposleni().getPrezime());
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Zaposleni", createZaposleniTab());
        tabs.addTab("Poslovnice", createPoslovnicePanel());
        tabs.addTab("Dobavljači", createDobavljaciPanel());
        tabs.addTab("Lijekovi", createLijekoviPanel());
        tabs.addTab("Nabavne narudžbe", createNabavnePanel());
        tabs.addTab("Izvještaji", createIzvjestajiPanel());

        add(tabs, BorderLayout.CENTER);

        JPanel donji = new JPanel(new BorderLayout());
        JButton btnOdjava = new JButton("Odjava");
        btnOdjava.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });
        donji.add(btnOdjava, BorderLayout.EAST);
        add(donji, BorderLayout.SOUTH);
    }

    /**
     * Kreira tab za upravljanje zaposlenima.
     * sa podtabovima
     */
    private JPanel createZaposleniTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTabbedPane subTabs = new JTabbedPane();
        subTabs.addTab("Spisak zaposlenih", createSpisakPanel());
        subTabs.addTab("Korisnički nalozi", createNaloziPanel());
        subTabs.addTab("Raspored", createRasporedPanel());
        panel.add(subTabs, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Kreira panel za pregled i upravljanje zaposlenima.
     */
    private JPanel createSpisakPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modelZaposleni = new DefaultTableModel(
                new String[]{"ID", "Ime", "Prezime", "Pozicija", "Plata", "Datum zaposlenja", "JMBG"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaZaposleni = new JTable(modelZaposleni);
        tabelaZaposleni.getSelectionModel().addListSelectionListener(e -> popuniFormuIzTabele());
        panel.add(new JScrollPane(tabelaZaposleni), BorderLayout.CENTER);

        JPanel forma = new JPanel(new GridLayout(3, 4, 5, 5));
        txtIme = new JTextField();
        txtPrezime = new JTextField();
        txtPozicija = new JTextField();
        txtPlata = new JTextField();
        txtJmbg = new JTextField();

        spinnerDatumZap = new JSpinner(new SpinnerDateModel());
        spinnerDatumZap.setEditor(new JSpinner.DateEditor(spinnerDatumZap, "yyyy-MM-dd"));

        forma.add(new JLabel("Ime:"));     forma.add(txtIme);
        forma.add(new JLabel("Prezime:")); forma.add(txtPrezime);
        forma.add(new JLabel("Pozicija:")); forma.add(txtPozicija);
        forma.add(new JLabel("Plata:"));   forma.add(txtPlata);
        forma.add(new JLabel("Datum zaposlenja:")); forma.add(spinnerDatumZap);
        forma.add(new JLabel("JMBG:"));    forma.add(txtJmbg);

        JPanel dugmad = new JPanel();
        JButton btnDodaj = new JButton("Dodaj");
        btnDodaj.addActionListener(e -> dodajZaposlenog());
        JButton btnIzmijeni = new JButton("Izmijeni odabrano");
        btnIzmijeni.addActionListener(e -> izmijeniZaposlenog());
        JButton btnObrisi = new JButton("Obriši odabrano");
        btnObrisi.addActionListener(e -> obrisiZaposlenog());
        JButton btnOcisti = new JButton("Očisti formu");
        btnOcisti.addActionListener(e -> ocistiFormu());

        dugmad.add(btnDodaj);
        dugmad.add(btnIzmijeni);
        dugmad.add(btnObrisi);
        dugmad.add(btnOcisti);

        JPanel donji = new JPanel(new BorderLayout());
        donji.add(forma, BorderLayout.CENTER);
        donji.add(dugmad, BorderLayout.SOUTH);
        panel.add(donji, BorderLayout.SOUTH);

        ucitajZaposlene();
        return panel;
    }
    /**
     * Učitava sve zaposlene iz baze podataka i prikazuje ih u tabeli.
     */
    private void ucitajZaposlene() {
        modelZaposleni.setRowCount(0);
        trenutniZaposleni = zaposleniDAO.getSviZaposleni();
        for (Zaposleni z : trenutniZaposleni) {
            modelZaposleni.addRow(new Object[]{
                    z.getIdZAPOSLENI(), z.getIme(), z.getPrezime(), z.getPozicija(),
                    String.format("%.2f", z.getPlata()), z.getDatumZaposlenja(), z.getJmbg()
            });
        }
        if (comboZaposleniNalog != null) {
            comboZaposleniNalog.removeAllItems();
            for (Zaposleni z : trenutniZaposleni) comboZaposleniNalog.addItem(z);
        }
        if (comboZaposleniRaspored != null) {
            comboZaposleniRaspored.removeAllItems();
            for (Zaposleni z : trenutniZaposleni) comboZaposleniRaspored .addItem(z);
        }
    }
    /**
     * Popunjava formu podacima odabranog zaposlenog iz tabele.
     */
    private void popuniFormuIzTabele() {
        int red = tabelaZaposleni.getSelectedRow();
        if (red == -1) return;
        Zaposleni z = trenutniZaposleni.get(red);
        txtIme.setText(z.getIme());
        txtPrezime.setText(z.getPrezime());
        txtPozicija.setText(z.getPozicija());
        txtPlata.setText(String.valueOf(z.getPlata()));
        txtJmbg.setText(z.getJmbg());
        spinnerDatumZap.setValue(Date.valueOf(z.getDatumZaposlenja()));
    }
    /**
     * Briše sadržaj svih polja forme i poništava selekciju tabele.
     */
    private void ocistiFormu() {
        txtIme.setText(""); txtPrezime.setText(""); txtPozicija.setText("");
        txtPlata.setText(""); txtJmbg.setText("");
        spinnerDatumZap.setValue(new java.util.Date());
        txtJmbg.setBackground(UIManager.getColor("TextField.background")); // <-- NOVO
        tabelaZaposleni.clearSelection();
    }
    /**
     * Dodaje novog zaposlenog u bazu podataka.
     */
    private void dodajZaposlenog() {
        Zaposleni z = procitajIzForme(null);
        if (z == null) return;

        // Validacija JMBG formata (13 cifara)
        if (!z.getJmbg().matches("\\d{13}")) {
            txtJmbg.setBackground(new Color(255, 200, 200));
            JOptionPane.showMessageDialog(this,
                    "JMBG mora sadržavati tačno 13 cifara.",
                    "Neispravan JMBG", JOptionPane.WARNING_MESSAGE);
            txtJmbg.requestFocus();
            return;
        }

        int rezultat = zaposleniDAO.dodajZaposlenog(z);
        switch (rezultat) {
            case 1 -> {
                JOptionPane.showMessageDialog(this, "Zaposleni dodat.");
                txtJmbg.setBackground(UIManager.getColor("TextField.background"));
                ucitajZaposlene();
                ocistiFormu();
            }
            case 0 -> {
                txtJmbg.setBackground(new Color(255, 200, 200));
                JOptionPane.showMessageDialog(this,
                        "JMBG \"" + z.getJmbg() + "\" već postoji u sistemu.\n" +
                                "Svaki zaposleni mora imati jedinstven JMBG.",
                        "JMBG zauzet", JOptionPane.WARNING_MESSAGE);
                txtJmbg.requestFocus();
                txtJmbg.selectAll();
            }
            default -> JOptionPane.showMessageDialog(this,
                    "Greška prilikom dodavanja.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Mijenja podatke odabranog zaposlenog.
     */
    private void izmijeniZaposlenog() {
        int red = tabelaZaposleni.getSelectedRow();
        if (red == -1) {
            JOptionPane.showMessageDialog(this, "Odaberite zaposlenog iz tabele.");
            return;
        }
        int id = trenutniZaposleni.get(red).getIdZAPOSLENI();
        Zaposleni z = procitajIzForme(id);
        if (z == null) return;

        // Validacija JMBG formata
        if (!z.getJmbg().matches("\\d{13}")) {
            txtJmbg.setBackground(new Color(255, 200, 200));
            JOptionPane.showMessageDialog(this,
                    "JMBG mora sadržavati tačno 13 cifara.",
                    "Neispravan JMBG", JOptionPane.WARNING_MESSAGE);
            txtJmbg.requestFocus();
            return;
        }

        int rezultat = zaposleniDAO.izmijeniZaposlenog(z);
        switch (rezultat) {
            case 1 -> {
                JOptionPane.showMessageDialog(this, "Podaci ažurirani.");
                txtJmbg.setBackground(UIManager.getColor("TextField.background"));
                ucitajZaposlene();
                ocistiFormu();
            }
            case 0 -> {
                txtJmbg.setBackground(new Color(255, 200, 200));
                JOptionPane.showMessageDialog(this,
                        "JMBG \"" + z.getJmbg() + "\" već koristi drugi zaposleni.",
                        "JMBG zauzet", JOptionPane.WARNING_MESSAGE);
                txtJmbg.requestFocus();
                txtJmbg.selectAll();
            }
            default -> JOptionPane.showMessageDialog(this,
                    "Greška prilikom izmjene.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Briše odabranog zaposlenog iz baze podataka.
     */
    private void obrisiZaposlenog() {
        int red = tabelaZaposleni.getSelectedRow();
        if (red == -1) {
            JOptionPane.showMessageDialog(this, "Odaberite zaposlenog iz tabele.");
            return;
        }
        int id = trenutniZaposleni.get(red).getIdZAPOSLENI();
        int potvrda = JOptionPane.showConfirmDialog(this,
                "Obrisati zaposlenog? Ovo može uticati na povezane narudžbe/korisničke naloge.",
                "Potvrda", JOptionPane.YES_NO_OPTION);
        if (potvrda != JOptionPane.YES_OPTION) return;

        boolean ok = zaposleniDAO.obrisiZaposlenog(id);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Zaposleni obrisan.");
            ucitajZaposlene();
            ocistiFormu();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Greška — zaposleni možda ima vezane podatke (korisnički nalog, narudžbe, raspored).",
                    "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Kreira objekat zaposlenog na osnovu podataka unesenih u formu.
     */
    private Zaposleni procitajIzForme(Integer postojeciId) {
        String ime = txtIme.getText().trim();
        String prezime = txtPrezime.getText().trim();
        String pozicija = txtPozicija.getText().trim();
        String plataStr = txtPlata.getText().trim();
        String jmbg = txtJmbg.getText().trim();

        if (ime.isEmpty() || prezime.isEmpty() || pozicija.isEmpty() || plataStr.isEmpty() || jmbg.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Popunite sva polja.");
            return null;
        }

        double plata;
        try {
            plata = Double.parseDouble(plataStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Plata mora biti broj.");
            return null;
        }

        java.util.Date datumUtil = (java.util.Date) spinnerDatumZap.getValue();
        LocalDate datum = datumUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        Zaposleni z = new Zaposleni();
        if (postojeciId != null) z.setIdZAPOSLENI(postojeciId);
        z.setIme(ime);
        z.setPrezime(prezime);
        z.setPozicija(pozicija);
        z.setPlata(plata);
        z.setDatumZaposlenja(datum);
        z.setJmbg(jmbg);
        return z;
    }

    /**
     * Kreira panel za upravljanje korisničkim nalozima zaposlenih.
     *
     * @return panel sa formom za kreiranje korisničkog naloga
     */
    private JPanel createNaloziPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 60, 30, 60));

        JLabel lblNaslov = new JLabel("Kreiranje korisničkog naloga za zaposlenog", SwingConstants.CENTER);
        lblNaslov.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lblNaslov, BorderLayout.NORTH);

        JPanel forma = new JPanel(new GridLayout(5, 2, 10, 10));

        comboZaposleniNalog = new JComboBox<>();
        if (trenutniZaposleni != null) for (Zaposleni z : trenutniZaposleni) comboZaposleniNalog.addItem(z);
        comboZaposleniNalog.addActionListener(e -> provjeriPostojeciNalog());

        lblPostojeciNalog = new JLabel(" ");
        txtKorisnickoIme = new JTextField();
        txtLozinka = new JPasswordField();
        comboUloga = new JComboBox<>(new String[]{"ZAPOSLENI", "MENADZER"});

        forma.add(new JLabel("Zaposleni:"));       forma.add(comboZaposleniNalog);
        forma.add(new JLabel("Postojeći nalog:")); forma.add(lblPostojeciNalog);
        forma.add(new JLabel("Korisničko ime:"));  forma.add(txtKorisnickoIme);
        forma.add(new JLabel("Lozinka:"));         forma.add(txtLozinka);
        forma.add(new JLabel("Uloga:"));           forma.add(comboUloga);

        panel.add(forma, BorderLayout.CENTER);

        JButton btnKreiraj = new JButton("Kreiraj nalog");
        btnKreiraj.addActionListener(e -> kreirajNalog());
        JPanel dugmad = new JPanel();
        dugmad.add(btnKreiraj);
        panel.add(dugmad, BorderLayout.SOUTH);

        provjeriPostojeciNalog();
        return panel;
    }
    /**
     * Provjerava da li odabrani zaposleni već ima korisnički nalog
     * i prikazuje odgovarajuću informaciju.
     */
    private void provjeriPostojeciNalog() {
        Zaposleni z = (Zaposleni) comboZaposleniNalog.getSelectedItem();
        if (z == null) {
            lblPostojeciNalog.setText(" ");
            return;
        }
        Korisnik postojeci = korisnikDAO.getKorisnikZaZaposlenog(z.getIdZAPOSLENI());
        lblPostojeciNalog.setText(postojeci != null
                ? "Postoji: " + postojeci.getKorisnickoIme() + " (" + postojeci.getUloga() + ")"
                : "Nema naloga");
    }
    /**
     * Kreira korisnički nalog za odabranog zaposlenog.
     */
    private void kreirajNalog() {
        Zaposleni z = (Zaposleni) comboZaposleniNalog.getSelectedItem();
        if (z == null) {
            JOptionPane.showMessageDialog(this, "Odaberite zaposlenog.");
            return;
        }
        if (korisnikDAO.getKorisnikZaZaposlenog(z.getIdZAPOSLENI()) != null) {
            JOptionPane.showMessageDialog(this,
                    "Zaposleni već ima korisnički nalog.",
                    "Nalog postoji", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String korisnickoIme = txtKorisnickoIme.getText().trim();
        String lozinka = new String(txtLozinka.getPassword()).trim();

        if (korisnickoIme.isEmpty() || lozinka.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Popunite korisničko ime i lozinku.",
                    "Nepotpuni podaci", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Korisnik k = new Korisnik();
        k.setKorisnickoIme(korisnickoIme);
        k.setLozinka(lozinka);
        k.setUloga((String) comboUloga.getSelectedItem());
        k.setZaposleniId(z.getIdZAPOSLENI());

        int rezultat = korisnikDAO.dodajKorisnika(k);

        switch (rezultat) {
            case 1 -> {
                // Uspjeh
                JOptionPane.showMessageDialog(this,
                        "Nalog \"" + korisnickoIme + "\" uspješno kreiran.",
                        "Uspjeh", JOptionPane.INFORMATION_MESSAGE);
                ocistiFormuNalozi();
                provjeriPostojeciNalog();
            }
            case 0 -> {
                // Unique constraint — ime zauzeto
                JOptionPane.showMessageDialog(this,
                        "Korisničko ime \"" + korisnickoIme + "\" je već zauzeto.\n" +
                                "Odaberite drugo korisničko ime.",
                        "Korisničko ime zauzeto", JOptionPane.WARNING_MESSAGE);
                ocistiFormuNalozi();
                //txtKorisnickoIme.requestFocus();
            }
            default -> {
                // Neočekivana greška
                JOptionPane.showMessageDialog(this,
                        "Greška prilikom kreiranja naloga. Pokušajte ponovo.",
                        "Greška", JOptionPane.ERROR_MESSAGE);
                ocistiFormuNalozi();
            }
        }
    }


    /**
     * Kreira panel za upravljanje rasporedom rada zaposlenih.
     */
    private JPanel createRasporedPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel forma = new JPanel(new FlowLayout(FlowLayout.LEFT));

        comboZaposleniRaspored = new JComboBox<>();
        if (trenutniZaposleni != null) for (Zaposleni z : trenutniZaposleni) comboZaposleniRaspored.addItem(z);
        comboZaposleniRaspored.addActionListener(e -> ucitajRaspored());

        comboPoslovnicaRaspored = new JComboBox<>();
        for (Poslovnica p : poslovnicaDAO.getSvePoslovnice()) comboPoslovnicaRaspored.addItem(p);

        spinnerDatumRaspored = new JSpinner(new SpinnerDateModel());
        spinnerDatumRaspored.setEditor(new JSpinner.DateEditor(spinnerDatumRaspored, "yyyy-MM-dd"));
        spinnerDatumRaspored.setValue(new java.util.Date());

        comboSmjena = new JComboBox<>(new Integer[]{1, 2, 3});

        forma.add(new JLabel("Zaposleni:"));  forma.add(comboZaposleniRaspored);
        forma.add(new JLabel("Poslovnica:")); forma.add(comboPoslovnicaRaspored);
        forma.add(new JLabel("Datum:"));      forma.add(spinnerDatumRaspored);
        forma.add(new JLabel("Smjena:"));     forma.add(comboSmjena);

        JButton btnDodaj = new JButton("Dodaj u raspored");
        btnDodaj.addActionListener(e -> dodajRaspored());
        forma.add(btnDodaj);

        panel.add(forma, BorderLayout.NORTH);

        modelRaspored = new DefaultTableModel(new String[]{"Datum", "Poslovnica", "Smjena"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaRaspored = new JTable(modelRaspored);
        panel.add(new JScrollPane(tabelaRaspored), BorderLayout.CENTER);

        JButton btnObrisi = new JButton("Obriši odabrani raspored");
        btnObrisi.addActionListener(e -> obrisiRaspored());
        JPanel donji = new JPanel();
        donji.add(btnObrisi);
        panel.add(donji, BorderLayout.SOUTH);

        ucitajRaspored();
        return panel;
    }
    /**
     * Učitava raspored rada odabranog zaposlenog
     * za narednih četrnaest dana.
     */
    private void ucitajRaspored() {
        if (modelRaspored == null) return;
        modelRaspored.setRowCount(0);
        Zaposleni z = (Zaposleni) comboZaposleniRaspored.getSelectedItem();
        if (z == null) return;

        LocalDate danas = LocalDate.now();
        trenutniRaspored = rasporedDAO.getRasporedZaZaposlenog(z.getIdZAPOSLENI(), danas, danas.plusDays(13));
        for (RasporedUnos r : trenutniRaspored) {
            modelRaspored.addRow(new Object[]{r.getDatum(), r.getPoslovnicaNaziv(), r.getSmjena()});
        }
    }
    /**
     * Dodaje novi raspored rada za odabranog zaposlenog.
     */
    private void dodajRaspored() {
        Zaposleni z = (Zaposleni) comboZaposleniRaspored.getSelectedItem();
        Poslovnica p = (Poslovnica) comboPoslovnicaRaspored.getSelectedItem();
        if (z == null || p == null) {
            JOptionPane.showMessageDialog(this, "Odaberite zaposlenog i poslovnicu.");
            return;
        }

        java.util.Date datumUtil = (java.util.Date) spinnerDatumRaspored.getValue();
        LocalDate datum = datumUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int smjena = (Integer) comboSmjena.getSelectedItem();

        boolean ok = rasporedDAO.dodajRaspored(z.getIdZAPOSLENI(), p.getIdPOSLOVNICA(), datum, smjena);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Raspored dodat.");
            ucitajRaspored();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Greška — zaposleni već ima raspored za taj datum.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Briše odabrani raspored rada zaposlenog.
     */
    private void obrisiRaspored() {
        int red = tabelaRaspored.getSelectedRow();
        if (red == -1) {
            JOptionPane.showMessageDialog(this, "Odaberite red iz rasporeda.");
            return;
        }
        Zaposleni z = (Zaposleni) comboZaposleniRaspored.getSelectedItem();
        RasporedUnos r = trenutniRaspored.get(red);

        boolean ok = rasporedDAO.obrisiRaspored(z.getIdZAPOSLENI(), r.getPoslovnicaId(), r.getDatum());
        if (ok) {
            JOptionPane.showMessageDialog(this, "Obrisano.");
            ucitajRaspored();
        } else {
            JOptionPane.showMessageDialog(this, "Greška prilikom brisanja.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Kreira GUI panel za upravljanje poslovnicama.
     * Sadrži tabelu za prikaz, formu za unos i dugmad za CRUD operacije.
     *
     * @return JPanel komponentu spremnu za prikaz u tabu
     */
    private JPanel createPoslovnicePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modelPoslovnice = new DefaultTableModel(
                new String[]{"ID", "Naziv", "Adresa", "Telefon", "Email", "Radi od", "Radi do", "Mjesto (Posta)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaPoslovnice = new JTable(modelPoslovnice);
        tabelaPoslovnice.getSelectionModel().addListSelectionListener(e -> popuniFormuPoslovnica());
        panel.add(new JScrollPane(tabelaPoslovnice), BorderLayout.CENTER);

        JPanel forma = new JPanel(new GridLayout(4, 4, 5, 5));
        txtNazivP = new JTextField();
        txtAdresaP = new JTextField();
        txtTelefonP = new JTextField();
        txtEmailP = new JTextField();
        txtRadiOd = new JTextField();
        txtRadiDo = new JTextField();
        comboMjestoP = new JComboBox<>();
        for (Mjesto m : mjestoDAO.getSvaMjesta()) comboMjestoP.addItem(m);

        forma.add(new JLabel("Naziv:"));    forma.add(txtNazivP);
        forma.add(new JLabel("Adresa:"));   forma.add(txtAdresaP);
        forma.add(new JLabel("Telefon:"));  forma.add(txtTelefonP);
        forma.add(new JLabel("Email:"));    forma.add(txtEmailP);
        forma.add(new JLabel("Radi od (HH:mm:ss):")); forma.add(txtRadiOd);
        forma.add(new JLabel("Radi do (HH:mm:ss):")); forma.add(txtRadiDo);
        forma.add(new JLabel("Mjesto:"));   forma.add(comboMjestoP);

        JPanel dugmad = new JPanel();
        JButton btnDodaj = new JButton("Dodaj");
        btnDodaj.addActionListener(e -> dodajPoslovnicu());
        JButton btnIzmijeni = new JButton("Izmijeni odabrano");
        btnIzmijeni.addActionListener(e -> izmijeniPoslovnicu());
        JButton btnObrisi = new JButton("Obriši odabrano");
        btnObrisi.addActionListener(e -> obrisiPoslovnicu());
        JButton btnOcisti = new JButton("Očisti formu");
        btnOcisti.addActionListener(e -> ocistiFormuPoslovnica());
        dugmad.add(btnDodaj); dugmad.add(btnIzmijeni); dugmad.add(btnObrisi); dugmad.add(btnOcisti);

        JPanel donji = new JPanel(new BorderLayout());
        donji.add(forma, BorderLayout.CENTER);
        donji.add(dugmad, BorderLayout.SOUTH);
        panel.add(donji, BorderLayout.SOUTH);

        ucitajPoslovnice();
        return panel;
    }
    /**
     * Učitava sve poslovnice iz baze i prikazuje ih u JTable.
     * Takođe osvježava comboBox koji se koristi u drugim tabovima.
     */
    private void ucitajPoslovnice() {
        modelPoslovnice.setRowCount(0);
        trenutnePoslovnice = poslovnicaDAO.getSvePoslovnice();
        for (Poslovnica p : trenutnePoslovnice) {
            modelPoslovnice.addRow(new Object[]{
                    p.getIdPOSLOVNICA(), p.getNaziv(), p.getAdresa(), p.getTelefon(),
                    p.getEmail(), p.getRadiOd(), p.getRadiDo(), p.getMjestoPosta()
            });
        }

        osvjeziSvePoslovniceCombo();
    }
    /**
     * Popunjava formu podacima iz selektovane poslovnice u tabeli.
     */
    private void popuniFormuPoslovnica() {
        int red = tabelaPoslovnice.getSelectedRow();
        if (red == -1) return;
        Poslovnica p = trenutnePoslovnice.get(red);
        txtNazivP.setText(p.getNaziv());
        txtAdresaP.setText(p.getAdresa());
        txtTelefonP.setText(p.getTelefon());
        txtEmailP.setText(p.getEmail());
        txtRadiOd.setText(p.getRadiOd());
        txtRadiDo.setText(p.getRadiDo());
        for (int i = 0; i < comboMjestoP.getItemCount(); i++) {
            if (comboMjestoP.getItemAt(i).getPosta() == p.getMjestoPosta()) {
                comboMjestoP.setSelectedIndex(i);
                break;
            }
        }
    }
    /**
     * Briše sve unose iz forme i resetuje selekciju tabele.
     */
    private void ocistiFormuPoslovnica() {
        txtNazivP.setText(""); txtAdresaP.setText(""); txtTelefonP.setText("");
        txtEmailP.setText(""); txtRadiOd.setText(""); txtRadiDo.setText("");
        tabelaPoslovnice.clearSelection();
    }
    /**
     * Kreira objekat Poslovnica na osnovu unosa iz forme.
     *
     * @param postojeciId ako nije null → radi se o update operaciji
     * @return Poslovnica objekat ili null ako validacija ne prođe
     */
    private Poslovnica procitajPoslovnicuIzForme(Integer postojeciId) {
        String naziv = txtNazivP.getText().trim();
        String adresa = txtAdresaP.getText().trim();
        String radiOd = txtRadiOd.getText().trim();
        String radiDo = txtRadiDo.getText().trim();
        Mjesto mjesto = (Mjesto) comboMjestoP.getSelectedItem();

        if (naziv.isEmpty() || adresa.isEmpty() || radiOd.isEmpty() || radiDo.isEmpty() || mjesto == null) {
            JOptionPane.showMessageDialog(this, "Popunite naziv, adresu, radno vrijeme i mjesto.");
            return null;
        }

        Poslovnica p = new Poslovnica();
        if (postojeciId != null) p.setIdPOSLOVNICA(postojeciId);
        p.setNaziv(naziv);
        p.setAdresa(adresa);
        p.setTelefon(txtTelefonP.getText().trim());
        p.setEmail(txtEmailP.getText().trim());
        p.setRadiOd(radiOd);
        p.setRadiDo(radiDo);
        p.setMjestoPosta(mjesto.getPosta());
        return p;
    }
    /**
     * Dodaje novu poslovnicu u bazu podataka.
     */
    private void dodajPoslovnicu() {
        Poslovnica p = procitajPoslovnicuIzForme(null);
        if (p == null) return;
        if (poslovnicaDAO.dodajPoslovnicu(p)) {
            JOptionPane.showMessageDialog(this, "Poslovnica dodata.");
            ucitajPoslovnice();
            ocistiFormuPoslovnica();
        } else {
            JOptionPane.showMessageDialog(this, "Greška prilikom dodavanja.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Ažurira selektovanu poslovnicu.
     */
    private void izmijeniPoslovnicu() {
        int red = tabelaPoslovnice.getSelectedRow();
        if (red == -1) {
            JOptionPane.showMessageDialog(this, "Odaberite poslovnicu iz tabele.");
            return;
        }
        int id = trenutnePoslovnice.get(red).getIdPOSLOVNICA();
        Poslovnica p = procitajPoslovnicuIzForme(id);
        if (p == null) return;
        if (poslovnicaDAO.izmijeniPoslovnicu(p)) {
            JOptionPane.showMessageDialog(this, "Poslovnica ažurirana.");
            ucitajPoslovnice();
            ocistiFormuPoslovnica();
        } else {
            JOptionPane.showMessageDialog(this, "Greška prilikom izmjene.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Briše selektovanu poslovnicu uz potvrdu korisnika.
     */
    private void obrisiPoslovnicu() {
        int red = tabelaPoslovnice.getSelectedRow();
        if (red == -1) {
            JOptionPane.showMessageDialog(this, "Odaberite poslovnicu iz tabele.");
            return;
        }
        int id = trenutnePoslovnice.get(red).getIdPOSLOVNICA();
        int potvrda = JOptionPane.showConfirmDialog(this,
                "Obrisati poslovnicu? Ovo može uticati na povezane narudžbe, zalihe i raspored.",
                "Potvrda", JOptionPane.YES_NO_OPTION);
        if (potvrda != JOptionPane.YES_OPTION) return;

        if (poslovnicaDAO.obrisiPoslovnicu(id)) {
            JOptionPane.showMessageDialog(this, "Poslovnica obrisana.");
            ucitajPoslovnice();
            ocistiFormuPoslovnica();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Greška — poslovnica ima vezane podatke (zalihe, narudžbe, raspored).",
                    "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Kreira GUI panel za upravljanje dobavljačima.
     * Panel sadrži tabelu za prikaz, formu za unos i dugmad za CRUD operacije.
     **/
    private JPanel createDobavljaciPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modelDobavljaci = new DefaultTableModel(
                new String[]{"ID", "Naziv", "Adresa", "Telefon", "Email", "Mjesto (Posta)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaDobavljaci = new JTable(modelDobavljaci);
        tabelaDobavljaci.getSelectionModel().addListSelectionListener(e -> popuniFormuDobavljac());
        panel.add(new JScrollPane(tabelaDobavljaci), BorderLayout.CENTER);

        JPanel forma = new JPanel(new GridLayout(3, 4, 5, 5));
        txtNazivD = new JTextField();
        txtAdresaD = new JTextField();
        txtTelefonD = new JTextField();
        txtEmailD = new JTextField();
        comboMjestoD = new JComboBox<>();
        for (Mjesto m : mjestoDAO.getSvaMjesta()) comboMjestoD.addItem(m);

        forma.add(new JLabel("Naziv:"));   forma.add(txtNazivD);
        forma.add(new JLabel("Adresa:"));  forma.add(txtAdresaD);
        forma.add(new JLabel("Telefon:")); forma.add(txtTelefonD);
        forma.add(new JLabel("Email:"));   forma.add(txtEmailD);
        forma.add(new JLabel("Mjesto:"));  forma.add(comboMjestoD);

        JPanel dugmad = new JPanel();
        JButton btnDodaj = new JButton("Dodaj");
        btnDodaj.addActionListener(e -> dodajDobavljaca());
        JButton btnIzmijeni = new JButton("Izmijeni odabrano");
        btnIzmijeni.addActionListener(e -> izmijeniDobavljaca());
        JButton btnObrisi = new JButton("Obriši odabrano");
        btnObrisi.addActionListener(e -> obrisiDobavljaca());
        JButton btnOcisti = new JButton("Očisti formu");
        btnOcisti.addActionListener(e -> ocistiFormuDobavljac());
        dugmad.add(btnDodaj); dugmad.add(btnIzmijeni); dugmad.add(btnObrisi); dugmad.add(btnOcisti);

        JPanel donji = new JPanel(new BorderLayout());
        donji.add(forma, BorderLayout.CENTER);
        donji.add(dugmad, BorderLayout.SOUTH);
        panel.add(donji, BorderLayout.SOUTH);

        ucitajDobavljace();
        return panel;
    }
    /**
     * Učitava sve dobavljače iz baze i prikazuje ih u JTable.
     * Svježi podaci se dohvaćaju iz DAO sloja.
     */
    private void ucitajDobavljace() {
        modelDobavljaci.setRowCount(0);
        trenutniDobavljaci = dobavljacDAO.getSviDobavljaci();
        for (Dobavljac d : trenutniDobavljaci) {
            modelDobavljaci.addRow(new Object[]{
                    d.getIdDOBAVLJAC(), d.getNaziv(), d.getAdresa(), d.getTelefon(), d.getEmail(), d.getMjestoPosta()
            });
        }
        if (comboDobavljacNabavka != null) {
            comboDobavljacNabavka.removeAllItems();
            for (Dobavljac d : trenutniDobavljaci) {
                comboDobavljacNabavka.addItem(d);
            }
        }
    }
    /**
     * Popunjava formu podacima iz selektovanog dobavljača u tabeli.
     */
    private void popuniFormuDobavljac() {
        int red = tabelaDobavljaci.getSelectedRow();
        if (red == -1) return;
        Dobavljac d = trenutniDobavljaci.get(red);
        txtNazivD.setText(d.getNaziv());
        txtAdresaD.setText(d.getAdresa());
        txtTelefonD.setText(d.getTelefon());
        txtEmailD.setText(d.getEmail());
        for (int i = 0; i < comboMjestoD.getItemCount(); i++) {
            if (comboMjestoD.getItemAt(i).getPosta() == d.getMjestoPosta()) {
                comboMjestoD.setSelectedIndex(i);
                break;
            }
        }
    }
    /**
     * Briše sve unose iz forme i resetuje selekciju tabele.
     */
    private void ocistiFormuDobavljac() {
        txtNazivD.setText(""); txtAdresaD.setText(""); txtTelefonD.setText(""); txtEmailD.setText("");
        tabelaDobavljaci.clearSelection();
    }
    /**
     * Kreira objekat Dobavljac na osnovu unosa iz forme.
     *
     * @param postojeciId ako nije null → radi se o UPDATE operaciji
     * @return Dobavljac objekat ili null ako validacija ne prođe
     */
    private Dobavljac procitajDobavljacaIzForme(Integer postojeciId) {
        String naziv = txtNazivD.getText().trim();
        String adresa = txtAdresaD.getText().trim();
        Mjesto mjesto = (Mjesto) comboMjestoD.getSelectedItem();

        if (naziv.isEmpty() || adresa.isEmpty() || mjesto == null) {
            JOptionPane.showMessageDialog(this, "Popunite naziv, adresu i mjesto.");
            return null;
        }

        Dobavljac d = new Dobavljac();
        if (postojeciId != null) d.setIdDOBAVLJAC(postojeciId);
        d.setNaziv(naziv);
        d.setAdresa(adresa);
        d.setTelefon(txtTelefonD.getText().trim());
        d.setEmail(txtEmailD.getText().trim());
        d.setMjestoPosta(mjesto.getPosta());
        return d;
    }
    /**
     * Dodaje novog dobavljača u bazu.
     */
    private void dodajDobavljaca() {
        Dobavljac d = procitajDobavljacaIzForme(null);
        if (d == null) return;
        if (dobavljacDAO.dodajDobavljaca(d)) {
            JOptionPane.showMessageDialog(this, "Dobavljač dodat.");
            ucitajDobavljace();
            ocistiFormuDobavljac();
        } else {
            JOptionPane.showMessageDialog(this, "Greška prilikom dodavanja.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Ažurira selektovanog dobavljača.
     */
    private void izmijeniDobavljaca() {
        int red = tabelaDobavljaci.getSelectedRow();
        if (red == -1) {
            JOptionPane.showMessageDialog(this, "Odaberite dobavljača iz tabele.");
            return;
        }
        int id = trenutniDobavljaci.get(red).getIdDOBAVLJAC();
        Dobavljac d = procitajDobavljacaIzForme(id);
        if (d == null) return;
        if (dobavljacDAO.izmijeniDobavljaca(d)) {
            JOptionPane.showMessageDialog(this, "Dobavljač ažuriran.");
            ucitajDobavljace();
            ocistiFormuDobavljac();
        } else {
            JOptionPane.showMessageDialog(this, "Greška prilikom izmjene.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Briše selektovanog dobavljača uz potvrdu korisnika.
     */
    private void obrisiDobavljaca() {
        int red = tabelaDobavljaci.getSelectedRow();
        if (red == -1) {
            JOptionPane.showMessageDialog(this, "Odaberite dobavljača iz tabele.");
            return;
        }
        int id = trenutniDobavljaci.get(red).getIdDOBAVLJAC();
        int potvrda = JOptionPane.showConfirmDialog(this,
                "Obrisati dobavljača?", "Potvrda", JOptionPane.YES_NO_OPTION);
        if (potvrda != JOptionPane.YES_OPTION) return;

        if (dobavljacDAO.obrisiDobavljaca(id)) {
            JOptionPane.showMessageDialog(this, "Dobavljač obrisan.");
            ucitajDobavljace();
            ocistiFormuDobavljac();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Greška — dobavljač ima vezane nabavne narudžbe.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Kreira glavni tab za upravljanje nabavnim narudžbama.
     * Sadrži dva pod-taba:
     *  - Kreiranje nove nabavne narudžbe
     *  - Pregled postojećih narudžbi
     */
    private JPanel createNabavnePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTabbedPane subTabs = new JTabbedPane();
        subTabs.addTab("Kreiranje narudžbe", createKreiranjeNabavkePanel());
        subTabs.addTab("Pregled narudžbi", createPregledNabavkiPanel());
        panel.add(subTabs, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Kreira UI za unos nove nabavne narudžbe.
     * Omogućava:
     * - prikaz niskih zalihe
     * - izbor dobavljača i poslovnice
     * - dodavanje stavki narudžbe
     *
     */
    private JPanel createKreiranjeNabavkePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Gornji dio: prikaz niskih zaliha ---
        JPanel gornji = new JPanel(new BorderLayout(5, 5));
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboPoslovnicaNabavka = new JComboBox<>();
        for (Poslovnica p : poslovnicaDAO.getSvePoslovnice()) comboPoslovnicaNabavka.addItem(p);
        comboPoslovnicaNabavka.addActionListener(e -> ucitajNiskuZalihu());
        filterPanel.add(new JLabel("Poslovnica:"));
        filterPanel.add(comboPoslovnicaNabavka);
        gornji.add(filterPanel, BorderLayout.NORTH);

        modelNiskaZaliha = new DefaultTableModel(
                new String[]{"ID", "Lijek", "Trenutna zaliha", "Minimalna količina", "Nedostaje"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabelaNiska = new JTable(modelNiskaZaliha);
        gornji.add(new JScrollPane(tabelaNiska), BorderLayout.CENTER);
        gornji.setPreferredSize(new Dimension(900, 180));
        panel.add(gornji, BorderLayout.NORTH);

        // --- Sredina: header narudžbe ---
        JPanel forma = new JPanel(new GridLayout(0, 2, 5, 5));
        comboDobavljacNabavka = new JComboBox<>();
        for (Dobavljac d : dobavljacDAO.getSviDobavljaci()) comboDobavljacNabavka.addItem(d);
        txtNapomenaNabavka = new JTextField();

        JButton btnKreiraj = new JButton("Kreiraj novu nabavnu narudžbu");
        btnKreiraj.addActionListener(e -> kreirajNovuNabavnu());
        lblAktivnaNabavka = new JLabel("Nema aktivne narudžbe.");

        forma.add(new JLabel("Dobavljač:")); forma.add(comboDobavljacNabavka);
        forma.add(new JLabel("Napomena:"));  forma.add(txtNapomenaNabavka);
        forma.add(new JLabel(""));           forma.add(btnKreiraj);
        forma.add(new JLabel("Status:"));    forma.add(lblAktivnaNabavka);

        // --- Donji dio: dodavanje stavki ---
        JPanel stavkePanel = new JPanel(new BorderLayout(5, 5));

        // Panel za odabir lijeka (iz baze ILI ručni unos)
        JPanel lijekPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        lijekPanel.setBorder(BorderFactory.createTitledBorder("Lijek"));

        comboLijekStavka = new JComboBox<>();
        for (Lijek l : lijekDAO.getSviLijekovi()) comboLijekStavka.addItem(l);

        chkNoviLijek = new JCheckBox("Unesi novi lijek (nije u bazi)");
        txtNovLijekNaziv = new JTextField();
        txtNovLijekNaziv.setEnabled(false);
        txtNovLijekNaziv.setToolTipText("Unesite naziv novog lijeka koji nije u bazi");

        // Toggleovanje između combo i text polja
        chkNoviLijek.addActionListener(e -> {
            boolean noviMode = chkNoviLijek.isSelected();
            comboLijekStavka.setEnabled(!noviMode);
            txtNovLijekNaziv.setEnabled(noviMode);
            if (noviMode) txtNovLijekNaziv.requestFocus();
        });

        lijekPanel.add(new JLabel("Lijek iz baze:"));    lijekPanel.add(comboLijekStavka);
        lijekPanel.add(chkNoviLijek);                     lijekPanel.add(new JLabel(""));
        lijekPanel.add(new JLabel("Naziv novog lijeka:")); lijekPanel.add(txtNovLijekNaziv);

        // Panel za količinu i rok — BEZ cijene
        JPanel kolicinaPanal = new JPanel(new FlowLayout(FlowLayout.LEFT));
        spinnerKolicinaStavka = new JSpinner(new SpinnerNumberModel(1, 1, 100000, 1));

        spinnerRokTrajanja = new JSpinner(new SpinnerDateModel());
        spinnerRokTrajanja.setEditor(new JSpinner.DateEditor(spinnerRokTrajanja, "yyyy-MM-dd"));
        spinnerRokTrajanja.setValue(Date.valueOf(LocalDate.now().plusYears(1)));

        JButton btnDodajStavku = new JButton("Dodaj stavku u narudžbu");
        btnDodajStavku.addActionListener(e -> dodajStavkuNabavke());

        kolicinaPanal.add(new JLabel("Količina:"));     kolicinaPanal.add(spinnerKolicinaStavka);
        kolicinaPanal.add(new JLabel("Rok trajanja:")); kolicinaPanal.add(spinnerRokTrajanja);
        kolicinaPanal.add(btnDodajStavku);

        JPanel formaStavke = new JPanel(new BorderLayout(5, 5));
        formaStavke.add(lijekPanel, BorderLayout.NORTH);
        formaStavke.add(kolicinaPanal, BorderLayout.SOUTH);

        stavkePanel.add(formaStavke, BorderLayout.NORTH);

        // Tabela stavki — bez kolone za cijenu
        modelStavkeNabavke = new DefaultTableModel(
                new String[]{"Lijek", "Količina", "Rok trajanja", "Napomena"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        stavkePanel.add(new JScrollPane(new JTable(modelStavkeNabavke)), BorderLayout.CENTER);

        // Dugme za uklanjanje stavke
        JButton btnUkloniStavku = new JButton("Ukloni odabranu stavku");
        btnUkloniStavku.addActionListener(e -> {
            JTable t = (JTable) ((JScrollPane) stavkePanel.getComponent(1)).getViewport().getView();
            int red = t.getSelectedRow();
            if (red == -1) {
                JOptionPane.showMessageDialog(this, "Odaberite stavku za uklanjanje.");
                return;
            }
            modelStavkeNabavke.removeRow(red);
        });
        JPanel dugmadStavke = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dugmadStavke.add(btnUkloniStavku);
        stavkePanel.add(dugmadStavke, BorderLayout.SOUTH);

        JPanel centar = new JPanel(new BorderLayout(5, 5));
        centar.add(forma, BorderLayout.NORTH);
        centar.add(stavkePanel, BorderLayout.CENTER);
        panel.add(centar, BorderLayout.CENTER);

        ucitajNiskuZalihu();
        return panel;
    }

    /**
     * Učitava i prikazuje lijekove sa niskom zalihom za izabranu poslovnicu.
     * Koristi se za automatsko generisanje nabavnih potreba.
     */
    private void ucitajNiskuZalihu() {
        if (modelNiskaZaliha == null) return;
        modelNiskaZaliha.setRowCount(0);
        Poslovnica p = (Poslovnica) comboPoslovnicaNabavka.getSelectedItem();
        if (p == null) return;
        for (NiskaZaliha n : zalihaDAO.getNiskaZaliha(p.getIdPOSLOVNICA())) {
            modelNiskaZaliha.addRow(new Object[]{
                    n.getIdLijek(), n.getNaziv(), n.getTrenutnaZaliha(), n.getMinimalnaKolicina(), n.getNedostaje()
            });
        }
    }
    /**
     * Kreira novu nabavnu narudžbu u sistemu.
     *
     * Proces:
     * - provjera izabrane poslovnice i dobavljača
     * - kreiranje zaglavlja narudžbe u bazi
     * - aktiviranje "session" narudžbe za dodavanje stavki
     */
    private void kreirajNovuNabavnu() {
        Poslovnica p = (Poslovnica) comboPoslovnicaNabavka.getSelectedItem();
        Dobavljac d = (Dobavljac) comboDobavljacNabavka.getSelectedItem();
        if (p == null || d == null) {
            JOptionPane.showMessageDialog(this, "Odaberite poslovnicu i dobavljača.");
            return;
        }
        String napomena = txtNapomenaNabavka.getText().trim();

        int id = nabavnaDAO.kreirajNabavnu(d.getIdDOBAVLJAC(), zaposleniId, p.getIdPOSLOVNICA(), napomena);
        if (id == -1) {
            JOptionPane.showMessageDialog(this, "Greška prilikom kreiranja narudžbe.", "Greška", JOptionPane.ERROR_MESSAGE);
            return;
        }

        aktivnaNabavkaId = id;
        modelStavkeNabavke.setRowCount(0);
        lblAktivnaNabavka.setText("Aktivna narudžba: #" + id + " — dodajte stavke ispod.");
        JOptionPane.showMessageDialog(this, "Narudžba #" + id + " kreirana. Dodajte stavke.");
    }
    /**
     * Dodaje stavku u aktivnu nabavnu narudžbu.
     */
    private void dodajStavkuNabavke() {
        if (aktivnaNabavkaId == -1) {
            JOptionPane.showMessageDialog(this, "Prvo kreirajte novu nabavnu narudžbu.");
            return;
        }

        int kolicina = (Integer) spinnerKolicinaStavka.getValue();
        java.util.Date datumUtil = (java.util.Date) spinnerRokTrajanja.getValue();
        LocalDate rok = datumUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        if (chkNoviLijek.isSelected()) {
            String nazivNovog = txtNovLijekNaziv.getText().trim();
            if (nazivNovog.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Unesite naziv novog lijeka.",
                        "Nedostaje naziv", JOptionPane.WARNING_MESSAGE);
                txtNovLijekNaziv.requestFocus();
                return;
            }

            // Provjeri postoji li lijek u bazi po nazivu (case-insensitive)
            Lijek pronadjen = lijekDAO.getLijekPoNazivu(nazivNovog);

            int lijekId;

            if (pronadjen != null) {
                // Lijek pronađen u bazi — koristi postojeći
                lijekId = pronadjen.getIdLIJEK();

                boolean ok = nabavnaDAO.dodajStavku(aktivnaNabavkaId, lijekId, kolicina, 0.0, rok);
                if (ok) {
                    modelStavkeNabavke.addRow(new Object[]{
                            pronadjen.getNaziv(), kolicina, rok, ""
                    });
                    txtNovLijekNaziv.setText("");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Greška pri dodavanju stavke.", "Greška", JOptionPane.ERROR_MESSAGE);
                }

            } else {
                List<Kategorija> kategorije = kategorijaDAO.getSveKategorije();
                if (kategorije.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Nema kategorija u bazi. Dodajte kategoriju pa pokušajte ponovo.",
                            "Nema kategorija", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Kategorija odabranaKat = (Kategorija) JOptionPane.showInputDialog(
                        this,
                        "Lijek \"" + nazivNovog + "\" nije pronađen u bazi.\n" +
                                "Odaberite kategoriju za novi lijek:",
                        "Odabir kategorije",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        kategorije.toArray(new Kategorija[0]),
                        kategorije.get(0)
                );

                if (odabranaKat == null) return; // menadžer otkazao dijalog

                lijekId = lijekDAO.dodajNovLijekMinimalan(nazivNovog, odabranaKat.getIdKategorija());
                if (lijekId == -1) {
                    JOptionPane.showMessageDialog(this,
                            "Greška pri dodavanju novog lijeka u bazu.",
                            "Greška", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean ok = nabavnaDAO.dodajStavku(aktivnaNabavkaId, lijekId, kolicina, 0.0, rok);
                if (ok) {
                    modelStavkeNabavke.addRow(new Object[]{
                            nazivNovog + " ★", kolicina, rok, "Novi lijek — uredi katalog"
                    });
                    txtNovLijekNaziv.setText("");

                    // Osvježi combo da novi lijek odmah bude dostupan
                    comboLijekStavka.removeAllItems();
                    for (Lijek l : lijekDAO.getSviLijekovi()) comboLijekStavka.addItem(l);

                    JOptionPane.showMessageDialog(this,
                            "Lijek \"" + nazivNovog + "\" dodat u katalog (★).\n" +
                                    "Uredite detalje (cijena, recept) u katalogu lijekova.",
                            "Novi lijek dodat", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Greška pri dodavanju stavke.", "Greška", JOptionPane.ERROR_MESSAGE);
                }
            }

        } else {
            Lijek l = (Lijek) comboLijekStavka.getSelectedItem();
            if (l == null) {
                JOptionPane.showMessageDialog(this, "Odaberite lijek iz liste.");
                return;
            }

            boolean ok = nabavnaDAO.dodajStavku(aktivnaNabavkaId, l.getIdLIJEK(), kolicina, 0.0, rok);
            if (ok) {
                modelStavkeNabavke.addRow(new Object[]{
                        l.getNaziv(), kolicina, rok, ""
                });
            } else {
                JOptionPane.showMessageDialog(this,
                        "Greška pri dodavanju stavke.", "Greška", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    /**
     * Kreira panel za pregled svih nabavnih narudžbi.
     * Omogućava:
     * - filtriranje po poslovnici
     * - pregled detalja
     * - promjenu statusa
     */
    private JPanel createPregledNabavkiPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboPoslovnicaPregled = new JComboBox<>();
        for (Poslovnica p : poslovnicaDAO.getSvePoslovnice()) comboPoslovnicaPregled.addItem(p);
        comboPoslovnicaPregled.addActionListener(e -> ucitajNabavneNarudzbe());
        filterPanel.add(new JLabel("Poslovnica:"));
        filterPanel.add(comboPoslovnicaPregled);

        JButton btnOsvjezi = new JButton("Osvježi");
        btnOsvjezi.addActionListener(e -> ucitajNabavneNarudzbe());
        filterPanel.add(btnOsvjezi);

        panel.add(filterPanel, BorderLayout.NORTH);

        modelNabavneNarudzbe = new DefaultTableModel(
                new String[]{"ID", "Datum narudžbe", "Dobavljač", "Status", "Napomena"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaNabavneNarudzbe = new JTable(modelNabavneNarudzbe);
        panel.add(new JScrollPane(tabelaNabavneNarudzbe), BorderLayout.CENTER);

        JPanel dugmad = new JPanel();
        JButton btnDetalji = new JButton("Detalji (stavke)");
        btnDetalji.addActionListener(e -> prikaziStavkeNabavne());
        JButton btnIsporuceno = new JButton("Markiraj kao isporučena");
        btnIsporuceno.addActionListener(e -> markirajIsporuceno());
        dugmad.add(btnDetalji);
        dugmad.add(btnIsporuceno);
        panel.add(dugmad, BorderLayout.SOUTH);

        ucitajNabavneNarudzbe();
        return panel;
    }
    /**
     * Učitava sve nabavne narudžbe za izabranu poslovnicu.
     * Podaci se prikazuju u tabeli.
     */
    private void ucitajNabavneNarudzbe() {
        if (modelNabavneNarudzbe == null) return;
        modelNabavneNarudzbe.setRowCount(0);
        Poslovnica p = (Poslovnica) comboPoslovnicaPregled.getSelectedItem();
        if (p == null) return;
        trenutneNabavne = nabavnaDAO.getNabavneZaPoslovnicu(p.getIdPOSLOVNICA());
        for (NabavnaNarudzba n : trenutneNabavne) {
            modelNabavneNarudzbe.addRow(new Object[]{
                    n.getIdNabavnaNarudzba(), n.getDatumNarudzbe(), n.getDobavljac(),
                    n.getStatus(), n.getNapomena()
            });
        }
    }
    /**
     * Prikazuje detalje (stavke) odabrane nabavne narudžbe.
     */
    private void prikaziStavkeNabavne() {
        int red = tabelaNabavneNarudzbe.getSelectedRow();
        if (red == -1) {
            JOptionPane.showMessageDialog(this, "Odaberite narudžbu iz tabele.");
            return;
        }
        NabavnaNarudzba n = trenutneNabavne.get(red);
        List<NabavnaStavka> stavke = nabavnaDAO.getStavkeNabavne(n.getIdNabavnaNarudzba());

        StringBuilder sb = new StringBuilder();
        sb.append("Nabavna narudžba #").append(n.getIdNabavnaNarudzba()).append("\n");
        sb.append("Dobavljač: ").append(n.getDobavljac()).append("\n");
        sb.append("Status: ").append(n.getStatus()).append("\n\n");
        for (NabavnaStavka s : stavke) {
            sb.append(String.format("  %-30s x%-5d  (rok trajanja: %s)%n",
                    s.getLijek(), s.getKolicina(), s.getRokTrajanja()));
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Detalji nabavne narudžbe", JOptionPane.PLAIN_MESSAGE);
    }
    /**
     * Označava nabavnu narudžbu kao isporučenu.
     *
     * Efekat:
     * - mijenja status narudžbe
     * - automatski povećava zalihe lijekova
     */
    private void markirajIsporuceno() {
        int red = tabelaNabavneNarudzbe.getSelectedRow();
        if (red == -1) {
            JOptionPane.showMessageDialog(this, "Odaberite narudžbu iz tabele.");
            return;
        }
        NabavnaNarudzba n = trenutneNabavne.get(red);
        if ("Isporucena".equals(n.getStatus())) {
            JOptionPane.showMessageDialog(this, "Narudžba je već isporučena.");
            return;
        }

        int potvrda = JOptionPane.showConfirmDialog(this,
                "Markirati narudžbu #" + n.getIdNabavnaNarudzba() + " kao isporučenu?\n" +
                        "Ovo će automatski povećati zalihe lijekova.",
                "Potvrda", JOptionPane.YES_NO_OPTION);
        if (potvrda != JOptionPane.YES_OPTION) return;

        boolean ok = nabavnaDAO.promijeniStatus(n.getIdNabavnaNarudzba(), "Isporucena", LocalDate.now());
        if (ok) {
            JOptionPane.showMessageDialog(this, "Narudžba ažurirana. Zalihe su povećane.");
            ucitajNabavneNarudzbe();
        } else {
            JOptionPane.showMessageDialog(this, "Greška prilikom ažuriranja.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Kreira glavni tab za rad sa izvještajima.
     * Sadrži podtabove za generisanje izvještaja o prometu
     * i pregled prethodno sačuvanih izvještaja.
     */
    private JPanel createIzvjestajiPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTabbedPane subTabs = new JTabbedPane();
        subTabs.addTab("Promet", createPrometPanel());
        subTabs.addTab("Sačuvani izvještaji", createSacuvaniPanel());
        panel.add(subTabs, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Kreira panel za generisanje izvještaja o prometu.
     * Omogućava izbor poslovnice i vremenskog perioda,
     * kao i prikaz rezultata u tabeli.
     */
    private JPanel createPrometPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        comboPoslovnicaPromet = new JComboBox<>();
        for (Poslovnica p : poslovnicaDAO.getSvePoslovnice()) comboPoslovnicaPromet.addItem(p);

        spinnerPeriodOd = new JSpinner(new SpinnerDateModel());
        spinnerPeriodOd.setEditor(new JSpinner.DateEditor(spinnerPeriodOd, "yyyy-MM-dd"));
        spinnerPeriodOd.setValue(Date.valueOf(LocalDate.now().withDayOfMonth(1)));

        spinnerPeriodDo = new JSpinner(new SpinnerDateModel());
        spinnerPeriodDo.setEditor(new JSpinner.DateEditor(spinnerPeriodDo, "yyyy-MM-dd"));
        spinnerPeriodDo.setValue(Date.valueOf(LocalDate.now()));

        JButton btnGenerisi = new JButton("Generiši izvještaj");
        btnGenerisi.addActionListener(e -> generisiIzvjestaj());

        JButton btnSacuvaj = new JButton("Sačuvaj izvještaj");
        btnSacuvaj.addActionListener(e -> sacuvajPrometIzvjestaj());

        filterPanel.add(new JLabel("Poslovnica:"));  filterPanel.add(comboPoslovnicaPromet);
        filterPanel.add(new JLabel("Period od:"));   filterPanel.add(spinnerPeriodOd);
        filterPanel.add(new JLabel("Period do:"));   filterPanel.add(spinnerPeriodDo);
        filterPanel.add(btnGenerisi);
        filterPanel.add(btnSacuvaj);

        panel.add(filterPanel, BorderLayout.NORTH);

        modelIzvjestajPromet = new DefaultTableModel(
                new String[]{"Lijek", "Ukupno prodano", "Ukupan prihod"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        panel.add(new JScrollPane(new JTable(modelIzvjestajPromet)), BorderLayout.CENTER);

        lblUkupanPrihod = new JLabel("Ukupan prihod: 0.00 KM", SwingConstants.RIGHT);
        lblUkupanPrihod.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblUkupanPrihod, BorderLayout.SOUTH);

        return panel;
    }
    /**
     * Generiše izvještaj o prometu za odabranu poslovnicu
     * i zadati vremenski period.
     * Rezultati se prikazuju u tabeli zajedno sa ukupnim prihodom.
     */
    private void generisiIzvjestaj() {
        Poslovnica p = (Poslovnica) comboPoslovnicaPromet.getSelectedItem();
        if (p == null) return;

        LocalDate od = toLocalDate(spinnerPeriodOd);
        LocalDate doDatum = toLocalDate(spinnerPeriodDo);

        if (od.isAfter(doDatum)) {
            JOptionPane.showMessageDialog(this, "'Period od' mora biti prije 'Period do'.");
            return;
        }

        trenutniIzvjestaj = izvjestajDAO.getPrometIzvjestaj(p.getIdPOSLOVNICA(), od, doDatum);
        modelIzvjestajPromet.setRowCount(0);

        double ukupno = 0;
        for (StavkaIzvjestaja s : trenutniIzvjestaj) {
            modelIzvjestajPromet.addRow(new Object[]{
                    s.getLijek(), s.getUkupnoProdano(), String.format("%.2f KM", s.getUkupanPrihod())
            });
            ukupno += s.getUkupanPrihod();
        }

        lblUkupanPrihod.setText(String.format("Ukupan prihod: %.2f KM", ukupno));

        if (trenutniIzvjestaj.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nema podataka o prometu za odabrani period.");
        }
    }
    /**
     * Čuva trenutno generisani izvještaj o prometu u bazu podataka.
     * Sadržaj izvještaja se formira kao tekstualni prikaz
     * i povezuje sa zaposlenim koji ga je kreirao.
     */
    private void sacuvajPrometIzvjestaj() {
        if (trenutniIzvjestaj == null || trenutniIzvjestaj.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Prvo generišite izvještaj.");
            return;
        }

        Poslovnica p = (Poslovnica) comboPoslovnicaPromet.getSelectedItem();
        LocalDate od = toLocalDate(spinnerPeriodOd);
        LocalDate doDatum = toLocalDate(spinnerPeriodDo);

        StringBuilder sb = new StringBuilder();
        sb.append("IZVJEŠTAJ O PROMETU\n");
        sb.append("Poslovnica: ").append(p.getNaziv()).append("\n");
        sb.append("Period: ").append(od).append(" — ").append(doDatum).append("\n\n");
        sb.append(String.format("%-30s %15s %15s%n", "Lijek", "Kolicina", "Prihod (KM)"));
        sb.append("-".repeat(62)).append("\n");

        double ukupno = 0;
        for (StavkaIzvjestaja s : trenutniIzvjestaj) {
            sb.append(String.format("%-30s %15d %15.2f%n", s.getLijek(), s.getUkupnoProdano(), s.getUkupanPrihod()));
            ukupno += s.getUkupanPrihod();
        }
        sb.append("-".repeat(62)).append("\n");
        sb.append(String.format("UKUPAN PRIHOD: %.2f KM%n", ukupno));

        boolean ok = izvjestajDAO.sacuvajIzvjestaj("Promet", od, doDatum, zaposleniId, p.getIdPOSLOVNICA(), sb.toString());
        if (ok) {
            JOptionPane.showMessageDialog(this, "Izvještaj sačuvan.");
        } else {
            JOptionPane.showMessageDialog(this, "Greška prilikom čuvanja izvještaja.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

    private LocalDate toLocalDate(JSpinner spinner) {
        java.util.Date datumUtil = (java.util.Date) spinner.getValue();
        return datumUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Kreira panel za pregled prethodno sačuvanih izvještaja.
     * Omogućava filtriranje po poslovnici i pregled sadržaja izvještaja.
     */
    private JPanel createSacuvaniPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        comboPoslovnicaSacuvani = new JComboBox<>();
        for (Poslovnica p : poslovnicaDAO.getSvePoslovnice()) comboPoslovnicaSacuvani.addItem(p);
        comboPoslovnicaSacuvani.addActionListener(e -> ucitajSacuvaneIzvjestaje());

        JButton btnOsvjezi = new JButton("Osvježi");
        btnOsvjezi.addActionListener(e -> ucitajSacuvaneIzvjestaje());

        filterPanel.add(new JLabel("Poslovnica:"));
        filterPanel.add(comboPoslovnicaSacuvani);
        filterPanel.add(btnOsvjezi);
        panel.add(filterPanel, BorderLayout.NORTH);

        modelSacuvaniIzvjestaji = new DefaultTableModel(
                new String[]{"ID", "Tip", "Datum generisanja", "Period od", "Period do"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaSacuvaniIzvjestaji = new JTable(modelSacuvaniIzvjestaji);
        panel.add(new JScrollPane(tabelaSacuvaniIzvjestaji), BorderLayout.CENTER);

        JButton btnPrikazi = new JButton("Prikaži sadržaj");
        btnPrikazi.addActionListener(e -> prikaziSadrzajIzvjestaja());
        JPanel dugmad = new JPanel();
        dugmad.add(btnPrikazi);
        panel.add(dugmad, BorderLayout.SOUTH);

        ucitajSacuvaneIzvjestaje();
        return panel;
    }
    /**
     * Učitava sve sačuvane izvještaje za odabranu poslovnicu
     * i prikazuje ih u tabeli.
     */
    private void ucitajSacuvaneIzvjestaje() {
        if (modelSacuvaniIzvjestaji == null) return;
        modelSacuvaniIzvjestaji.setRowCount(0);
        Poslovnica p = (Poslovnica) comboPoslovnicaSacuvani.getSelectedItem();
        if (p == null) return;

        trenutniSacuvani = izvjestajDAO.getSacuvaniIzvjestaji(p.getIdPOSLOVNICA());
        for (Izvjestaj iz : trenutniSacuvani) {
            modelSacuvaniIzvjestaji.addRow(new Object[]{
                    iz.getId(), iz.getTip(), iz.getDatum(), iz.getPeriodOd(), iz.getPeriodDo()
            });
        }
    }
    /**
     * Prikazuje sadržaj izabranog izvještaja iz tabele
     * u posebnom prozoru.
     */
    private void prikaziSadrzajIzvjestaja() {
        int red = tabelaSacuvaniIzvjestaji.getSelectedRow();
        if (red == -1) {
            JOptionPane.showMessageDialog(this, "Odaberite izvještaj iz tabele.");
            return;
        }
        Izvjestaj iz = trenutniSacuvani.get(red);

        JTextArea area = new JTextArea(iz.getSadrzaj());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scroll, "Izvještaj #" + iz.getId(), JOptionPane.PLAIN_MESSAGE);
    }
    /**
     * Kreira panel za upravljanje katalogom lijekova.
     * Panel sadrži tabelu svih lijekova, formu za unos podataka
     * i dugmad za dodavanje, izmjenu, brisanje i čišćenje forme.
     */
    private JPanel createLijekoviPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        modelLijekovi = new DefaultTableModel(
                new String[]{"ID", "Naziv", "Opis", "Na recept", "Kategorija", "Mjera", "Cijena (KM)"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaLijekovi = new JTable(modelLijekovi);
        tabelaLijekovi.getSelectionModel().addListSelectionListener(e -> popuniFormuLijek());
        panel.add(new JScrollPane(tabelaLijekovi), BorderLayout.CENTER);

        JPanel forma = new JPanel(new GridLayout(0, 4, 5, 5));
        txtNazivL = new JTextField();
        txtOpisL = new JTextField();
        txtMjeraL = new JTextField();
        txtCijenaL = new JTextField();
        chkNaRecept = new JCheckBox("Na recept");

        comboKategorijaL = new JComboBox<>();
        for (Kategorija k : kategorijaDAO.getSveKategorije()) comboKategorijaL.addItem(k);

        forma.add(new JLabel("Naziv:"));      forma.add(txtNazivL);
        forma.add(new JLabel("Opis:"));       forma.add(txtOpisL);
        forma.add(new JLabel("Kategorija:")); forma.add(comboKategorijaL);
        forma.add(new JLabel("Mjera:"));      forma.add(txtMjeraL);
        forma.add(new JLabel("Cijena (KM):")); forma.add(txtCijenaL);
        forma.add(new JLabel(""));            forma.add(chkNaRecept);

        JPanel dugmad = new JPanel();
        JButton btnDodaj = new JButton("Dodaj");
        btnDodaj.addActionListener(e -> dodajLijekUI());
        JButton btnIzmijeni = new JButton("Izmijeni odabrano");
        btnIzmijeni.addActionListener(e -> izmijeniLijekUI());
        JButton btnObrisi = new JButton("Obriši odabrano");
        btnObrisi.addActionListener(e -> obrisiLijekUI());
        JButton btnOcisti = new JButton("Očisti formu");
        btnOcisti.addActionListener(e -> ocistiFormuLijek());
        dugmad.add(btnDodaj); dugmad.add(btnIzmijeni); dugmad.add(btnObrisi); dugmad.add(btnOcisti);

        JPanel donji = new JPanel(new BorderLayout());
        donji.add(forma, BorderLayout.CENTER);
        donji.add(dugmad, BorderLayout.SOUTH);
        panel.add(donji, BorderLayout.SOUTH);

        ucitajLijekoveTabela();
        return panel;
    }
    /**
     * Učitava sve lijekove iz baze podataka i prikazuje ih u tabeli.
     * Takođe osvježava listu lijekova koja se koristi prilikom kreiranja
     * stavki nabavne narudžbe.
     */
    private void ucitajLijekoveTabela() {
        modelLijekovi.setRowCount(0);
        trenutniLijekoviSvi = lijekDAO.getSviLijekovi();
        for (Lijek l : trenutniLijekoviSvi) {
            String kategorijaNaziv = "";
            for (int i = 0; i < comboKategorijaL.getItemCount(); i++) {
                Kategorija k = comboKategorijaL.getItemAt(i);
                if (k.getIdKategorija() == l.getKategorijaId()) {
                    kategorijaNaziv = k.getNaziv();
                    break;
                }
            }
            modelLijekovi.addRow(new Object[]{
                    l.getIdLIJEK(), l.getNaziv(), l.getOpis(),
                    l.isNaRecept() ? "Da" : "Ne",
                    kategorijaNaziv, l.getMjera(),
                    String.format("%.2f", l.getCijena())
            });
        }

        // Osvjezi combo u tabu "Nabavne narudzbe" ako vec postoji
        if (comboLijekStavka != null) {
            comboLijekStavka.removeAllItems();
            for (Lijek l : trenutniLijekoviSvi) comboLijekStavka.addItem(l);
        }
    }
    /**
     * Popunjava formu podacima o trenutno odabranom lijeku iz tabele.
     * Omogućava izmjenu postojećeg lijeka.
     */
    private void popuniFormuLijek() {
        int red = tabelaLijekovi.getSelectedRow();
        if (red == -1) return;
        Lijek l = trenutniLijekoviSvi.get(red);
        txtNazivL.setText(l.getNaziv());
        txtOpisL.setText(l.getOpis());
        chkNaRecept.setSelected(l.isNaRecept());
        txtMjeraL.setText(l.getMjera());
        txtCijenaL.setText(String.valueOf(l.getCijena()));
        for (int i = 0; i < comboKategorijaL.getItemCount(); i++) {
            if (comboKategorijaL.getItemAt(i).getIdKategorija() == l.getKategorijaId()) {
                comboKategorijaL.setSelectedIndex(i);
                break;
            }
        }
    }
    /**
     * Briše sadržaj svih polja forme i uklanja selekciju iz tabele lijekova.
     */
    private void ocistiFormuLijek() {
        txtNazivL.setText(""); txtOpisL.setText(""); txtMjeraL.setText(""); txtCijenaL.setText("");
        chkNaRecept.setSelected(false);
        tabelaLijekovi.clearSelection();
    }
    /**
     * Čita podatke unesene u formu i kreira objekat Lijek
     */
    private Lijek procitajLijekIzForme(Integer postojeciId) {
        String naziv = txtNazivL.getText().trim();
        String opis = txtOpisL.getText().trim();
        String mjera = txtMjeraL.getText().trim();
        String cijenaStr = txtCijenaL.getText().trim();
        Kategorija kat = (Kategorija) comboKategorijaL.getSelectedItem();

        if (naziv.isEmpty() || cijenaStr.isEmpty() || kat == null) {
            JOptionPane.showMessageDialog(this, "Popunite naziv, cijenu i kategoriju.");
            return null;
        }

        double cijena;
        try {
            cijena = Double.parseDouble(cijenaStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Cijena mora biti broj (npr. 4.50).");
            return null;
        }

        Lijek l = new Lijek();
        if (postojeciId != null) l.setIdLIJEK(postojeciId);
        l.setNaziv(naziv);
        l.setOpis(opis);
        l.setNaRecept(chkNaRecept.isSelected());
        l.setKategorijaId(kat.getIdKategorija());
        l.setMjera(mjera);
        l.setCijena(cijena);
        return l;
    }
    /**
     * Dodaje novi lijek u bazu podataka na osnovu podataka unesenih u formu.
     * Nakon uspješnog dodavanja osvježava tabelu i čisti formu.
     */
    private void dodajLijekUI() {
        Lijek l = procitajLijekIzForme(null);
        if (l == null) return;
        if (lijekDAO.dodajLijek(l)) {
            JOptionPane.showMessageDialog(this, "Lijek dodat.");
            ucitajLijekoveTabela();
            ocistiFormuLijek();
        } else {
            JOptionPane.showMessageDialog(this, "Greška prilikom dodavanja.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Mijenja podatke o odabranom lijeku.
     * Nakon uspješne izmjene osvježava prikaz i čisti formu.
     */
    private void izmijeniLijekUI() {
        int red = tabelaLijekovi.getSelectedRow();
        if (red == -1) {
            JOptionPane.showMessageDialog(this, "Odaberite lijek iz tabele.");
            return;
        }
        int id = trenutniLijekoviSvi.get(red).getIdLIJEK();
        Lijek l = procitajLijekIzForme(id);
        if (l == null) return;
        if (lijekDAO.izmijeniLijek(l)) {
            JOptionPane.showMessageDialog(this, "Lijek ažuriran.");
            ucitajLijekoveTabela();
            ocistiFormuLijek();
        } else {
            JOptionPane.showMessageDialog(this, "Greška prilikom izmjene.", "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Briše trenutno odabrani lijek iz baze podataka.
     * Prije brisanja od korisnika traži potvrdu akcije.
     * Brisanje neće biti moguće ukoliko lijek ima povezane zalihe,
     * narudžbe ili nabavne narudžbe.
     */
    private void obrisiLijekUI() {
        int red = tabelaLijekovi.getSelectedRow();
        if (red == -1) {
            JOptionPane.showMessageDialog(this, "Odaberite lijek iz tabele.");
            return;
        }
        int id = trenutniLijekoviSvi.get(red).getIdLIJEK();
        int potvrda = JOptionPane.showConfirmDialog(this,
                "Obrisati lijek?", "Potvrda", JOptionPane.YES_NO_OPTION);
        if (potvrda != JOptionPane.YES_OPTION) return;

        String greska = lijekDAO.obrisiLijek(id);
        if (greska == null) {
            JOptionPane.showMessageDialog(this, "Lijek obrisan.");
            ucitajLijekoveTabela();
            ocistiFormuLijek();
        } else {
            String poruka = greska.contains(":") ? greska.substring(greska.lastIndexOf(':') + 1).trim() : greska;
            JOptionPane.showMessageDialog(this, poruka, "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void osvjeziSvePoslovniceCombo() {
        List<Poslovnica> svePoslovnice = poslovnicaDAO.getSvePoslovnice();

        // Čistimo svih 5 menija
        if (comboPoslovnicaNabavka != null) comboPoslovnicaNabavka.removeAllItems();
        if (comboPoslovnicaPregled != null) comboPoslovnicaPregled.removeAllItems();
        if (comboPoslovnicaPromet != null) comboPoslovnicaPromet.removeAllItems();
        if (comboPoslovnicaSacuvani != null) comboPoslovnicaSacuvani.removeAllItems();
        if (comboPoslovnicaRaspored != null) comboPoslovnicaRaspored.removeAllItems(); // NOVO

        // Punimo svih 5 menija
        for (Poslovnica p : svePoslovnice) {
            if (comboPoslovnicaNabavka != null) comboPoslovnicaNabavka.addItem(p);
            if (comboPoslovnicaPregled != null) comboPoslovnicaPregled.addItem(p);
            if (comboPoslovnicaPromet != null) comboPoslovnicaPromet.addItem(p);
            if (comboPoslovnicaSacuvani != null) comboPoslovnicaSacuvani.addItem(p);
            if (comboPoslovnicaRaspored != null) comboPoslovnicaRaspored.addItem(p); // NOVO
        }
    }
    private void ocistiFormuNalozi() {
        txtKorisnickoIme.setText("");
        txtLozinka.setText("");
    }
    private void obrisiNabavnu() {
        int red = tabelaNabavneNarudzbe.getSelectedRow();
        if (red == -1) {
            JOptionPane.showMessageDialog(this, "Odaberite zaposlenog iz tabele.");
            return;
        }


        int id = trenutneNabavne.get(red).getIdNabavnaNarudzba();
        boolean ok = nabavnaDAO.obrisiNabavnuNarudzbu(id);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Narudzba obrisan.");
            ucitajNabavneNarudzbe();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Greška — narudzba možda ima vezane podatke.",
                    "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }

}