package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MusicQuery {
    // Метод для получения всех музыкальных композиций
    public static void getAllMusic() {
        String query = "SELECT * FROM music";

        try (Connection connection = DatabaseConnection.connect()) {
            assert connection != null;
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet resultSet = stmt.executeQuery()) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");  // Получаем id
                    String name = resultSet.getString("name");  // Получаем название песни
                    System.out.println("ID: " + id + ", Песня: " + name);  // Выводим id и название песни
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении данных: " + e.getMessage());
        }
    }

    // Метод для получения музыкальных композиций без букв "m" и "t"
    public static void getMusicWithoutMAndT() {
        String query = "SELECT * FROM music WHERE name NOT LIKE '%m%' AND name NOT LIKE '%t%'";

        try (Connection connection = DatabaseConnection.connect()) {
            assert connection != null;
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet resultSet = stmt.executeQuery()) {

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");  // Получаем id
                    String name = resultSet.getString("name");  // Получаем название песни
                    System.out.println("ID: " + id + ", Песня: " + name);  // Выводим id и название песни
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении данных: " + e.getMessage());
        }
    }

    // Метод для добавления пользовательской композиции в базу данных
    public static void insertFavoriteSong(String songName) {
        String insertSQL = "INSERT INTO music (name) VALUES (?);";

        try (Connection connection = DatabaseConnection.connect()) {
            assert connection != null;
            try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
                stmt.setString(1, songName);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Ваша любимая композиция '" + songName + "' успешно добавлена в базу данных.");
                } else {
                    System.out.println("Ошибка при добавлении композиции.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении композиции в базу данных: " + e.getMessage());
        }
    }
}
