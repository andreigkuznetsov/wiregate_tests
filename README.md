# Wiregate Autotests

## Описание

Этот репозиторий содержит автотесты для регистрации пользователя в проекте Wiregate. Покрытие реализовано на трех уровнях:

- **UI-тесты:** эмулируют действия пользователя через браузер (Selenide + JUnit 5), проверяют фронтенд-валидацию и сценарии успешной регистрации.
- **API-тесты:** напрямую обращаются к backend (Rest Assured + JUnit 5), тестируют регистрацию, граничные значения, ошибки.
- **DB-проверки:** автоматическая валидация данных в PostgreSQL после действий через UI и API (java.sql).

## Используемые технологии и библиотеки

- **Java 17+**
- **Gradle** (в качестве сборщика)
- **JUnit 5** — unit и интеграционное тестирование
- **Selenide** — browser automation для UI
- **Rest Assured** — тестирование REST API
- **PostgreSQL JDBC** — прямое подключение к БД
- **SLF4J** — логирование (по умолчанию — NOP)

> Всё, что требуется для работы, уже указано в `build.gradle`.

## Структура проекта

```
wiregate_tests/
├── build.gradle
└── src
└── test
├── java
│ ├── api
│ │ └── RegistrationApiTest.java
│ ├── pages
│ │ └── RegistrationPage.java
│ ├── ui
│ │ └── RegistrationUiTest.java
│ └── util
│ └── DatabaseHelper.java
└── resources
```

- `api/` — API-тесты (Rest Assured)
- `ui/` — UI-тесты (Selenide)
- `pages/` — Page Object для страницы регистрации
- `util/` — Хелперы для работы с БД и прочее

## Как настроить и запустить тесты

### 1. Предварительные требования

- **Java 17 или выше** (проверьте: `java -version`)
- **Gradle** (`gradle -v`) или используйте wrapper: `./gradlew`
- Запущены сервисы Wiregate:
  - Фронт: [http://localhost:3000](http://localhost:3000)
  - Backend: [http://localhost:8000](http://localhost:8000)
  - PostgreSQL: на `localhost:5432` (user: `postgres`, password: `secret`, database: `postgres`)
- **Chromedriver** совместимой версии с Chrome (обычно Selenide скачает автоматически)

### 2. Настройка

- При необходимости измените настройки подключения к БД в `DatabaseHelper.java`:
    ```java
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "<username>";
    private static final String PASSWORD = "<password>";
    ```
- Фронт и бэк должны быть доступны на указанных выше адресах/портах.

### 3. Запуск тестов

#### Через Gradle:

- Все тесты:
    ```bash
    ./gradlew test
    ```
- Только UI:
    ```bash
    ./gradlew test --tests "ui.*"
    ```
- Только API:
    ```bash
    ./gradlew test --tests "api.*"
    ```

#### Через IDE (IntelliJ IDEA / Giga IDE):

- Откройте проект как Gradle-проект.
- Убедитесь, что `src/test/java` помечена как **Test Sources Root**.
- Запустите интересующие тесты через меню JUnit.

#### Дополнительно

- **Параллельный запуск** тестов можно настроить через JUnit Platform, если потребуется.
- В логах будет видно, какие тесты прошли/упали, а также результаты проверок в базе данных.
- После каждого теста созданный тестовый пользователь удаляется из БД.

### 4. Частые вопросы

- **Что делать, если тесты не видят сервисы?**
  - Проверьте, что фронт, backend и БД запущены в Docker и доступны на нужных портах.
- **Не совпадают данные в БД?**
  - Актуализируйте структуру БД/миграции согласно последнему коду backend.

### 5. Как добавить свои тесты

- Клонируйте любой из существующих тестов в папке `api` или `ui` и изменяйте сценарии.
- Для новых API-эндпоинтов используйте Rest Assured по аналогии с `RegistrationApiTest`.
- Для новых UI-страниц — создайте PageObject в `pages/`.
