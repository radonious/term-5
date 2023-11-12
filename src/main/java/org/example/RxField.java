package org.example;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface RxField {
    String info(); // Поле доп информации

    String name(); // Код аннотации
}
