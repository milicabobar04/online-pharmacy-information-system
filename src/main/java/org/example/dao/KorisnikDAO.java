package org.example.dao;

import org.example.db.DatabaseConnection;
import org.example.model.Korisnik;
import org.example.model.Zaposleni;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KorisnikDAO {
    public Korisnik login(String korisnickoIme, String lozinka){
        String sql = """
                SELECT k.*, z.Ime, z.Prezime, z.Pozicija, z.Plata, z.DatumZaposlenja, z.JMBG
                FROM KORISNIK k
                JOIN ZAPOSLENI z ON z.idZAPOSLENI = k.ZAPOSLENI_idZAPOSLENI
                WHERE k.korisnickoIme = ? AND k.lozinka = ?
                """;

        try(PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)){
            ps.setString(1, korisnickoIme);
            ps.setString(2, lozinka);

            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                Korisnik k = new Korisnik(
                        rs.getInt("idKORISNIK"),
                        rs.getString("korisnickoIme"),
                        rs.getString("lozinka"),
                        rs.getString("uloga"),
                        rs.getInt("ZAPOSLENI_idZAPOSLENI")
                );
                Zaposleni z = new Zaposleni(
                        rs.getInt("ZAPOSLENI_idZAPOSLENI"),
                        rs.getString("Ime"),
                        rs.getString("Prezime"),
                        rs.getString("Pozicija"),
                        rs.getDouble("Plata"),
                        rs.getDate("DatumZaposlenja").toLocalDate(),
                        rs.getString("JMBG")
                );
                k.setZaposleni(z);
                return k;

            }
        }catch (SQLException e){
            System.err.println("Greska login: " + e.getMessage());
        }
        return null;
    }

    // Dodavanje korisnika - koristi manager
    public int dodajKorisnika(Korisnik k) {
        String sql = "INSERT INTO KORISNIK (korisnickoIme, lozinka, uloga, ZAPOSLENI_idZAPOSLENI) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, k.getKorisnickoIme());
            ps.setString(2, k.getLozinka());
            ps.setString(3, k.getUloga());
            ps.setInt(4, k.getZaposleniId());
            ps.executeUpdate();
            return 1; // uspjeh

        } catch (com.mysql.cj.jdbc.exceptions.MysqlDataTruncation e) {
            e.printStackTrace();
            return -1;
        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
            // Duplicate entry — unique constraint na korisnickoIme
            return 0;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }


    // Provjeri postoji li vec nalog za zaposlenog
    public Korisnik getKorisnikZaZaposlenog(int zaposleniId) {
        String sql = "SELECT * FROM KORISNIK WHERE ZAPOSLENI_idZAPOSLENI=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, zaposleniId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Korisnik(
                        rs.getInt("idKORISNIK"),
                        rs.getString("korisnickoIme"),
                        rs.getString("lozinka"),
                        rs.getString("uloga"),
                        rs.getInt("ZAPOSLENI_idZAPOSLENI")
                );
            }
        } catch (SQLException e) {
            System.err.println("Greska getKorisnikZaZaposlenog: " + e.getMessage());
        }
        return null;
    }
}
