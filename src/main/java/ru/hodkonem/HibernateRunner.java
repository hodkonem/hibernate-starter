package ru.hodkonem;

import org.hibernate.cfg.Configuration;
import ru.hodkonem.entity.User;

import java.sql.SQLException;
import java.time.LocalDate;

public class HibernateRunner {
    static void main() throws SQLException {
        var configuration = new Configuration();
        configuration.addAnnotatedClass(User.class);
        configuration.configure();
        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {
            session.beginTransaction();

            var user = User.builder()
                    .username("mikhail@gmail.com")
                    .firstname("Mikhail")
                    .lastname("Belov")
                    .birthDate(LocalDate.of(2000,11,18))
                    .age(25)
                    .build();

            session.persist(user);
            session.getTransaction().commit();
        }
    }
}
