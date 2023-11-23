package org.example;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class AnnotationProcessor {
    public static void main(String[] args) throws Exception {
        // Сериализуем класс в текстовый файл
        System.out.println("\nSerialization: ");
        MyTestClass a = new MyTestClass();
        MySerialization(a, "A.txt");

        // Десериализуем текстовый файл в класс
        System.out.println("\nDeserialization: ");
        MyTestClass b = (MyTestClass) MyDeserialization("B.txt");
        b.print();
    }

    static void MySerialization(Object a, String fileName) throws Exception {
        // Init
        int fieldsToSerialize = 0;
        HashMap<String, String> fieldsValues = new HashMap<>();

        // Get fields
        Field[] fields = a.getClass().getDeclaredFields(); // Получаем все поля класса по типу класса

        // Add serializable filds to HashMap
        for (Field field : fields) {
            // Провверяем наличие аннотации @RxField у очередного поля
            if (field.isAnnotationPresent(RxField.class)) {
                ++fieldsToSerialize;
                System.out.println("Field " + field.getName() + " have @RxField annotation");
                field.setAccessible(true); // Форсируем получение доступа к полю (private/protected)
                fieldsValues.put(field.getName(), field.get(a).toString()); // Добавляем информацию о поле
            } else {
                System.out.println("Field " + field.getName() + " do not have @RxField annotation");
            }
        }

        // Serialize
        BufferedWriter fileIO = new BufferedWriter(new FileWriter(fileName));
        fileIO.write(a.getClass().getName() + '\n'); // Записываем имя класса
        fileIO.write(Integer.toString(fieldsToSerialize)); // Записываем количество объектов
        for (String key : fieldsValues.keySet()) {
            // Записываем объекты: имя=значение
            fileIO.write('\n' + key + "=" + fieldsValues.get(key));
        }
        fileIO.close();
    }

    static Object MyDeserialization(String fileName) throws Exception {
        // Init
        HashMap<String, String> fieldsValues = new HashMap<>(); // Храним: Имя переменной -> Значение

        // Deserialize
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        String className = reader.readLine(); // Читаем имя класса в первой строке
        String count = reader.readLine(); // Читаем количество переменных

        // Делим строку и записываем имя и значение переменной
        for (int i = 0; i < Integer.parseInt(count); ++i) {
            String[] field = reader.readLine().split("=");
            fieldsValues.put(field[0], field[1]);
        }
        reader.close();

        // Create object
        Class<?> clazz = Class.forName(className); // Получаем тип класа по имени
        Object obj = clazz.newInstance(); // Создаем экземпляр

        Field[] fields = clazz.getDeclaredFields(); // Получаем поля класса

        for (Field field : fields) {
            // Если поле имеет аннотацию и данные о нем хранятся в HashMap
            if (field.isAnnotationPresent(RxField.class) && fieldsValues.get(field.getName()) != null) {
                field.setAccessible(true); // Открываем доступ к данным поля
                if (field.getType() == Integer.class || field.getType() == int.class) {
                    field.set(obj, Integer.parseInt(fieldsValues.get(field.getName())));
                } else if (field.getType() == Double.class || field.getType() == double.class) {
                    field.set(obj,Double.parseDouble(fieldsValues.get(field.getName())));
                } else if (field.getType() == String.class) {
                    field.set(obj,fieldsValues.get(field.getName()));
                }
                field.setAccessible(false); // Закрываем доступ к данным поля
            }
        }

        return obj;
    }
}
