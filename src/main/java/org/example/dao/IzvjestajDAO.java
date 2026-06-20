package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.Izvjestaj;
import org.example.model.StavkaIzvjestaja;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class IzvjestajDAO {

    public List<StavkaIzvjestaja> getPrometIzvjestaj(int poslovnicaId, LocalDate datumOd, LocalDate datumDo) {
        List<StavkaIzvjestaja> lista = new ArrayList<>();
        String sql = "CALL sp_izvjestaj_promet(?,?,?)";
        try (CallableStatement cs = DatabaseConnection.getConnection().prepareCall(sql)) {
            cs.setInt(1, poslovnicaId);
            cs.setDate(2, Date.valueOf(datumOd));
            cs.setDate(3, Date.valueOf(datumDo));
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                lista.add(new StavkaIzvjestaja(
                        rs.getString("lijek"),
                        rs.getInt("ukupno_prodano"),
                        rs.getDouble("ukupan_prihod")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Greska getPrometIzvjestaj: " + e.getMessage());
        }
        return lista;
    }

    public boolean sacuvajIzvjestaj(String tip, LocalDate periodOd, LocalDate periodDo,
                                    int zaposleniId, int poslovnicaId, String sadrzaj) {
        String sql = "INSERT INTO IZVJESTAJ (tipIzvjestaja, datum, periodOd, periodDo, ZAPOSLENI_idZAPOSLENI, sadrzaj, POSLOVNICA_idPOSLOVNICA) " +
                "VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, tip);
            ps.setDate(2, Date.valueOf(LocalDate.now()));
            ps.setDate(3, Date.valueOf(periodOd));
            ps.setDate(4, Date.valueOf(periodDo));
            ps.setInt(5, zaposleniId);
            ps.setString(6, sadrzaj);
            ps.setInt(7, poslovnicaId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Greska sacuvajIzvjestaj: " + e.getMessage());
            return false;
        }
    }

    public List<Izvjestaj> getSacuvaniIzvjestaji(int poslovnicaId) {
        List<Izvjestaj> lista = new ArrayList<>();
        String sql = "SELECT * FROM IZVJESTAJ WHERE POSLOVNICA_idPOSLOVNICA=? ORDER BY datum DESC";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, poslovnicaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Izvjestaj(
                        rs.getInt("idIZVJESTAJ"),
                        rs.getString("tipIzvjestaja"),
                        rs.getDate("datum").toLocalDate(),
                        rs.getDate("periodOd").toLocalDate(),
                        rs.getDate("periodDo").toLocalDate(),
                        rs.getString("sadrzaj")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Greska getSacuvaniIzvjestaji: " + e.getMessage());
        }
        return lista;
    }
}