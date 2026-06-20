package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.Narudzba;
import org.example.model.NarudzbaPregled;
import org.example.model.StavkaNarudzbe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NarudzbaDAO {

    // Kreiraj narudzbu kroz stored proceduru, vraca id nove narudzbe
    public int kreirajNarudzbu(String ime, String prezime, String email,
                               String telefon, String adresa,
                               String kartica, int poslovnicaId) {
        String sql = "CALL sp_kreiraj_narudzbu(?,?,?,?,?,?,NULL,?,?)";
        try (CallableStatement cs = DatabaseConnection.getConnection().prepareCall(sql)) {
            cs.setString(1, ime);
            cs.setString(2, prezime);
            cs.setString(3, email);
            cs.setString(4, telefon);
            cs.setString(5, adresa);
            cs.setString(6, kartica);
            cs.setInt(7, poslovnicaId);
            cs.registerOutParameter(8, Types.INTEGER);
            cs.execute();
            return cs.getInt(8);
        } catch (SQLException e) {
            System.err.println("Greska kreirajNarudzbu: " + e.getMessage());
            return -1;
        }
    }

    // Dodaj stavku kroz stored proceduru sp_dodaj_stavku_narudzbe
    public boolean dodajStavku(int narudzbaId, int lijekId, int kolicina) {
        String sql = "CALL sp_dodaj_stavku_narudzbe(?,?,?)";
        try (CallableStatement cs = DatabaseConnection.getConnection().prepareCall(sql)) {
            cs.setInt(1, narudzbaId);
            cs.setInt(2, lijekId);
            cs.setInt(3, kolicina);
            cs.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Greska dodajStavku: " + e.getMessage());
            return false;
        }
    }

    // Sve narudzbe za poslovnicu
    public List<Narudzba> getNarudzbePoPoslovnici(int poslovnicaId) {
        List<Narudzba> lista = new ArrayList<>();
        String sql = "SELECT * FROM NARUDZBA WHERE POSLOVNICA_idPOSLOVNICA=? ORDER BY datum DESC";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, poslovnicaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Narudzba(
                        rs.getInt("idNARUDZBA"),
                        rs.getDate("datum").toLocalDate(),
                        rs.getString("status"),
                        rs.getDouble("UkupnaVrijednost"),
                        rs.getInt("ZAPOSLENI_idZAPOSLENI"),
                        rs.getInt("POSLOVNICA_idPOSLOVNICA")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Greska getNarudzbe: " + e.getMessage());
        }
        return lista;
    }

    // Promijeni status narudzbe
    public boolean promijeniStatus(int narudzbaId, String status) {
        String sql = "UPDATE NARUDZBA SET status=? WHERE idNARUDZBA=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, narudzbaId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Greska promijeniStatus: " + e.getMessage());
            return false;
        }
    }
    // Zaposleni preuzima narudzbu na obradu
    public boolean preuzimiNarudzbu(int narudzbaId, int zaposleniId) {
        String sql = "UPDATE NARUDZBA SET ZAPOSLENI_idZAPOSLENI=?, status='U obradi' " +
                "WHERE idNARUDZBA=? AND ZAPOSLENI_idZAPOSLENI IS NULL";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, zaposleniId);
            ps.setInt(2, narudzbaId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Greska preuzimiNarudzbu: " + e.getMessage());
            return false;
        }
    }
    // Evidencija placanja (trigger automatski mijenja status narudzbe na 'Placena')
    public boolean evidentirajPlacanje(int narudzbaId, String nacinPlacanja, String brojTransakcije, String status) {
        String sql = "INSERT INTO PLACANJE (NARUDZBA_idNARUDZBA, DatumPlacanja, NacinaPlacanja, BrojTransakcije, status) " +
                "VALUES (?, NOW(), ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, narudzbaId);
            ps.setString(2, nacinPlacanja);
            ps.setString(3, brojTransakcije);
            ps.setString(4, status);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Greska evidentirajPlacanje: " + e.getMessage());
            return false;
        }
    }
    // Aktivna narudzbe za poslovnicu (koristi pogled v_aktivne_narudzbe)
    public List<NarudzbaPregled> getNarudzbeZaPoslovnicu(int poslovnicaId) {
        List<NarudzbaPregled> lista = new ArrayList<>();
        String sql = """
            SELECT idNARUDZBA, datum, status, UkupnaVrijednost, zaposleni_id,
                   kupac_ime, kupac_prezime, kupac_telefon, kupac_adresa
            FROM v_aktivne_narudzbe
            WHERE poslovnica_id = ?
            ORDER BY datum DESC, idNARUDZBA DESC
            """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, poslovnicaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer zapId = rs.getObject("zaposleni_id") != null
                        ? rs.getInt("zaposleni_id") : null;
                lista.add(new NarudzbaPregled(
                        rs.getInt("idNARUDZBA"),
                        rs.getDate("datum").toLocalDate(),
                        rs.getString("status"),
                        rs.getDouble("UkupnaVrijednost"),
                        zapId,
                        rs.getString("kupac_ime"),
                        rs.getString("kupac_prezime"),
                        rs.getString("kupac_telefon"),
                        rs.getString("kupac_adresa")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Greska getNarudzbeZaPoslovnicu: " + e.getMessage());
        }
        return lista;
    }

    // Stavke odredjene narudzbe
    public List<StavkaNarudzbe> getStavkeNarudzbe(int narudzbaId) {
        List<StavkaNarudzbe> lista = new ArrayList<>();
        String sql = """
                SELECT l.Naziv, ns.Kolicina, ns.cijena, (ns.Kolicina * ns.cijena) AS ukupno
                FROM NARUDZBA_STAVKA ns
                JOIN LIJEK l ON l.idLIJEK = ns.LIJEK_idLIJEK
                WHERE ns.NARUDZBA_idNARUDZBA = ?
                """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, narudzbaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new StavkaNarudzbe(
                        rs.getString("Naziv"),
                        rs.getInt("Kolicina"),
                        rs.getDouble("cijena"),
                        rs.getDouble("ukupno")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Greska getStavkeNarudzbe: " + e.getMessage());
        }
        return lista;
    }
    public boolean obrisiNabavnuNarudzbu(int id) {
        String sql = "DELETE FROM NABA WHERE idPOSLOVNICA=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Greska obrisiPoslovnicu: " + e.getMessage());
            return false;
        }
    }

}