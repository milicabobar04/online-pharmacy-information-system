package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.NiskaZaliha;
import org.example.model.StanjeZalihe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ZalihaDAO {

    // Stanje zaliha za poslovnicu (koristi stored proceduru sp_stanje_zaliha)
    public List<StanjeZalihe> getStanjeZaliha(int poslovnicaId) {
        List<StanjeZalihe> lista = new ArrayList<>();
        String sql = "CALL sp_stanje_zaliha(?)";
        try (CallableStatement cs = DatabaseConnection.getConnection().prepareCall(sql)) {
            cs.setInt(1, poslovnicaId);
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                lista.add(new StanjeZalihe(
                        rs.getInt("idLIJEK"),
                        rs.getString("Naziv"),
                        rs.getInt("zaliha"),
                        rs.getInt("MinimalnaKolicina"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Greska getStanjeZaliha: " + e.getMessage());
        }
        return lista;
    }
    // Lijekovi ispod minimalne zalihe za poslovnicu (koristi view v_niska_zaliha)
    public List<NiskaZaliha> getNiskaZaliha(int poslovnicaId) {
        List<NiskaZaliha> lista = new ArrayList<>();
        String sql = "SELECT idLIJEK, lijek, trenutna_zaliha, MinimalnaKolicina, nedostaje " +
                "FROM v_niska_zaliha WHERE idPOSLOVNICA = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, poslovnicaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new NiskaZaliha(
                        rs.getInt("idLIJEK"),
                        rs.getString("lijek"),
                        rs.getInt("trenutna_zaliha"),
                        rs.getInt("MinimalnaKolicina"),
                        rs.getInt("nedostaje")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Greska getNiskaZaliha: " + e.getMessage());
        }
        return lista;
    }
}