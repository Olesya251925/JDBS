package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

public class JsonDataLoader {

    // Метод для чтения файла JSON из ресурсов
    private static String readJsonFromFile() {
        try (InputStream inputStream = JsonDataLoader.class.getClassLoader().getResourceAsStream("books.json")) {
            if (inputStream == null) {
                throw new IOException("Файл не найден: books.json");
            }

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

    // Вставка данных из JSON в базу данных
    private static void insertDataFromJson(Connection conn, String jsonData) {
        JSONArray users = new JSONArray(jsonData);

        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            String name = user.getString("name");
            String surname = user.getString("surname");
            String phone = user.getString("phone");
            boolean subscribed = user.getBoolean("subscribed");

            // Вставляем пользователя, если его нет
            BookDatabase.insertUserIfNotExists(conn, name, surname, phone, subscribed);

            // Вставляем книги, если их нет
            JSONArray favoriteBooks = user.getJSONArray("favoriteBooks");
            for (int j = 0; j < favoriteBooks.length(); j++) {
                JSONObject book = favoriteBooks.getJSONObject(j);
                String bookName = book.getString("name");
                String author = book.getString("author");
                int publishingYear = book.getInt("publishingYear");
                String isbn = book.getString("isbn");
                String publisher = book.getString("publisher");

                BookDatabase.insertBookIfNotExists(conn, bookName, author, publishingYear, isbn, publisher);
                // Связываем пользователя с книгой
                BookDatabase.linkUserWithBook(phone, isbn);
            }
        }
    }

    // Метод для загрузки данных из JSON-файла в базу данных
    public static void loadDataFromJson() {
        try (Connection connection = BookDatabase.connect()) {
            assert connection != null;
            String jsonData = readJsonFromFile();
            if (jsonData != null) {
                insertDataFromJson(connection, jsonData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
