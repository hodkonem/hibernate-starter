package ru.hodkonem;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hodkonem.entity.Birthday;
import ru.hodkonem.entity.Role;
import ru.hodkonem.entity.User;
import ru.hodkonem.util.HibernateUtil;

import java.time.LocalDate;

/**
 * Учебный bootstrap-класс для демонстрации работы Hibernate 7 без Spring.
 *
 * <p><strong>Сценарий выполнения:</strong></p>
 * <ul>
 *     <li>инициализация {@link SessionFactory} через {@link HibernateUtil};</li>
 *     <li>Создание и сохранение новой сущности {@link User};</li>
 *     <li>Чтение этой сущности из базы данных;</li>
 *     <li>Обновление данных и фиксация транзакции;</li>
 *     <li>Корректное закрытие фабрики;</li>
 * </ul>
 *
 * <p>Класс используется для локального запуска и проверки настроек Hibernate:
 * логирования, SQL-логов, slow query log и работы с конвертерами.</p>
 */
public class HibernateRunner {

    private static final Logger log = LoggerFactory.getLogger(HibernateRunner.class);

    public static void main(String[] args) {

        log.info("Запуск учебного стенда Hibernate...");

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

        try (Session session = sessionFactory.openSession()) {

            session.beginTransaction();

            User user = User.builder()
                    .username("mikhail1@gmail.com")
                    .firstname("Mikhail")
                    .lastname("Smith")
                    .info("""
                        {
                          "name": "Mikhail",
                          "id": 25
                        }
                        """)
                    .birthDate(new Birthday(LocalDate.of(2000, 11, 18)))
                    .role(Role.ADMIN)
                    .build();

            session.persist(user);
            log.info("Создан пользователь: {}", user);

            session.flush();
            session.clear();

            User loaded = session.find(User.class, user.getUsername());
            log.info("Загружен пользователь: {}", loaded);

            loaded.setLastname("Smith-Updated");
            User updated = session.merge(loaded);
            log.info("Пользователь обновлён: {}", updated);

            session.getTransaction().commit();
            log.info("Транзакция успешно зафиксирована.");

        } catch (Exception e) {
            log.error("Ошибка при работе HibernateRunner", e);
        } finally {
            HibernateUtil.shutdown();
        }

        log.info("Работа HibernateRunner завершена.");
    }
}
