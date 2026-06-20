package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.NabavnaNarudzba;
import org.example.model.NabavnaStavka;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NabavnaNarudzbaDAO {

    // Kreira header nabavne narudzbe, vraca njen id
    public int kreirajNabavnu(int dobavljacId, int zaposleniId, int poslovnicaId, String napomena) {
        String sql = "CALL sp_kreiraj_nabavnu_narudzbu(?,?,?,?,?)";
        try (CallableStatement cs = DatabaseConnection.getConnection().prepareCall(sql)) {
            cs.setInt(1, dobavljacId);
            cs.setInt(2, zaposleniId);
            cs.setInt(3, poslovnicaId);
            cs.setString(4, napomena);
            cs.registerOutParameter(5, Types.INTEGER);
            cs.execute();
            return cs.getInt(5);
        } catch (SQLException e) {
            System.err.println("Greska kreirajNabavnu: " + e.getMessage());
            return -1;
        }
    }

    // Dodaje stavku i azurira ukupnu vrijednost headera
    public boolean dodajStavku(int nabavnaId, int lijekId, int kolicina, double jedinicnaCijena, LocalDate rokTrajanja) {
        String sqlInsert = """
                INSERT INTO NABAVNA_NARUDZBA_STAVKA
                (NABAVNA_NARUDZBA_idNABAVNA_NARUDZBA, kolicina, jedinicnaCIjena, ukupnaCijena, rokTrajanja, LIJEK_idLIJEK)
                VALUES (?,?,?,?,?,?)
                """;
        String sqlUpdate = """
                UPDATE NABAVNA_NARUDZBA
                SET UkupnaVrijednost = (
                    SELECT SUM(ukupnaCijena) FROM NABAVNA_NARUDZBA_STAVKA
                    WHERE NABAVNA_NARUDZBA_idNABAVNA_NARUDZBA = ?
                )
                WHERE idNABAVNA_NARUDZBA = ?
                """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sqlInsert)) {
            double ukupno = kolicina * jedinicnaCijena;
            ps.setInt(1, nabavnaId);
            ps.setInt(2, kolicina);
            ps.setDouble(3, jedinicnaCijena);
            ps.setDouble(4, ukupno);
            ps.setDate(5, Date.valueOf(rokTrajanja));
            ps.setInt(6, lijekId);
            ps.executeUpdate();

            try (PreparedStatement ps2 = DatabaseConnection.getConnection().prepareStatement(sqlUpdate)) {
                ps2.setInt(1, nabavnaId);
                ps2.setInt(2, nabavnaId);
                ps2.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Greska dodajStavku (nabavna): " + e.getMessage());
            return false;
        }
    }

    public List<NabavnaNarudzba> getNabavneZaPoslovnicu(int poslovnicaId) {
        List<NabavnaNarudzba> lista = new ArrayList<>();
        String sql = """
                SELECT nn.idNABAVNA_NARUDZBA, nn.datumNarudzbe, nn.datumIsporuke,
                       nn.UkupnaVrijednost, nn.status, nn.napomena, d.Naziv AS dobavljac,
                       nn.POSLOVNICA_idPOSLOVNICA
                FROM NABAVNA_NARUDZBA nn
                JOIN DOBAVLJAC d ON d.idDOBAVLJAC = nn.DOBAVLJAC_idDOBAVLJAC
                WHERE nn.POSLOVNICA_idPOSLOVNICA = ?
                ORDER BY nn.datumNarudzbe DESC, nn.idNABAVNA_NARUDZBA DESC
                """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, poslovnicaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new NabavnaNarudzba(
                        rs.getInt("idNABAVNA_NARUDZBA"),
                        rs.getDate("datumNarudzbe").toLocalDate(),
                        rs.getDate("datumIsporuke") != null ? rs.getDate("datumIsporuke").toLocalDate() : null,
                        rs.getDouble("UkupnaVrijednost"),
                        rs.getString("status"),
                        rs.getString("napomena"),
                        rs.getString("dobavljac"),
                        rs.getInt("POSLOVNICA_idPOSLOVNICA")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Greska getNabavneZaPoslovnicu: " + e.getMessage());
        }
        return lista;
    }

    public List<NabavnaStavka> getStavkeNabavne(int nabavnaId) {
        List<NabavnaStavka> lista = new ArrayList<>();
        String sql = """
                SELECT l.Naziv, s.kolicina, s.jedinicnaCIjena, s.ukupnaCijena, s.rokTrajanja
                FROM NABAVNA_NARUDZBA_STAVKA s
                JOIN LIJEK l ON l.idLIJEK = s.LIJEK_idLIJEK
                WHERE s.NABAVNA_NARUDZBA_idNABAVNA_NARUDZBA = ?
                """;
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, nabavnaId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new NabavnaStavka(
                        rs.getString("Naziv"),
                        rs.getInt("kolicina"),
                        rs.getDouble("jedinicnaCIjena"),
                        rs.getDouble("ukupnaCijena"),
                        rs.getDate("rokTrajanja") != null ? rs.getDate("rokTrajanja").toLocalDate() : null
                ));
            }
        } catch (SQLException e) {
            System.err.println("Greska getStavkeNabavne: " + e.getMessage());
        }
        return lista;
    }

    // Promjena statusa - na 'Isporucena' trigger automatski povecava zalihe
    public boolean promijeniStatus(int nabavnaId, String status, LocalDate datumIsporuke) {
        String sql = "UPDATE NABAVNA_NARUDZBA SET status=?, datumIsporuke=? WHERE idNABAVNA_NARUDZBA=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, status);
            if (datumIsporuke != null) ps.setDate(2, Date.valueOf(datumIsporuke));
            else ps.setNull(2, Types.DATE);
            ps.setInt(3, nabavnaId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Greska promijeniStatus (nabavna): " + e.getMessage());
            return false;
        }
    }
    public boolean obrisiNabavnuNarudzbu(int id) {
        String sql = "DELETE FROM NABAVNA_NARUDZBA WHERE idNABAVNA_NARUDZBA=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Greska obrisiPoslovnicu: " + e.getMessage());
            return false;
        }
    }
}