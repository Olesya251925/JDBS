package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Создаем объект Scanner для ввода с консоли
        Scanner scanner = new Scanner(System.in);

        // Цикл для повторного запроса действия
        while (true) {
            System.out.println("\n1 - Получить все музыкальные композиции");
            System.out.println("2 - Получить музыкальные композиции без букв 'm' и 't'");
            System.out.println("3 - Добавить свою любимую композицию");
            System.out.println("4 - Получить все книги, отсортированные по году издания");
            System.out.println("5 - Получить книги младше 2000 года");
            System.out.println("6 - Ввести данные о себе и своих книгах");
            System.out.println("7 - Выход");
            System.out.println("Выберите действие:");

            int action = scanner.nextInt();
            scanner.nextLine(); // Чтение новой строки после ввода числа

            switch (action) {
                case 1:
                    // Вызов метода для получения всех музыкальных композиций
                    MusicQuery.getAllMusic();
                    break;
                case 2:
                    // Вызов метода для получения музыкальных композиций без букв "m" и "t"
                    MusicQuery.getMusicWithoutMAndT();
                    break;
                case 3:
                    // Запрос у пользователя любимой песни и добавление в базу данных
                    System.out.println("Введите название вашей любимой композиции:");
                    String favoriteSong = scanner.nextLine();
                    MusicQuery.insertFavoriteSong(favoriteSong);
                    break;
                case 4:
                    // Вызов метода для получения всех книг, отсортированных по году издания
                    BookQuery.getBooksSortedByYear();
                    break;
                case 5:
                    // Вызов метода для получения книг младше 2000 года
                    BookQuery.getBooksYoungerThan2000();
                    break;
                case 6:
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

                    // Проверка, существует ли уже пользователь
                    if (BookDatabase.userExists(phone)) {
                        System.out.println("Такой пользователь уже существует.");
                        System.out.println("Хотите записать вашу любимую книгу? (да/нет)");

                        String response = scanner.nextLine();
                        if (response.equalsIgnoreCase("да")) {
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

                            // Добавление книги в базу данных
                            BookDatabase.addBook(bookName, author, year, isbn, publisher);

                            // Связывание книги с пользователем
                            BookDatabase.linkUserWithBook(phone, isbn);

                            System.out.println("Ваша любимая книга добавлена в базу данных.");
                        }
                    } else {
                        // Добавление нового пользователя
                        BookDatabase.addUser(name, surname, phone, subscribed);

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

                        // Добавление книги в базу данных
                        BookDatabase.addBook(bookName, author, year, isbn, publisher);

                        // Связывание книги с пользователем
                        BookDatabase.linkUserWithBook(phone, isbn);

                        // Уведомление о завершении
                        System.out.println("Ваши данные успешно внесены в базу данных.");
                    }
                    break;
                case 7:
                    // Выход из программы
                    System.out.println("Выход из программы.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверный выбор.");
            }
        }
    }
}
