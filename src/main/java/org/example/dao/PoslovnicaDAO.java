package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.Poslovnica;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PoslovnicaDAO {

    public List<Poslovnica> getSvePoslovnice() {
        List<Poslovnica> lista = new ArrayList<>();
        String sql = "SELECT * FROM POSLOVNICA";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Poslovnica p = new Poslovnica(
                        rs.getInt("idPOSLOVNICA"),
                        rs.getString("Naziv"),
                        rs.getString("Adresa"),
                        rs.getString("Telefon"),
                        rs.getString("Email"),
                        rs.getString("RadiOd"),
                        rs.getString("RadiDo"),
                        rs.getInt("MJESTO_Posta")
                );
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Greska getSvePoslovnice: " + e.getMessage());
        }
        return lista;
    }

    public boolean dodajPoslovnicu(Poslovnica p) {
        String sql = "INSERT INTO POSLOVNICA (Naziv, Adresa, Telefon, Email, RadiOd, RadiDo, MJESTO_Posta) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getNaziv());
            ps.setString(2, p.getAdresa());
            ps.setString(3, p.getTelefon());
            ps.setString(4, p.getEmail());
            ps.setString(5, p.getRadiOd());
            ps.setString(6, p.getRadiDo());
            ps.setInt(7, p.getMjestoPosta());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Greska dodajPoslovnicu: " + e.getMessage());
            return false;
        }
    }

    public boolean izmijeniPoslovnicu(Poslovnica p) {
        String sql = "UPDATE POSLOVNICA SET Naziv=?, Adresa=?, Telefon=?, Email=?, RadiOd=?, RadiDo=?, MJESTO_Posta=? WHERE idPOSLOVNICA=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, p.getNaziv());
            ps.setString(2, p.getAdresa());
            ps.setString(3, p.getTelefon());
            ps.setString(4, p.getEmail());
            ps.setString(5, p.getRadiOd());
            ps.setString(6, p.getRadiDo());
            ps.setInt(7, p.getMjestoPosta());
            ps.setInt(8, p.getIdPOSLOVNICA());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Greska izmijeniPoslovnicu: " + e.getMessage());
            return false;
        }
    }

    public boolean obrisiPoslovnicu(int id) {
        String sql = "DELETE FROM POSLOVNICA WHERE idPOSLOVNICA=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Greska obrisiPoslovnicu: " + e.getMessage());
            return false;
        }
    }
    public Poslovnica getPoslovnicaById(int id) {
        String sql = "SELECT * FROM POSLOVNICA WHERE idPOSLOVNICA = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Poslovnica(
                        rs.getInt("idPOSLOVNICA"),
                        rs.getString("Naziv"),
                        rs.getString("Adresa"),
                        rs.getString("Telefon"),
                        rs.getString("Email"),
                        rs.getString("RadiOd"),
                        rs.getString("RadiDo"),
                        rs.getInt("MJESTO_Posta")
                );
            }
        } catch (SQLException e) {
            System.err.println("Greska getPoslovnicaById: " + e.getMessage());
        }
        return null;
    }
}