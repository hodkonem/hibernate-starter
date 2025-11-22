package ru.hodkonem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Represents a system user stored in the {@code public.users} table.
 * <p>
 * Demonstrates several Hibernate ORM 7 features:
 * <ul>
 *     <li>mapping of an identifier using a natural key (email)</li>
 *     <li>mapping of a custom Value Object {@link Birthday}</li>
 *     <li>automatic conversion of JSONB fields via {@link JdbcTypeCode}</li>
 *     <li>enum mapping using {@link Enumerated}</li>
 * </ul>
 *
 * <p><strong>Fields:</strong></p>
 * <ul>
 *     <li>{@code username} – primary key, business identifier</li>
 *     <li>{@code firstname}, {@code lastname} – basic personal data</li>
 *     <li>{@code birthDate} – wrapped date value (domain-specific type)</li>
 *     <li>{@code info} – JSONB payload for flexible metadata storage</li>
 *     <li>{@code role} – user role mapped as a string enum</li>
 * </ul>
 *
 * <p>
 * This entity is part of the training project and demonstrates
 * clean domain modeling with Hibernate 7 without Spring.
 * </p>
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users", schema = "public")
public class User {
    @Id
    private String username;
    private String firstname;
    private String lastname;

    @Column(name = "birth_date")
    private Birthday birthDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String info;

    @Enumerated(EnumType.STRING)
    private Role role;
}
