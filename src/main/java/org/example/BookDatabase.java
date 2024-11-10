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
    public static Connection connect() {
        return DatabaseConnection.connect(); // Используем метод connect из класса DatabaseConnection
    }

    // Создание таблиц, если они не существуют
    public static void createTablesIfNotExist(Connection conn) {
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

        // Дополнительная таблица для связи пользователей с книгами
        String createUserBooksTable = "CREATE TABLE IF NOT EXISTS user_books (" +
                "user_id INT NOT NULL, " +
                "book_id INT NOT NULL, " +
                "PRIMARY KEY (user_id, book_id), " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE)";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createUsersTable);
            stmt.execute(createBooksTable);
            stmt.execute(createUserBooksTable); // Создаем таблицу связей
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
                System.out.println("\nПользователь с телефоном " + phone + " уже существует.");
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

    // Вставка связи между пользователем и книгой
    private static void insertUserBook(Connection conn, int userId, int bookId) {
        String insertUserBook = "INSERT INTO user_books (user_id, book_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertUserBook)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, bookId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Ошибка при вставке связи пользователя с книгой: " + e.getMessage());
        }
    }

    // Метод для добавления нового пользователя
    public static void addUser(String name, String surname, String phone, boolean subscribed) {
        try (Connection connection = connect()) {
            assert connection != null;
            insertUserIfNotExists(connection, name, surname, phone, subscribed);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Метод для добавления книги
    public static void addBook(String bookName, String author, int publishingYear, String isbn, String publisher) {
        try (Connection connection = connect()) {
            assert connection != null;
            insertBookIfNotExists(connection, bookName, author, publishingYear, isbn, publisher);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Метод для связывания пользователя с книгой
    public static void linkUserWithBook(String phone, String isbn) {
        // Находим user_id по телефону
        String getUserIdQuery = "SELECT id FROM users WHERE phone = ?";
        String getBookIdQuery = "SELECT id FROM books WHERE isbn = ?";

        try (Connection connection = connect()) {
            // Получаем user_id по номеру телефона
            assert connection != null;
            PreparedStatement userStmt = connection.prepareStatement(getUserIdQuery);
            userStmt.setString(1, phone);
            var userResult = userStmt.executeQuery();
            if (userResult.next()) {
                int userId = userResult.getInt("id");

                // Получаем book_id по ISBN
                PreparedStatement bookStmt = connection.prepareStatement(getBookIdQuery);
                bookStmt.setString(1, isbn);
                var bookResult = bookStmt.executeQuery();
                if (bookResult.next()) {
                    int bookId = bookResult.getInt("id");

                    // Создаем запись в таблице user_books
                    insertUserBook(connection, userId, bookId);
                    System.out.println("Связь между пользователем и книгой успешно создана.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean userExists(String phone) {
        String sql = "SELECT COUNT(*) FROM users WHERE phone = ?";
        try (Connection connection = connect()) {
            assert connection != null;
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, phone);
                var resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;  // Если количество пользователей больше 0, значит, пользователь существует
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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

                insertBookIfNotExists(conn, bookName, author, publishingYear, isbn, publisher);
                // Связываем пользователя с книгой
                linkUserWithBook(phone, isbn);
            }
        }
    }

    // Метод для загрузки данных из файла JSON в базу данных
    public static void loadDataFromJson() {
        try (Connection connection = connect()) {
            assert connection != null;
            String jsonData = readJsonFromFile();
            if (jsonData != null) {
                insertDataFromJson(connection, jsonData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void getUserInfoWithBooks(String phone) {
        String userInfoQuery = "SELECT u.name, u.surname, u.phone, u.subscribed, b.name AS bookName, " +
                "b.author, b.publishingYear, b.isbn, b.publisher " +
                "FROM users u " +
                "LEFT JOIN user_books ub ON u.id = ub.user_id " +
                "LEFT JOIN books b ON ub.book_id = b.id " +
                "WHERE u.phone = ?";

        try (Connection connection = connect();
             PreparedStatement pstmt = connection.prepareStatement(userInfoQuery)) {

            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();

            boolean userFound = false;
            while (rs.next()) {
                if (!userFound) {
                    System.out.println("\nИнформация о пользователе:");
                    System.out.println("Имя: " + rs.getString("name"));
                    System.out.println("Фамилия: " + rs.getString("surname"));
                    System.out.println("Телефон: " + rs.getString("phone"));
                    System.out.println("Подписан на рассылку: " + rs.getBoolean("subscribed"));
                    userFound = true;
                }
                System.out.println("\nКнига:");
                System.out.println("Название: " + rs.getString("bookName"));
                System.out.println("Автор: " + rs.getString("author"));
                System.out.println("Год издания: " + rs.getInt("publishingYear"));
                System.out.println("ISBN: " + rs.getString("isbn"));
                System.out.println("Издательство: " + rs.getString("publisher"));
            }

            if (!userFound) {
                System.out.println("Пользователь с таким номером телефона не найден.");
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении информации о пользователе и его книгах: " + e.getMessage());
        }
    }

    public static void deleteTables() {
        String deleteUserBooksTable = "DROP TABLE IF EXISTS user_books";
        String deleteUsersTable = "DROP TABLE IF EXISTS users";
        String deleteBooksTable = "DROP TABLE IF EXISTS books";

        try (Connection connection = connect();
             Statement stmt = connection.createStatement()) {

            // Удаляем таблицу связей
            stmt.executeUpdate(deleteUserBooksTable);
            System.out.println("Таблица связей удалена.");

            // Удаляем таблицу пользователей
            stmt.executeUpdate(deleteUsersTable);
            System.out.println("Таблица посетителей удалена.");

            // Удаляем таблицу книг
            stmt.executeUpdate(deleteBooksTable);
            System.out.println("Таблица книг удалена.");

        } catch (SQLException e) {
            System.out.println("Ошибка при удалении таблиц: " + e.getMessage());
        }
    }


}
