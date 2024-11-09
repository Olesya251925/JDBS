package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection connect() {
        String url = "jdbc:postgresql://localhost:5433/java"; // Замените на имя вашей БД
        String user = "postgres"; // Замените на имя пользователя
        String password = "251925"; // Замените на пароль

        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println("Ошибка подключения к базе данных: " + e.getMessage());
            return null;
        }
    }
}
