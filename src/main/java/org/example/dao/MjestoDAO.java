package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.Mjesto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MjestoDAO {
    public List<Mjesto> getSvaMjesta() {
        List<Mjesto> lista = new ArrayList<>();
        String sql = "SELECT * FROM MJESTO ORDER BY naziv";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Mjesto(rs.getInt("Posta"), rs.getString("naziv")));
            }
        } catch (SQLException e) {
            System.err.println("Greska getSvaMjesta: " + e.getMessage());
        }
        return lista;
    }
}