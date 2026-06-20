package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.Dobavljac;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DobavljacDAO {

    public List<Dobavljac> getSviDobavljaci() {
        List<Dobavljac> lista = new ArrayList<>();
        String sql = "SELECT * FROM DOBAVLJAC ORDER BY Naziv";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Greska getSviDobavljaci: " + e.getMessage());
        }
        return lista;
    }

    public boolean dodajDobavljaca(Dobavljac d) {
        String sql = "INSERT INTO DOBAVLJAC (Naziv, Adresa, Telefon, Email, MJESTO_Posta) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, d.getNaziv());
            ps.setString(2, d.getAdresa());
            ps.setString(3, d.getTelefon());
            ps.setString(4, d.getEmail());
            ps.setInt(5, d.getMjestoPosta());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Greska dodajDobavljaca: " + e.getMessage());
            return false;
        }
    }

    public boolean izmijeniDobavljaca(Dobavljac d) {
        String sql = "UPDATE DOBAVLJAC SET Naziv=?, Adresa=?, Telefon=?, Email=?, MJESTO_Posta=? WHERE idDOBAVLJAC=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, d.getNaziv());
            ps.setString(2, d.getAdresa());
            ps.setString(3, d.getTelefon());
            ps.setString(4, d.getEmail());
            ps.setInt(5, d.getMjestoPosta());
            ps.setInt(6, d.getIdDOBAVLJAC());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Greska izmijeniDobavljaca: " + e.getMessage());
            return false;
        }
    }

    public boolean obrisiDobavljaca(int id) {
        String sql = "DELETE FROM DOBAVLJAC WHERE idDOBAVLJAC=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Greska obrisiDobavljaca: " + e.getMessage());
            return false;
        }
    }

    private Dobavljac mapRow(ResultSet rs) throws SQLException {
        return new Dobavljac(
                rs.getInt("idDOBAVLJAC"),
                rs.getString("Naziv"),
                rs.getString("Adresa"),
                rs.getString("Telefon"),
                rs.getString("Email"),
                rs.getInt("MJESTO_Posta")
        );
    }
}