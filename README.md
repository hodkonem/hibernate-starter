# Hibernate Starter

Учебный стенд для изучения Hibernate ORM 7.x, конфигурации ORM-уровня, ведения SQL-логирования, работы с транзакциями и моделирования корпоративного слоя доступа к данным без Spring.  
Проект применяется для тренировки навыков backend-разработчика и проверки архитектурных решений в локальном окружении.

---

## 1. Назначение

Проект предназначен для:

- изучения конфигурации Hibernate ORM 7.x;
- отработки объектно-реляционного маппинга (ORM);
- выполнения CRUD-операций вручную через `Session API`;
- анализа SQL, генерируемого ORM (включая slow queries);
- демонстрации natural key вместо surrogate key;
- исследования транзакций, flush/clear, merge/persist;
- обучения корпоративным практикам логирования.

Проект используется исключительно в учебных целях.

---

## 2. Системные требования

| Компонент        | Требование                  |
|------------------|-----------------------------|
| ОС               | Windows / Linux / macOS     |
| Java             | 25                          |
| PostgreSQL       | 15+                         |
| Gradle           | 8.x                         |
| RAM              | от 512 MB                   |
| Интернет         | Загрузка зависимостей       |

---

## 3. Технологический стек

| Технология            | Версия                |
|-----------------------|------------------------|
| Java                  | 25                     |
| Hibernate ORM         | 7.1.8.Final            |
| PostgreSQL JDBC       | 42.7.7                 |
| Lombok                | 1.18.42                |
| Logback               | 1.5.x                  |
| Logstash Encoder      | 9.0                    |
| JUnit                 | 5                      |
| Сборка                | Gradle                 |

---

## 4. Структура проекта

```

src
├── main
│   ├── java
│   │    └── ru/hodkonem
│   │         ├── entity/        # доменные модели
│   │         ├── converter/     # конвертеры значений
│   │         └── util/          # HibernateUtil
│   └── resources
│        ├── hibernate.cfg.xml
│        └── logback.xml
└── test
└── java                     # модульные тесты

````

Корпоративные соглашения:

- `entity` — JPA-сущности;
- `converter` — Value Objects + AttributeConverter’ы;
- `util` — инфраструктурные классы;
- `HibernateRunner` — учебный bootstrap-класс.

---

## 5. Политика логирования

Логирование используется для:

- анализа SQL, генерируемого Hibernate;
- поиска медленных запросов;
- структурированного JSON-логирования;
- отладки flush → clear → merge сценариев.

### Основные лог-файлы

| Файл                 | Содержимое                                    |
|----------------------|------------------------------------------------|
| `logs/app.log`       | текстовый лог приложения                       |
| `logs/app-json.log`  | JSON-лог (совместим с ELK / Loki)              |
| `logs/sql.log`       | SQL Hibernate + slow queries                   |

### Настройки Hibernate

```xml
<property name="hibernate.show_sql">false</property>
<property name="hibernate.format_sql">true</property>

<!-- Логирование медленных запросов (>50 мс) -->
<property name="hibernate.session.events.log.LOG_QUERIES_SLOWER_THAN_MS">50</property>

<property name="hibernate.use_sql_comments">true</property>
<property name="hibernate.generate_statistics">true</property>
````

### Уровни логирования

* локальная разработка — `DEBUG`, SQL включён;
* тестовые среды — `INFO`, slow queries включены;
* прод — SQL включён точечно (по запросу).

---

## 6. Пример `logback.xml`

```xml
<configuration>

    <property name="LOG_PATTERN"
              value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"/>

    <!-- Цветная консоль -->
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <withJansi>true</withJansi>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %highlight(%-5level) %cyan(%logger{36}) - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- Обычный лог -->
    <appender name="APP_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/app.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/app-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>7</maxHistory>
        </rollingPolicy>
        <encoder><pattern>${LOG_PATTERN}</pattern></encoder>
    </appender>

    <!-- JSON-лог -->
    <appender name="APP_JSON" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/app-json.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/app-json-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>7</maxHistory>
        </rollingPolicy>
        <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder"/>
    </appender>

    <!-- SQL -->
    <appender name="SQL_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/sql.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/sql-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>7</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} %-5level %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="org.hibernate.SQL" level="DEBUG" additivity="false">
        <appender-ref ref="SQL_FILE"/>
    </logger>

    <logger name="org.hibernate.SQL_SLOW" level="INFO" additivity="false">
        <appender-ref ref="SQL_FILE"/>
        <appender-ref ref="APP_FILE"/>
    </logger>

    <root level="INFO">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="APP_FILE"/>
        <appender-ref ref="APP_JSON"/>
    </root>

</configuration>
```

---

## 7. Natural Key вместо Surrogate Key

Сущность `User` использует **натуральный ключ** (`username`), а не авто-ID:

```java
@Id
private String username;
```

Загрузка сущности:

```
session.find(User.class, username);
```

Преимущества:

* бизнес-смысл идентификатора сохраняется;
* улучшенная читаемость логов и аудита;
* упрощение тестов и обход автогенерации id.

---

## 8. Настройка подключения к БД

В `hibernate.cfg.xml` используются переменные окружения:

```xml
<property name="connection.url">${DB_URL}</property>
<property name="connection.username">${DB_USER}</property>
<property name="connection.password">${DB_PASS}</property>
```

### Пример установки переменных

PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/postgres"
$env:DB_USER="postgres"
$env:DB_PASS="postgres"
```

Bash:

```bash
export DB_URL="jdbc:postgresql://localhost:5432/postgres"
export DB_USER="postgres"
export DB_PASS="postgres"
```

---

## 9. Сборка и запуск

### Сборка

```bash
./gradlew build
```

### Запуск

Через IDE или запуском класса:

```
ru.hodkonem.HibernateRunner
```

---

## 10. Частые проблемы и их решения

### ❗ ClassNotFoundException: org.postgresql.Driver

Причина: отсутствует JDBC-драйвер.
Решение: убедитесь, что в зависимостях есть:

```gradle
runtimeOnly 'org.postgresql:postgresql:42.7.7'
```

---

### ❗ org.hibernate.MappingException: No Dialect mapping for JDBC type

Причина: используется JSONB, но нет `@JdbcTypeCode(SqlTypes.JSON)`.
Решение: проверьте поле:

```java
@JdbcTypeCode(SqlTypes.JSON)
private String info;
```

---

### ❗ Failed to read hibernate.cfg.xml

Причины:

* файл не в `src/main/resources`;
* опечатка в имени;
* неправильная структура XML.

Решение: перепроверьте расположение и корневой тег:

```
<hibernate-configuration>
```

---

### ❗ org.hibernate.HibernateException: Environment variable XXX is not set

Причина: отсутствует DB_URL / DB_USER / DB_PASS.
Решение: экспортируйте переменные (см. раздел выше).

---

## 11. Developer Onboarding

### 1. Установить окружение

* Java 25
* Gradle 8
* PostgreSQL 15
* IntelliJ IDEA (рекомендуется)

### 2. Склонировать репозиторий

```bash
git clone <URL>
cd hibernate-starter
```

### 3. Установить переменные окружения

(см. раздел 8)

### 4. Запустить базовый сценарий Hibernate

Открыть IDE → запустить `HibernateRunner`.

### 5. Проверить логи

После запуска должны создаться:

```
logs/app.log
logs/app-json.log
logs/sql.log
```

---

## 12. План развития

* сущности со связями (OneToMany, ManyToOne);
* HQL / JPQL / Criteria API;
* batch-операции;
* кэш второго уровня;
* модульные тесты;
* профилирование SQL и статистика Hibernate.

---

## 13. Ограничения

* Проект предназначен только для учебных целей.
* Не используется в продакшене.
* Конфигурации и подходы представлены для демонстрации ORM-механизмов.

---
