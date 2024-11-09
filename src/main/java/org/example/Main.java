package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Создаем объект Scanner для ввода с консоли
        Scanner scanner = new Scanner(System.in);

        // Цикл для повторного запроса действия
        while (true) {
            // Запрашиваем у пользователя ввод цифры для выбора действия
            System.out.println("\n1 - Получить все музыкальные композиции");
            System.out.println("2 - Получить музыкальные композиции без букв 'm' и 't'");
            System.out.println("3 - Добавить свою любимую композицию");
            System.out.println("4 - Получить все книги, отсортированные по году издания");
            System.out.println("5 - Получить книги младше 2000 года");
            System.out.println("6 - Выход");
            System.out.println("Выберите действие:");

            // Чтение введенной цифры
            int action = scanner.nextInt();
            scanner.nextLine(); // Чтение новой строки после ввода числа

            // Выполнение соответствующего действия
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
                    // Выход из программы
                    System.out.println("Выход из программы.");
                    scanner.close();
                    return; // Завершаем программу
                default:
                    System.out.println("Неверный выбор.");
            }
        }
    }
}
