package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.RasporedUnos;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RasporedDAO {
    // Dodaj smjenu zaposlenom
    public boolean dodajRaspored(int zaposleniId, int poslovnicaId, LocalDate datum, int smjena) {
        String sql = "INSERT INTO ZAPOSLENI_has_POSLOVNIC (ZAPOSLENI_idZAPOSLENI, POSLOVNICA_idPOSLOVNICA, DatumRada, Smjena) VALUES (?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, zaposleniId);
            ps.setInt(2, poslovnicaId);
            ps.setDate(3, Date.valueOf(datum));
            ps.setInt(4, smjena);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Greska dodajRaspored: " + e.getMessage());
            return false;
        }
    }
    public List<RasporedUnos> getRasporedZaZaposlenog(int zaposleniId, LocalDate datumOd, LocalDate datumDo) {
        List<RasporedUnos> lista = new ArrayList<>();
        String sql = """
                SELECT zp.POSLOVNICA_idPOSLOVNICA, p.Naziv, zp.DatumRada, zp.Smjena
                FROM ZAPOSLENI_has_POSLOVNIC zp
                JOIN POSLOVNICA p ON p.idPOSLOVNICA = zp.POSLOVNICA_idPOSLOVNICA
                WHERE zp.ZAPOSLENI_idZAPOSLENI = ?
                  AND zp.DatumRada BETWEEN ? AND ?
                ORDER BY zp.DatumRada
                """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, zaposleniId);
            ps.setDate(2, Date.valueOf(datumOd));
            ps.setDate(3, Date.valueOf(datumDo));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new RasporedUnos(
                        rs.getInt("POSLOVNICA_idPOSLOVNICA"),
                        rs.getString("Naziv"),
                        rs.getDate("DatumRada").toLocalDate(),
                        rs.getInt("Smjena")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Greska getRasporedZaZaposlenog: " + e.getMessage());
        }
        return lista;
    }
    public boolean obrisiRaspored(int zaposleniId, int poslovnicaId, LocalDate datum) {
        String sql = "DELETE FROM ZAPOSLENI_has_POSLOVNIC WHERE ZAPOSLENI_idZAPOSLENI=? AND POSLOVNICA_idPOSLOVNICA=? AND DatumRada=?";        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, zaposleniId);
            ps.setInt(2, poslovnicaId);
            ps.setDate(3, Date.valueOf(datum));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Greska obrisiRaspored: " + e.getMessage());
            return false;
        }
    }
}
