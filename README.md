Hibernate Starter

Учебный сервис, предназначенный для изучения механизмов работы ORM-уровня на базе Hibernate ORM 7.x и взаимодействия с реляционными СУБД. Проект используется для отработки корпоративных подходов к реализации слоя доступа к данным и проверки технических решений в локальном окружении.

1. Назначение

Проект предназначен для:

изучения конфигурации и возможностей Hibernate ORM 7.x;

отработки принципов маппинга объектов в реляционные структуры;

выполнения базовых операций управления данными (CRUD);

анализа SQL, генерируемого ORM;

исследования моделей транзакций и поведения Session API.

Сервис не является продуктовой системой и используется исключительно для учебных и исследовательских задач.

2. Системные требования
   Компонент	Требование
   ОС	Windows / Linux / macOS
   Java	25
   PostgreSQL	15+
   Gradle	8.x
   RAM	от 512 МБ
   Сетевой доступ	Интернет для загрузки зависимостей
3. Технологический стек
   Технология	Версия
   Java	25
   Hibernate ORM	7.1.8.Final
   PostgreSQL JDBC	42.7.7
   Lombok	1.18.42
   Тестирование	JUnit 5
   Сборка	Gradle
4. Структура проекта
   src
   ├── main
   │    ├── java
   │    │    └── ru/...            # доменные модели, конфигурация ORM
   │    └── resources
   │         └── hibernate.cfg.xml
   └── test
   └── java                   # модульные тесты


Архитектура проекта соответствует внутренним стандартам:

domain — JPA-сущности;

repository / dao — операции доступа к данным;

config — конфигурация ORM и инфраструктуры.

5. Соглашения по разработке и код-стайлу

Используется форматирование IntelliJ IDEA либо Google Java Style Guide.

Пакеты формируются по шаблону ru.<company>.<module>.

Аннотации JPA размещаются над полями.

Сущности должны содержать:

первичный ключ (@Id);

стратегию генерации ключей (если применимо);

публичный конструктор без аргументов.

Для уменьшения шаблонного кода используется Lombok.

Управление транзакциями выполняется через Session API.

6. Политика логирования

Логирование предназначено для анализа SQL.

<property name="hibernate.show_sql">true</property>
<property name="hibernate.format_sql">true</property>


Рекомендации:

локальная разработка — DEBUG,

тестовые окружения — INFO,

продуктивные окружения — SQL-логирование отключено, если нет отдельного согласования.

7. Настройка подключения к БД

Параметры подключения не хранятся в репозитории и передаются через переменные окружения.
В hibernate.cfg.xml используются плейсхолдеры:

<property name="connection.url">${DB_URL}</property>
<property name="connection.username">${DB_USER}</property>
<property name="connection.password">${DB_PASS}</property>
<property name="connection.driver_class">org.postgresql.Driver</property>

Переменные окружения
Переменная	Значение
DB_URL	JDBC URL (например: jdbc:postgresql://localhost:5432/postgres)
DB_USER	имя пользователя БД
DB_PASS	пароль пользователя БД
Пример установки переменных окружения

Windows (PowerShell):

$env:DB_URL="jdbc:postgresql://localhost:5432/postgres"
$env:DB_USER="appuser"
$env:DB_PASS="postgres"


Linux/macOS (bash):

export DB_URL="jdbc:postgresql://localhost:5432/postgres"
export DB_USER="appuser"
export DB_PASS="postgres"

8. Сборка и запуск
   Сборка:
   ./gradlew build

Запуск:

Через IDE либо выполнением HibernateRunner.

9. Пример доменной модели
   @Entity
   @Table(name = "persons")
   @Data
   public class Person {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   private String name;
   }

10. План дальнейшего развития

добавление сущностей со связями (OneToMany, ManyToOne, ManyToMany);

введение уровня DAO/Repository;

использование HQL и Criteria API;

batch-операции;

кэш второго уровня;

профилирование SQL;

расширение модульных тестов (JUnit 5).

11. Ограничения

Проект предназначен только для локального использования.
Применение конфигураций и подходов осуществляется в учебных целях.