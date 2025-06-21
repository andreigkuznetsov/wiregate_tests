package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class DatabaseHelper {
    private static final Logger log = LoggerFactory.getLogger(DatabaseHelper.class);

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
                boolean match = Objects.equals(rs.getString("first_name"), firstName)
                        && Objects.equals(rs.getString("last_name"), lastName)
                        && Objects.equals(rs.getString("phone_number"), phoneNumber)
                        && Objects.equals(rs.getString("email"), email)
                        && Objects.equals(rs.getString("password"), password);

                if (match) {
                    log.info("Пользователь с id={} найден и все поля совпадают: имя='{}', фамилия='{}', телефон='{}', email='{}', пароль='{}'.",
                            id, firstName, lastName, phoneNumber, email, password);
                } else {
                    log.warn("Пользователь с id={} найден, но значения полей отличаются: из БД — имя='{}', фамилия='{}', телефон='{}', email='{}', пароль='{}'.",
                            id, rs.getString("first_name"), rs.getString("last_name"),
                            rs.getString("phone_number"), rs.getString("email"), rs.getString("password"));
                }
                return match;
            }
            log.warn("Пользователь с id={} не найден в базе данных.", id);
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
                long id = rs.getLong("id");
                log.info("ID пользователя для email '{}' найден: {}.", email, id);
                return id;
            }
            log.warn("Пользователь с email '{}' не найден в базе данных.", email);
            throw new IllegalStateException("Пользователь не найден с email: " + email);
        }
    }

    public static void deleteUserById(long id) throws Exception {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM users WHERE id = ?"
            );
            stmt.setLong(1, id);
            int result = stmt.executeUpdate();
            if (result > 0) {
                log.info("Пользователь с id={} успешно удалён из базы данных.", id);
            } else {
                log.warn("Пользователь с id={} не был найден для удаления.", id);
            }
        }
    }
}