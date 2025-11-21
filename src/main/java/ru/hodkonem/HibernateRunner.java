package ru.hodkonem;

import org.hibernate.cfg.Configuration;
import ru.hodkonem.entity.Birthday;
import ru.hodkonem.converter.BirthdayConverter;
import ru.hodkonem.entity.Role;
import ru.hodkonem.entity.User;

import java.sql.SQLException;
import java.time.LocalDate;

public class HibernateRunner {

    public static void main(String[] args) throws SQLException {

        var configuration = new Configuration();
        configuration.addAnnotatedClass(User.class);
        configuration.addAttributeConverter(new BirthdayConverter());
        configuration.configure();

        applyEnvVariable(configuration, "hibernate.connection.url", "DB_URL");
        applyEnvVariable(configuration, "hibernate.connection.username", "DB_USER");
        applyEnvVariable(configuration, "hibernate.connection.password", "DB_PASS");

        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {

            session.beginTransaction();

            var user = User.builder()
                    .username("mikhail1@gmail.com")
                    .firstname("Mikhail")
                    .lastname("Belov")
                    .birthDate(new Birthday(LocalDate.of(2000, 11, 18)))
                    .role(Role.ADMIN)
                    .build();

            session.persist(user);
            session.getTransaction().commit();
        }
    }

    private static void applyEnvVariable(Configuration configuration,
                                         String hibernateProperty,
                                         String envName) {

        String value = System.getenv(envName);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Переменная окружения " + envName + " не установлена."
            );
        }

        configuration.setProperty(hibernateProperty, value);
    }
}
