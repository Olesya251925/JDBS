package org.example;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookQuery {

    // Метод для получения всех книг, отсортированных по году издания
    public static void getBooksSortedByYear() {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT * FROM books ORDER BY publishingYear";
            assert conn != null;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                System.out.println("Название книги: " + rs.getString("name") +
                        ", Автор: " + rs.getString("author") +
                        ", Год издания: " + rs.getInt("publishingYear"));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
        }
    }

    // Метод для получения книг младше 2000 года
    public static void getBooksYoungerThan2000() {
        try (Connection conn = DatabaseConnection.connect()) {
            String query = "SELECT * FROM books WHERE publishingYear < 2000";
            assert conn != null;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                System.out.println("Название книги: " + rs.getString("name") +
                        ", Автор: " + rs.getString("author") +
                        ", Год издания: " + rs.getInt("publishingYear"));
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
        }
    }
}
