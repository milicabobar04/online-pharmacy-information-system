package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.Lijek;
import org.example.model.LijekPoslovnica;
import org.example.model.Poslovnica;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LijekDAO {

    // Svi lijekovi
    public List<Lijek> getSviLijekovi() {
        List<Lijek> lista = new ArrayList<>();
        String sql = "SELECT * FROM LIJEK";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Greska getLijekovi: " + e.getMessage());
        }
        return lista;
    }

    // Lijekovi dostupni u poslovnici (ima na zalihama)
    public List<Lijek> getLijekoviZaPoslovnicu(int poslovnicaId) {
        List<Lijek> lista = new ArrayList<>();
        String sql = """
                SELECT l.* FROM LIJEK l
                JOIN LIJEK_has_POSLOVNICA lp ON l.idLIJEK = lp.LIJEK_idLIJEK
                WHERE lp.POSLOVNICA_idPOSLOVNICA = ? AND lp.zaliha > 0
                """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, poslovnicaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Greska getLijekoviZaPoslovnicu: " + e.getMessage());
        }
        return lista;
    }

    // Dodaj lijek
    public boolean dodajLijek(Lijek l) {
        String sql = "INSERT INTO LIJEK (Naziv, Opis, NaRecept, KATEGORIJA_idKATEGORIJA, Mjera, Cijena) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, l.getNaziv());
            ps.setString(2, l.getOpis());
            ps.setBoolean(3, l.isNaRecept());
            ps.setInt(4, l.getKategorijaId());
            ps.setString(5, l.getMjera());
            ps.setDouble(6, l.getCijena());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Greska dodajLijek: " + e.getMessage());
            return false;
        }
    }

    // Izmijeni lijek
    public boolean izmijeniLijek(Lijek l) {
        String sql = "UPDATE LIJEK SET Naziv=?, Opis=?, NaRecept=?, KATEGORIJA_idKATEGORIJA=?, Mjera=?, Cijena=? WHERE idLIJEK=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, l.getNaziv());
            ps.setString(2, l.getOpis());
            ps.setBoolean(3, l.isNaRecept());
            ps.setInt(4, l.getKategorijaId());
            ps.setString(5, l.getMjera());
            ps.setDouble(6, l.getCijena());
            ps.setInt(7, l.getIdLIJEK());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Greska izmijeniLijek: " + e.getMessage());
            return false;
        }
    }

    // Obrisi lijek
    public String obrisiLijek(int id) {
        String sql = "DELETE FROM LIJEK WHERE idLIJEK=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            return null; // uspjeh
        } catch (SQLException e) {
            System.err.println("Greska obrisiLijek: " + e.getMessage());
            return e.getMessage();
        }
    }

    private Lijek mapRow(ResultSet rs) throws SQLException {
        return new Lijek(
                rs.getInt("idLIJEK"),
                rs.getString("Naziv"),
                rs.getString("Opis"),
                rs.getBoolean("NaRecept"),
                rs.getInt("KATEGORIJA_idKATEGORIJA"),
                rs.getString("Mjera"),
                rs.getDouble("Cijena")
        );
    }
    // Lijekovi sa zalihom za poslovnicu, sa opcionalnim filterima
    public List<LijekPoslovnica> getLijekoviSaZalihom(int poslovnicaId, Integer kategorijaId, String pretraga) {
        List<LijekPoslovnica> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
                SELECT l.idLIJEK, l.Naziv, l.Cijena, l.Mjera, l.NaRecept, lp.zaliha
                FROM LIJEK l
                JOIN LIJEK_has_POSLOVNICA lp ON l.idLIJEK = lp.LIJEK_idLIJEK
                WHERE lp.POSLOVNICA_idPOSLOVNICA = ? AND lp.zaliha > 0
                """);

        if (kategorijaId != null) sql.append(" AND l.KATEGORIJA_idKATEGORIJA = ? ");
        if (pretraga != null && !pretraga.isBlank()) sql.append(" AND l.Naziv LIKE ? ");
        sql.append(" ORDER BY l.Naziv");

        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql.toString())) {
            int idx = 1;
            ps.setInt(idx++, poslovnicaId);
            if (kategorijaId != null) ps.setInt(idx++, kategorijaId);
            if (pretraga != null && !pretraga.isBlank()) ps.setString(idx++, "%" + pretraga + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new LijekPoslovnica(
                        rs.getInt("idLIJEK"),
                        rs.getString("Naziv"),
                        rs.getDouble("Cijena"),
                        rs.getString("Mjera"),
                        rs.getBoolean("NaRecept"),
                        rs.getInt("zaliha")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Greska getLijekoviSaZalihom: " + e.getMessage());
        }
        return lista;
    }
    // Pronalazi poslovnice (osim trenutne) koje imaju lijek po nazivu na stanju
    public List<Poslovnica> getPoslovniceSaLijekom(String naziv, int excludePoslovnicaId) {
        List<Poslovnica> lista = new ArrayList<>();
        String sql = """
                SELECT DISTINCT p.*
                FROM POSLOVNICA p
                JOIN LIJEK_has_POSLOVNICA lp ON p.idPOSLOVNICA = lp.POSLOVNICA_idPOSLOVNICA
                JOIN LIJEK l ON l.idLIJEK = lp.LIJEK_idLIJEK
                WHERE l.Naziv LIKE ? AND lp.zaliha > 0 AND p.idPOSLOVNICA <> ?
                """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, "%" + naziv + "%");
            ps.setInt(2, excludePoslovnicaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Poslovnica(
                        rs.getInt("idPOSLOVNICA"),
                        rs.getString("Naziv"),
                        rs.getString("Adresa"),
                        rs.getString("Telefon"),
                        rs.getString("Email"),
                        rs.getString("RadiOd"),
                        rs.getString("RadiDo"),
                        rs.getInt("MJESTO_Posta")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Greska getPoslovniceSaLijekom: " + e.getMessage());
        }
        return lista;
    }
    public int dodajNovLijekMinimalan(String naziv, int kategorijaId) {
        String sql = "INSERT INTO LIJEK (Naziv, Opis, NaRecept, KATEGORIJA_idKATEGORIJA, Cijena) " +
                "VALUES (?, 'Potrebno urediti', 0, ?, 0.00)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, naziv);
            ps.setInt(2, kategorijaId);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public Lijek getLijekPoNazivu(String naziv) {
        String sql = "SELECT * FROM LIJEK WHERE LOWER(Naziv) = LOWER(?) LIMIT 1";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, naziv);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Lijek l = new Lijek();
                l.setIdLIJEK(rs.getInt("idLIJEK"));
                l.setNaziv(rs.getString("Naziv"));
                l.setOpis(rs.getString("Opis"));
                l.setNaRecept(rs.getBoolean("NaRecept"));
                l.setKategorijaId(rs.getInt("KATEGORIJA_idKATEGORIJA"));
                l.setMjera(rs.getString("Mjera"));
                l.setCijena(rs.getDouble("Cijena"));
                return l;
            }
        } catch (SQLException e) {
            System.err.println("Greska getLijekPoNazivu: " + e.getMessage());
        }
        return null;
    }


}