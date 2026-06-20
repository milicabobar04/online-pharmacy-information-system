package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.Poslovnica;
import org.example.model.RasporedStavka;
import org.example.model.Zaposleni;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ZaposleniDAO {

    public List<Zaposleni> getSviZaposleni() {
        List<Zaposleni> lista = new ArrayList<>();
        String sql = "SELECT * FROM ZAPOSLENI";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Greska getSviZaposleni: " + e.getMessage());
        }
        return lista;
    }

    public int dodajZaposlenog(Zaposleni z) {
        String sql = "INSERT INTO ZAPOSLENI (Ime, Prezime, Pozicija, Plata, DatumZaposlenja, JMBG) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, z.getIme());
            ps.setString(2, z.getPrezime());
            ps.setString(3, z.getPozicija());
            ps.setDouble(4, z.getPlata());
            ps.setDate(5, Date.valueOf(z.getDatumZaposlenja()));
            ps.setString(6, z.getJmbg());
            ps.executeUpdate();
            return 1;
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            return 0; // JMBG već postoji
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }


    public int izmijeniZaposlenog(Zaposleni z) {
        String sql = "UPDATE ZAPOSLENI SET Ime=?, Prezime=?, Pozicija=?, Plata=?, DatumZaposlenja=?, JMBG=? " +
                "WHERE idZAPOSLENI=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, z.getIme());
            ps.setString(2, z.getPrezime());
            ps.setString(3, z.getPozicija());
            ps.setDouble(4, z.getPlata());
            ps.setDate(5, Date.valueOf(z.getDatumZaposlenja()));
            ps.setString(6, z.getJmbg());
            ps.setInt(7, z.getIdZAPOSLENI());
            ps.executeUpdate();
            return 1;
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            return 0; // JMBG već postoji kod drugog zaposlenog
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }


    public boolean obrisiZaposlenog(int id) {
        String sql = "DELETE FROM ZAPOSLENI WHERE idZAPOSLENI=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Greska obrisiZaposlenog: " + e.getMessage());
            return false;
        }
    }

    private Zaposleni mapRow(ResultSet rs) throws SQLException {
        return new Zaposleni(
                rs.getInt("idZAPOSLENI"),
                rs.getString("Ime"),
                rs.getString("Prezime"),
                rs.getString("Pozicija"),
                rs.getDouble("Plata"),
                rs.getDate("DatumZaposlenja").toLocalDate(),
                rs.getString("JMBG")
        );
    }
    // Poslovnica gdje zaposleni radi danas (vraca null ako ne radi danas) koristi v_zaposleni_poslovnica
    public Poslovnica getPoslovnicaDanas(int zaposleniId) {
        String sql = """
                SELECT idPOSLOVNICA, poslovnica
                FROM v_zaposleni_poslovnica
                WHERE idZAPOSLENI = ? AND DatumRada = CURDATE()
                """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, zaposleniId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Iz view-a nemamo sve kolone POSLOVNICA, pa dohvatimo cijeli red preko PoslovnicaDAO
                return new PoslovnicaDAO().getPoslovnicaById(rs.getInt("idPOSLOVNICA"));
            }
        } catch (SQLException e) {
            System.err.println("Greska getPoslovnicaDanas: " + e.getMessage());
        }
        return null;
    }

    // Raspored zaposlenog za narednih 7 dana koristi v_zaposleni_poslovnica
    public List<RasporedStavka> getRasporedZaposlenog(int zaposleniId) {
        List<RasporedStavka> lista = new ArrayList<>();
        String sql = """
                SELECT DatumRada, poslovnica, Smjena
                FROM v_zaposleni_poslovnica
                WHERE idZAPOSLENI = ?
                  AND DatumRada BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 6 DAY)
                ORDER BY DatumRada
                """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, zaposleniId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new RasporedStavka(
                        rs.getDate("DatumRada").toLocalDate(),
                        rs.getString("poslovnica"),
                        rs.getInt("Smjena")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Greska getRasporedZaposlenog: " + e.getMessage());
        }
        return lista;
    }
}