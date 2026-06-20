package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.Kategorija;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KategorijaDAO {

    public List<Kategorija> getSveKategorije() {
        List<Kategorija> lista = new ArrayList<>();
        String sql = "SELECT * FROM KATEGORIJA ORDER BY Naziv";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Kategorija(
                        rs.getInt("idKATEGORIJA"),
                        rs.getString("Naziv"),
                        rs.getString("Opis")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Greska getSveKategorije: " + e.getMessage());
        }
        return lista;
    }
}