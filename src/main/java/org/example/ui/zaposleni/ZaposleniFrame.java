package org.example.ui.zaposleni;

import org.example.dao.*;
import org.example.model.*;
import org.example.ui.LoginForm;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ZaposleniFrame extends JFrame {

    private final int zaposleniId; // trenutno prijavljeni zaposleni
    private final Poslovnica poslovnicaDanas; // poslovnica u kojoj danas radi

    // komunikacija sa bazom
    private final ZaposleniDAO zaposleniDAO = new ZaposleniDAO();
    private final NarudzbaDAO narudzbaDAO = new NarudzbaDAO();
    private final ZalihaDAO zalihaDAO = new ZalihaDAO();

    // modeli tabela za prikaz
    private DefaultTableModel modelRaspored;
    private DefaultTableModel modelNarudzbe;
    private DefaultTableModel modelZalihe;
    private JTable tabelaNarudzbe;

    // lista trenutno ucitanih narudzbi
    private List<NarudzbaPregled> trenutneNarudzbe;
    /**
     * Kreira glavni prozor zaposlenog i inicijalizuje korisnički interfejs.
     *
     * @param korisnik trenutno prijavljeni korisnik sistema
     */
    public ZaposleniFrame(Korisnik korisnik) {
        this.zaposleniId = korisnik.getZaposleniId();
        this.poslovnicaDanas = zaposleniDAO.getPoslovnicaDanas(zaposleniId);

        setTitle("Online Apoteka — Zaposleni: " +
                korisnik.getZaposleni().getIme() + " " + korisnik.getZaposleni().getPrezime());
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
    }
    /**
     * Inicijalizuje korisnički interfejs zaposlenog.
     * Kreira kartice za raspored, narudžbe i zalihe,
     * kao i donji panel sa informacijom o poslovnici i dugmetom za odjavu.
     */
    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Moj raspored", createRasporedPanel());
        tabs.addTab("Narudžbe", createNarudzbePanel());
        tabs.addTab("Zalihe", createZalihePanel());

        add(tabs, BorderLayout.CENTER);

        JPanel donji = new JPanel(new BorderLayout());

        JLabel lblInfo = new JLabel(poslovnicaDanas != null
                ? "  Danas radite u: " + poslovnicaDanas.getNaziv()
                : "  Danas nemate raspoređenu smjenu.");
        donji.add(lblInfo, BorderLayout.WEST);

        JButton btnOdjava = new JButton("Odjava");
        btnOdjava.addActionListener(e -> {
            dispose();
            new LoginForm().setVisible(true);
        });
        donji.add(btnOdjava, BorderLayout.EAST);

        add(donji, BorderLayout.SOUTH);
    }

    /**
     * Kreira panel za prikaz rasporeda rada zaposlenog.
     *
     * @return panel sa tabelom rasporeda za narednih sedam dana
     */
    private JPanel createRasporedPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lbl = new JLabel("Raspored za narednih 7 dana", SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lbl, BorderLayout.NORTH);

        modelRaspored = new DefaultTableModel(new String[]{"Datum", "Poslovnica", "Smjena"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        panel.add(new JScrollPane(new JTable(modelRaspored)), BorderLayout.CENTER);

        ucitajRaspored();
        return panel;
    }
    /**
     * Učitava raspored rada zaposlenog iz baze podataka
     * i popunjava tabelu rasporeda.
     */
    private void ucitajRaspored() {
        modelRaspored.setRowCount(0);
        for (RasporedStavka r : zaposleniDAO.getRasporedZaposlenog(zaposleniId)) {
            modelRaspored.addRow(new Object[]{r.getDatum(), r.getPoslovnica(), r.getSmjena()});
        }
    }

    /**
     * Kreira panel za pregled i obradu narudžbi.
     * Ako zaposleni nema raspoređenu smjenu za tekući dan,
     * prikazuje odgovarajuću poruku.
     *
     * @return panel sa tabelom narudžbi i komandnim dugmadima
     */
    private JPanel createNarudzbePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (poslovnicaDanas == null) {
            panel.add(new JLabel("Nemate raspoređenu smjenu za danas. Narudžbe nisu dostupne.",
                    SwingConstants.CENTER), BorderLayout.CENTER);
            return panel;
        }

        JLabel lblNaslov = new JLabel("Aktivne narudžbe — " + poslovnicaDanas.getNaziv(), SwingConstants.CENTER);
        lblNaslov.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lblNaslov, BorderLayout.NORTH);

        modelNarudzbe = new DefaultTableModel(
                new String[]{"ID", "Datum", "Kupac", "Telefon", "Adresa", "Ukupno", "Status", "Dodijeljeno"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaNarudzbe = new JTable(modelNarudzbe);
        panel.add(new JScrollPane(tabelaNarudzbe), BorderLayout.CENTER);

        JPanel dugmad = new JPanel();

        JButton btnDetalji = new JButton("Detalji narudžbe");
        btnDetalji.addActionListener(e -> prikaziDetalje());

        JButton btnPreuzmi = new JButton("Preuzmi narudžbu");
        btnPreuzmi.addActionListener(e -> preuzmiNarudzbu());

        JButton btnStatus = new JButton("Promijeni status");
        btnStatus.addActionListener(e -> promijeniStatus());

        JButton btnPlacanje = new JButton("Evidentiraj plaćanje");
        btnPlacanje.addActionListener(e -> evidentirajPlacanje());

        JButton btnOsvjezi = new JButton("Osvježi");
        btnOsvjezi.addActionListener(e -> ucitajNarudzbe());

        dugmad.add(btnDetalji);
        dugmad.add(btnPreuzmi);
        dugmad.add(btnStatus);
        dugmad.add(btnPlacanje);
        dugmad.add(btnOsvjezi);

        panel.add(dugmad, BorderLayout.SOUTH);

        ucitajNarudzbe();
        return panel;
    }
    /**
     * Učitava aktivne narudžbe za poslovnicu u kojoj zaposleni radi
     * i prikazuje ih u tabeli.
     */
    private void ucitajNarudzbe() {
        modelNarudzbe.setRowCount(0);
        trenutneNarudzbe = narudzbaDAO.getNarudzbeZaPoslovnicu(poslovnicaDanas.getIdPOSLOVNICA());
        for (NarudzbaPregled n : trenutneNarudzbe) {
            String dodijeljeno;
            if (n.getZaposleniId() == null) {
                dodijeljeno = "Ne";
            } else if (n.getZaposleniId() == zaposleniId) {
                dodijeljeno = "Za mene";
            } else {
                dodijeljeno = "Drugog zaposlenog";
            }

            modelNarudzbe.addRow(new Object[]{
                    n.getIdNarudzba(),
                    n.getDatum(),
                    n.getKupacIme() + " " + n.getKupacPrezime(),
                    n.getKupacTelefon(),
                    n.getKupacAdresa(),
                    String.format("%.2f KM", n.getUkupno()),
                    n.getStatus(),
                    dodijeljeno
            });
        }
    }
    /**
     * Vraća trenutno odabranu narudžbu iz tabele.
     *
     * @return odabrana narudžba ili null ako nijedna nije označena
     */
    private NarudzbaPregled getOdabranaNarudzba() {
        int red = tabelaNarudzbe.getSelectedRow();
        if (red == -1) {
            JOptionPane.showMessageDialog(this, "Odaberite narudžbu iz tabele.");
            return null;
        }
        return trenutneNarudzbe.get(red);
    }
    /**
     * Prikazuje detaljne informacije o odabranoj narudžbi,
     * uključujući podatke o kupcu i stavke narudžbe.
     */
    private void prikaziDetalje() {
        NarudzbaPregled n = getOdabranaNarudzba();
        if (n == null) return;

        List<StavkaNarudzbe> stavke = narudzbaDAO.getStavkeNarudzbe(n.getIdNarudzba());

        StringBuilder sb = new StringBuilder();
        sb.append("Narudžba #").append(n.getIdNarudzba()).append("\n");
        sb.append("Kupac: ").append(n.getKupacIme()).append(" ").append(n.getKupacPrezime()).append("\n");
        sb.append("Adresa: ").append(n.getKupacAdresa()).append("\n");
        sb.append("Telefon: ").append(n.getKupacTelefon()).append("\n");
        sb.append("Status: ").append(n.getStatus()).append("\n\n");
        sb.append("Stavke:\n");
        for (StavkaNarudzbe s : stavke) {
            sb.append(String.format("  %-25s x%-3d %8.2f KM = %8.2f KM%n",
                    s.getNaziv(), s.getKolicina(), s.getCijena(), s.getUkupno()));
        }
        sb.append(String.format("%nUkupno: %.2f KM", n.getUkupno()));

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Detalji narudžbe", JOptionPane.PLAIN_MESSAGE);
    }
    /**
     * Dodjeljuje odabranu narudžbu trenutno prijavljenom zaposlenom.
     * Nakon uspješnog preuzimanja osvježava prikaz narudžbi.
     */
    private void preuzmiNarudzbu() {
        NarudzbaPregled n = getOdabranaNarudzba();
        if (n == null) return;

        if (n.getZaposleniId() != null) {
            JOptionPane.showMessageDialog(this, "Narudžba je već dodijeljena.");
            return;
        }

        boolean ok = narudzbaDAO.preuzimiNarudzbu(n.getIdNarudzba(), zaposleniId);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Narudžba #" + n.getIdNarudzba() + " preuzeta.");
            ucitajNarudzbe();
        } else {
            JOptionPane.showMessageDialog(this, "Greška — narudžba je možda već preuzeta.",
                    "Greška", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Omogućava promjenu statusa narudžbe koju je preuzeo
     * trenutno prijavljeni zaposleni.
     */
    private void promijeniStatus() {
        NarudzbaPregled n = getOdabranaNarudzba();
        if (n == null) return;

        if (n.getZaposleniId() == null || !n.getZaposleniId().equals(zaposleniId)) {
            JOptionPane.showMessageDialog(this, "Možete mijenjati status samo narudžbi koje ste preuzeli.");
            return;
        }

        String[] statusi = {"U obradi", "Spremna", "Isporucena", "Otkazana"};
        JList<String> lista = new JList<>(statusi);
        lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lista.setSelectedValue(n.getStatus(), true);
        lista.setVisibleRowCount(4);

        int rezultat = JOptionPane.showConfirmDialog(this,
                new JScrollPane(lista),
                "Promjena statusa — narudžba #" + n.getIdNarudzba(),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (rezultat == JOptionPane.OK_OPTION) {
            String novi = lista.getSelectedValue();
            if (novi == null) {
                JOptionPane.showMessageDialog(this, "Odaberite status.");
                return;
            }
            boolean ok = narudzbaDAO.promijeniStatus(n.getIdNarudzba(), novi);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Status ažuriran na: " + novi);
                ucitajNarudzbe();
            } else {
                JOptionPane.showMessageDialog(this, "Greška prilikom ažuriranja statusa.",
                        "Greška", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    /**
     * Evidentira podatke o plaćanju za odabranu narudžbu.
     * Korisnik unosi način plaćanja, broj transakcije i status plaćanja.
     */
    private void evidentirajPlacanje() {
        NarudzbaPregled n = getOdabranaNarudzba();
        if (n == null) return;

        JTextField txtBroj = new JTextField();
        JComboBox<String> comboNacin = new JComboBox<>(new String[]{"Kartica",  "Gotovina"});
        JComboBox<String> comboStatus = new JComboBox<>(new String[]{"Uspjesno", "Neuspjesno"});

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Način plaćanja:"));
        panel.add(comboNacin);
        panel.add(new JLabel("Broj transakcije:"));
        panel.add(txtBroj);
        panel.add(new JLabel("Status:"));
        panel.add(comboStatus);

        int rezultat = JOptionPane.showConfirmDialog(this, panel,
                "Evidencija plaćanja — narudžba #" + n.getIdNarudzba(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (rezultat == JOptionPane.OK_OPTION) {
            boolean ok = narudzbaDAO.evidentirajPlacanje(
                    n.getIdNarudzba(),
                    (String) comboNacin.getSelectedItem(),
                    txtBroj.getText().trim(),
                    (String) comboStatus.getSelectedItem()
            );
            if (ok) {
                JOptionPane.showMessageDialog(this, "Plaćanje evidentirano.");
                ucitajNarudzbe();
            } else {
                JOptionPane.showMessageDialog(this, "Greška prilikom evidentiranja plaćanja.",
                        "Greška", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Kreira panel za pregled stanja zaliha u poslovnici
     * u kojoj zaposleni trenutno radi.
     *
     * @return panel sa tabelom stanja zaliha
     */
    private JPanel createZalihePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (poslovnicaDanas == null) {
            panel.add(new JLabel("Nemate raspoređenu smjenu za danas. Zalihe nisu dostupne.",
                    SwingConstants.CENTER), BorderLayout.CENTER);
            return panel;
        }

        JLabel lblNaslov = new JLabel("Stanje zaliha — " + poslovnicaDanas.getNaziv(), SwingConstants.CENTER);
        lblNaslov.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(lblNaslov, BorderLayout.NORTH);

        modelZalihe = new DefaultTableModel(
                new String[]{"ID", "Naziv", "Zaliha", "Minimalna količina", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        panel.add(new JScrollPane(new JTable(modelZalihe)), BorderLayout.CENTER);

        JButton btnOsvjezi = new JButton("Osvježi");
        btnOsvjezi.addActionListener(e -> ucitajZalihe());
        JPanel dugmad = new JPanel();
        dugmad.add(btnOsvjezi);
        panel.add(dugmad, BorderLayout.SOUTH);

        ucitajZalihe();
        return panel;
    }
    /**
     * Učitava stanje zaliha iz baze podataka
     * i popunjava tabelu lijekova i njihovih količina.
     */
    private void ucitajZalihe() {
        modelZalihe.setRowCount(0);
        for (StanjeZalihe s : zalihaDAO.getStanjeZaliha(poslovnicaDanas.getIdPOSLOVNICA())) {
            modelZalihe.addRow(new Object[]{
                    s.getIdLijek(), s.getNaziv(), s.getZaliha(), s.getMinimalnaKolicina(), s.getStatus()
            });
        }
    }
}