package org.example;

import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        Connection connection = DatabaseConnection.connect();

        if (connection != null) {
            System.out.println("Подключение к базе данных установлено.");
        } else {
            System.out.println("Не удалось подключиться к базе данных.");
        }
    }
}
