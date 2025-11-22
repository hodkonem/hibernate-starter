package ru.hodkonem.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.hodkonem.converter.BirthdayConverter;
import ru.hodkonem.entity.User;

/**
 * Центральная фабрика для создания и управления единым экземпляром {@link SessionFactory}.
 * <p>
 * <strong>Назначение</strong>
 * <ul>
 *     <li>Инициализация {@link SessionFactory} на основании {@code hibernate.cfg.xml};</li>
 *     <li>Регистрация доменных сущностей и конвертеров;</li>
 *     <li>Применение параметров подключения из переменных окружения;</li>
 *     <li>Грациозное завершение работы ORM через метод {@link #shutdown()}.</li>
 * </ul>
 * <p>
 * <strong>Особенности реализации</strong>
 * <ul>
 *     <li>Паттерн «Lazy Holder» для ленивой, потокобезопасной инициализации;</li>
 *     <li>Отсутствие синхронизации на горячем пути {@link #getSessionFactory()};</li>
 *     <li>Явное логирование этапов инициализации и ошибок.</li>
 * </ul>
 */
public final class HibernateUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HibernateUtil.class);

    /**
     * Внутренний holder для ленивой инициализации {@link SessionFactory}.
     */
    private static class SessionFactoryHolder {
        private static final SessionFactory SESSION_FACTORY = buildSessionFactory();
    }

    private HibernateUtil() {
    }

    /**
     * Возвращает единый экземпляр {@link SessionFactory} для всего приложения.
     *
     * @return lazily инициализированный {@link SessionFactory}
     */
    public static SessionFactory getSessionFactory() {
        return SessionFactoryHolder.SESSION_FACTORY;
    }

    /**
     * Строит новый экземпляр {@link SessionFactory} на основании конфигурации Hibernate.
     * <p>
     * Метод не должен вызываться напрямую, используйте {@link #getSessionFactory()}.
     */
    private static SessionFactory buildSessionFactory() {
        LOG.info("Инициализация Hibernate SessionFactory...");
        try {
            var configuration = new Configuration();
            configuration.configure(); // hibernate.cfg.xml из classpath

            configuration.addAnnotatedClass(User.class);
            configuration.addAttributeConverter(BirthdayConverter.class, true);

            applyEnvVariable(configuration, "hibernate.connection.url", "DB_URL");
            applyEnvVariable(configuration, "hibernate.connection.username", "DB_USER");
            applyEnvVariable(configuration, "hibernate.connection.password", "DB_PASS");

            LOG.debug("Hibernate dialect: {}", configuration.getProperty("hibernate.dialect"));
            LOG.info("Подключение к БД: url={}", configuration.getProperty("hibernate.connection.url"));

            SessionFactory sessionFactory = configuration.buildSessionFactory();
            LOG.info("Hibernate SessionFactory успешно инициализирован.");

            return sessionFactory;
        } catch (Exception ex) {
            LOG.error("Ошибка инициализации Hibernate SessionFactory", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Закрывает {@link SessionFactory}, если он был создан, и освобождает ресурсы.
     * <p>
     * Рекомендуется вызывать в конце работы standalone-приложения.
     */
    public static void shutdown() {
        SessionFactory sessionFactory = SessionFactoryHolder.SESSION_FACTORY;
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            LOG.info("Остановка Hibernate SessionFactory...");
            sessionFactory.close();
            LOG.info("Hibernate SessionFactory остановлен.");
        }
    }

    /**
     * Читает значение переменной окружения и применяет его к конфигурации Hibernate.
     *
     * @param configuration     конфигурация Hibernate
     * @param hibernateProperty имя свойства Hibernate (например, {@code hibernate.connection.url})
     * @param envName           имя переменной окружения (например, {@code DB_URL})
     */
    private static void applyEnvVariable(Configuration configuration,
                                         String hibernateProperty,
                                         String envName) {
        String value = System.getenv(envName);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Переменная окружения " + envName + " не установлена.");
        }
        configuration.setProperty(hibernateProperty, value);
        LOG.debug("Применено значение из ENV {} для свойства {}", envName, hibernateProperty);
    }
}
