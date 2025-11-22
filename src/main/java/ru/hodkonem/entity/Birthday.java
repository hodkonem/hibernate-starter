package ru.hodkonem.entity;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Value Object representing a user's birth date.
 * <p>
 * Encapsulates {@link LocalDate} to provide stronger domain semantics
 * and to avoid leaking primitive date types into the entity layer.
 * <p>
 * Provides a utility method {@link #getAge()} for calculating
 * the user's age in full years as of the current date.
 */
public record Birthday(LocalDate birtDate) {

    /**
     * Calculates the age in full years from the birth date until today.
     *
     * @return number of full years between {@code birtDate} and {@code LocalDate.now()}
     */
    public long getAge() {
        return ChronoUnit.YEARS.between(birtDate, LocalDate.now());
    }
}

