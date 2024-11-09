package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MusicDatabase {
    // Метод для создания таблицы, если она не существует
    public static void createMusicTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS music ("
                + "id SERIAL PRIMARY KEY, "
                + "name TEXT NOT NULL"
                + ");";

        try (Connection connection = DatabaseConnection.connect();
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(createTableSQL);
            System.out.println("Таблица music успешно создана или уже существует.");
        } catch (SQLException e) {
            System.out.println("Ошибка при создании таблицы: " + e.getMessage());
        }
    }

    // Метод для вставки данных, проверяя, что этих данных нет в таблице
    public static void insertMusicData() {
        String[] musicData = {
                "Bohemian Rhapsody", "Stairway to Heaven", "Imagine", "Sweet Child O Mine", "Hey Jude",
                "Hotel California", "Billie Jean", "Wonderwall", "Smells Like Teen Spirit", "Let It Be",
                "I Want It All", "November Rain", "Losing My Religion", "One", "With or Without You",
                "Sweet Caroline", "Yesterday", "Dont Stop Believin", "Crazy Train", "Always"
        };

        try (Connection connection = DatabaseConnection.connect()) {
            for (String song : musicData) {
                if (!isSongExists(connection, song)) {
                    String insertSQL = "INSERT INTO music (name) VALUES (?);";
                    try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
                        stmt.setString(1, song);
                        stmt.executeUpdate();
                        System.out.println("Добавлена песня: " + song);
                    } catch (SQLException e) {
                        System.out.println("Ошибка при вставке данных: " + e.getMessage());
                    }
                } else {
                    System.out.println("Песня '" + song + "' уже существует в базе данных.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при подключении к базе данных: " + e.getMessage());
        }
    }

    // Метод для проверки существования песни в базе данных
    private static boolean isSongExists(Connection connection, String songName) throws SQLException {
        String checkSQL = "SELECT COUNT(*) FROM music WHERE name = ?;";
        try (PreparedStatement stmt = connection.prepareStatement(checkSQL)) {
            stmt.setString(1, songName);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0; // Если строка существует, вернется больше 0
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        createMusicTable();
        insertMusicData();
    }
}
