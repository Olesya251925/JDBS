package org.example;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BookDatabase {

    // Подключение к базе данных через класс DatabaseConnection
    private static Connection connect() {
        return DatabaseConnection.connect(); // Используем метод connect из класса DatabaseConnection
    }

    // Создание таблиц, если они не существуют
    private static void createTablesIfNotExist(Connection conn) {
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "surname VARCHAR(100) NOT NULL, " +
                "phone VARCHAR(50) NOT NULL, " +
                "subscribed BOOLEAN NOT NULL)";

        String createBooksTable = "CREATE TABLE IF NOT EXISTS books (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "author VARCHAR(255) NOT NULL, " +
                "publishingYear INT NOT NULL, " +
                "isbn VARCHAR(50) NOT NULL UNIQUE, " +
                "publisher VARCHAR(255) NOT NULL)";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createBooksTable);
        } catch (SQLException e) {
            System.out.println("Ошибка при создании таблиц: " + e.getMessage());
        }
    }

    // Вставка пользователя, если его нет
    private static void insertUserIfNotExists(Connection conn, String name, String surname, String phone, boolean subscribed) {
        String insertUser = "INSERT INTO users (name, surname, phone, subscribed) " +
                "SELECT ?, ?, ?, ? WHERE NOT EXISTS (SELECT 1 FROM users WHERE phone = ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertUser)) {
            pstmt.setString(1, name);
            pstmt.setString(2, surname);
            pstmt.setString(3, phone);
            pstmt.setBoolean(4, subscribed);
            pstmt.setString(5, phone); // Уникальность проверяется по телефону
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Новый пользователь добавлен: " + name + " " + surname);
            } else {
                System.out.println("Пользователь с телефоном " + phone + " уже существует.");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении пользователя: " + e.getMessage());
        }
    }

    // Вставка книги, если она не существует
    private static void insertBookIfNotExists(Connection conn, String name, String author, int publishingYear, String isbn, String publisher) {
        String insertBook = "INSERT INTO books (name, author, publishingYear, isbn, publisher) " +
                "SELECT ?, ?, ?, ?, ? WHERE NOT EXISTS (SELECT 1 FROM books WHERE isbn = ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertBook)) {
            pstmt.setString(1, name);
            pstmt.setString(2, author);
            pstmt.setInt(3, publishingYear);
            pstmt.setString(4, isbn);
            pstmt.setString(5, publisher);
            pstmt.setString(6, isbn); // Уникальность проверяется по ISBN
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Новая книга добавлена: " + name + " автор: " + author);
            } else {
                System.out.println("Книга с ISBN " + isbn + " уже существует.");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении книги: " + e.getMessage());
        }
    }

    // Метод для чтения файла JSON из resources
    private static String readJsonFromFile() {
        try (InputStream inputStream = BookDatabase.class.getClassLoader().getResourceAsStream("books.json")) {
            if (inputStream == null) {
                throw new IOException("Файл не найден: " + "books.json");
            }

            // Преобразуем InputStream в строку
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int nRead;
            byte[] buffer = new byte[1024];
            while ((nRead = inputStream.read(buffer, 0, buffer.length)) != -1) {
                byteArrayOutputStream.write(buffer, 0, nRead);
            }

            return byteArrayOutputStream.toString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Ошибка при чтении JSON файла: " + e.getMessage());
            return null;
        }
    }

    // Вставка данных из JSON
    private static void insertDataFromJson(Connection conn, String jsonData) {
        JSONArray users = new JSONArray(jsonData);

        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            String name = user.getString("name");
            String surname = user.getString("surname");
            String phone = user.getString("phone");
            boolean subscribed = user.getBoolean("subscribed");

            // Вставляем пользователя, если его нет
            insertUserIfNotExists(conn, name, surname, phone, subscribed);

            // Вставляем книги, если их нет
            JSONArray favoriteBooks = user.getJSONArray("favoriteBooks");
            for (int j = 0; j < favoriteBooks.length(); j++) {
                JSONObject book = favoriteBooks.getJSONObject(j);
                String bookName = book.getString("name");
                String author = book.getString("author");
                int publishingYear = book.getInt("publishingYear");
                String isbn = book.getString("isbn");
                String publisher = book.getString("publisher");

                // Вставляем книгу, если ее нет
                insertBookIfNotExists(conn, bookName, author, publishingYear, isbn, publisher);
            }
        }
    }

    public static void main(String[] args) {
        // Загружаем данные из JSON файла в папке resources
        String jsonData = readJsonFromFile();

        // Подключаемся к базе данных
        try (Connection conn = connect()) {
            if (conn != null && jsonData != null) {
                createTablesIfNotExist(conn);
                insertDataFromJson(conn, jsonData);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка подключения к базе данных: " + e.getMessage());
        }
    }
}
