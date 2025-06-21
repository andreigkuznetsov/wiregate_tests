package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class DatabaseHelper {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "secret";

    public static boolean checkUserInDbById(
            long id,
            String firstName,
            String lastName,
            String phoneNumber,
            String email,
            String password
    ) throws Exception {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM users WHERE id = ?"
            );
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return
                        Objects.equals(rs.getString("first_name"), firstName) &&
                                Objects.equals(rs.getString("last_name"), lastName) &&
                                Objects.equals(rs.getString("phone_number"), phoneNumber) &&
                                Objects.equals(rs.getString("email"), email) &&
                                Objects.equals(rs.getString("password"), password);
            }
            return false;
        }
    }

    public static long findIdByEmail(String email) throws Exception {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id FROM users WHERE email = ? ORDER BY id DESC LIMIT 1"
            );
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("id");
            }
            throw new IllegalStateException("User not found with email: " + email);
        }
    }

    public static void deleteUserById(long id) throws Exception {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM users WHERE id = ?"
            );
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }
}