package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Создаем объект Scanner для ввода с консоли
        Scanner scanner = new Scanner(System.in);

        // Цикл для повторного запроса действия
        while (true) {
            // Запрашиваем у пользователя ввод цифры для выбора действия
            System.out.println("1 - Получить все музыкальные композиции");
            System.out.println("2 - Получить музыкальные композиции без букв 'm' и 't'");
            System.out.println("3 - Добавить свою любимую композицию");
            System.out.println("4 - Выход");
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
