package ru.hodkonem.entity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public record Birthday(LocalDate birtDate) {

    public long getAge() {
        return ChronoUnit.YEARS.between(birtDate, LocalDate.now());
    }
}
