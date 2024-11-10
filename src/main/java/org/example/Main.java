package org.example;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

import static org.example.BookDatabase.loadDataFromJson;

public class Main {
    public static void main(String[] args) {
        // Создаем объект Scanner для ввода с консоли
        Scanner scanner = new Scanner(System.in);

        // Цикл для повторного запроса действия
        while (true) {
            System.out.println("\n1 - Создать таблицу музыки");
            System.out.println("2 - Получить все музыкальные композиции");
            System.out.println("3 - Получить музыкальные композиции без букв 'm' и 't'");
            System.out.println("4 - Добавить свою любимую композицию");
            System.out.println("5 - Создать таблицы книг и посетителей");
            System.out.println("6 - Получить все книги, отсортированные по году издания");
            System.out.println("7 - Получить книги младше 2000 года");
            System.out.println("8 - Ввести данные о себе и своих книгах");
            System.out.println("9 - Удалить таблицы");
            System.out.println("10 - Выход");
            System.out.println("Выберите действие:");

            int action = scanner.nextInt();
            scanner.nextLine(); // Чтение новой строки после ввода числа

            switch (action) {
                case 1:
                    // Создание таблицы для музыки и добавление песен
                    MusicDatabase.createMusicTable();
                    MusicDatabase.insertMusicData();
                    break;
                case 2:
                    // Вызов метода для получения всех музыкальных композиций
                    MusicQuery.getAllMusic();
                    break;
                case 3:
                    // Вызов метода для получения музыкальных композиций без букв "m" и "t"
                    MusicQuery.getMusicWithoutMAndT();
                    break;
                case 4:
                    // Запрос у пользователя любимой песни и добавление в базу данных
                    System.out.println("Введите название вашей любимой композиции:");
                    String favoriteSong = scanner.nextLine();
                    MusicQuery.insertFavoriteSong(favoriteSong);
                    break;
                case 5:
                    try (Connection connection = BookDatabase.connect()) {
                        BookDatabase.createTablesIfNotExist(connection);
                        loadDataFromJson();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 6:
                    // Вызов метода для получения всех книг, отсортированных по году издания
                    BookQuery.getBooksSortedByYear();
                    break;
                case 7:
                    // Вызов метода для получения книг младше 2000 года
                    BookQuery.getBooksYoungerThan2000();
                    break;
                case 8:
                    // Ввод данных о пользователе и добавление книг
                    handleUserData(scanner);
                    break;
                case 9:
                    // Удаление таблиц
                    BookDatabase.deleteTables();
                    break;
                case 10:
                    // Выход из программы
                    System.out.println("Выход из программы.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверный выбор.");
            }
        }
    }

    private static void handleUserData(Scanner scanner) {
        // Ввод данных о пользователе
        System.out.println("Введите ваше имя:");
        String name = scanner.nextLine();
        System.out.println("Введите вашу фамилию:");
        String surname = scanner.nextLine();
        System.out.println("Введите ваш телефон:");
        String phone = scanner.nextLine();
        System.out.println("Вы подписаны на рассылку (true/false)?");
        boolean subscribed = scanner.nextBoolean();
        scanner.nextLine();  // Чтение новой строки

        if (BookDatabase.userExists(phone)) {
            System.out.println("Такой пользователь уже существует.");
            System.out.println("Хотите записать вашу любимую книгу? (да/нет)");

            String response = scanner.nextLine();
            if (response.equalsIgnoreCase("да")) {
                // Ввод данных о любимой книге и добавление в базу данных
                addFavoriteBook(scanner, phone);
            }
        } else {
            // Добавление нового пользователя
            BookDatabase.addUser(name, surname, phone, subscribed);
            // Ввод данных о любимой книге и добавление в базу данных
            addFavoriteBook(scanner, phone);
        }
    }

    private static void addFavoriteBook(Scanner scanner, String phone) {
        // Ввод данных о любимой книге
        System.out.println("Введите название вашей любимой книги:");
        String bookName = scanner.nextLine();
        System.out.println("Введите автора книги:");
        String author = scanner.nextLine();
        System.out.println("Введите год издания книги:");
        int year = scanner.nextInt();
        scanner.nextLine();  // Чтение новой строки
        System.out.println("Введите ISBN книги:");
        String isbn = scanner.nextLine();
        System.out.println("Введите издательство книги:");
        String publisher = scanner.nextLine();

        // Добавление книги и связывание с пользователем
        BookDatabase.addBook(bookName, author, year, isbn, publisher);
        BookDatabase.linkUserWithBook(phone, isbn);

        System.out.println("Ваша любимая книга добавлена в базу данных.");
        BookDatabase.getUserInfoWithBooks(phone);  // Вывод информации о пользователе и книгах
    }
}
