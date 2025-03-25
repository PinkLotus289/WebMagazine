
<h1>🎓 University Project – Web Service for Online Shop</h1>

<div class="section">
    <h2>📌 Описание проекта</h2>
    <p>Данный проект представляет собой <strong>RESTful веб-сервис</strong> для управления интернет-магазином, созданный в рамках учебной программы.<br>
        Реализован с использованием <strong>Java 17</strong>, <strong>Spring Boot</strong>, <strong>Maven</strong>, <strong>PostgreSQL</strong> и включает современные практики разработки: валидацию, логирование, асинхронность, кэширование, CI/CD и UI.</p>
</div>

<div class="section">
    <h2>🚀 Реализованный функционал</h2>

    <h3>🌐 REST API</h3>
    <ul>
        <li>GET с Query Parameters</li>
        <li>GET с Path Parameters</li>
        <li>POST-методы (включая bulk-операции)</li>
        <li>Поддержка формата JSON</li>
        <li>Swagger-документация</li>
    </ul>

    <h3>🛍 Работа с сущностями</h3>
    <ul>
        <li>Товары, пользователи, заказы, категории</li>
        <li>@OneToMany и @ManyToMany связи</li>
        <li>CRUD-операции</li>
        <li>Каскадные операции</li>
        <li>Ленивые и жадные загрузки</li>
    </ul>

    <h3>🧠 Кэш и запросы</h3>
    <ul>
        <li>In-memory кэш (HashMap Bean)</li>
        <li>Запросы с фильтрацией по вложенным сущностям (@Query, JPQL, Native Query)</li>
    </ul>

    <h3>🛡 Валидация и обработка ошибок</h3>
    <ul>
        <li>@ControllerAdvice</li>
        <li>Кастомные исключения</li>
        <li>Ошибки валидации (400 Bad Request)</li>
    </ul>

    <h3>🧾 Логирование</h3>
    <ul>
        <li>Aspect-ориентированное логирование действий и ошибок</li>
        <li>Генерация .log-файлов</li>
        <li>Эндпоинт для загрузки логов по дате</li>
    </ul>

    <h3>⏱ Асинхронность</h3>
    <ul>
        <li>Асинхронное создание логов</li>
        <li>Получение статуса задачи и файла по ID</li>
    </ul>

    <h3>👁 Счетчик посещений</h3>
    <ul>
        <li>Сервис учета URL-запросов</li>
        <li>Потокобезопасная реализация с synchronized</li>
        <li>Нагрузочное тестирование (JMeter/Postman)</li>
    </ul>

    <h3>🧪 Тестирование</h3>
    <ul>
        <li>Unit-тесты бизнес-логики</li>
        <li>Покрытие >80%</li>
    </ul>

    <h3>💻 UI-интерфейс</h3>
    <ul>
        <li>Создан на React (или Angular/Spring MVC)</li>
        <li>Просмотр и редактирование сущностей</li>
        <li>Используются UI-библиотеки (Material UI, Bootstrap и др.)</li>
    </ul>

    <h3>🐳 DevOps и деплой</h3>
    <ul>
        <li>Docker-контейнеризация</li>
        <li>Размещение на бесплатном хостинге (Render, Railway и др.)</li>
        <li>CI/CD через GitHub Actions</li>
        <li>Автосборка и деплой JAR-файла</li>
    </ul>
</div>

<div class="section">
    <h2>📂 Структура проекта</h2>
    <pre><code>unishop/
├── src/
│   ├── main/
│   │   ├── java/com/university/shop/
│   │   │   ├── controllers/       # REST-контроллеры
│   │   │   ├── services/          # Бизнес-логика
│   │   │   ├── models/            # Сущности: Product, Order, User, Category
│   │   │   ├── repositories/      # Интерфейсы JPA
│   │   │   ├── cache/             # In-memory кэш
│   │   │   ├── logging/           # Логика логирования и аспектов
│   │   │   ├── config/            # Swagger, Docker и пр.
│   │   │   └── ShopApplication.java  # Точка входа
│   ├── resources/
│   │   └── application.properties  # Конфигурация БД и пр.
├── test/                     # Unit-тесты
├── Dockerfile
├── docker-compose.yml
├── pom.xml                  # Maven зависимости
├── checkstyle.xml          # Стиль кода
└── README.md
</code></pre>
</div>

<div class="section">
    <h2>📦 Технологии и инструменты</h2>
    <ul>
        <li><strong>Java 17</strong>, <strong>Spring Boot 3.4.3</strong>, <strong>Maven</strong></li>
        <li><strong>PostgreSQL/MySQL</strong>, JPA, Hibernate</li>
        <li>Swagger, MapStruct, Lombok</li>
        <li>React / Angular / Spring MVC</li>
        <li>Docker, GitHub Actions, Render/Railway</li>
        <li>CheckStyle, JMeter, Postman</li>
        <li>JUnit, Mockito</li>
    </ul>
</div>

<div class="section">
    <h2>🏁 Как запустить проект</h2>
    <h3>Через Maven:</h3>
    <pre><code># Клонируйте репозиторий
git clone https://github.com/yourusername/unishop.git
cd unishop

# Соберите проект
./mvnw clean install

# Запустите приложение
./mvnw spring-boot:run</code></pre>

    <h3>Или через Docker:</h3>
    <pre><code>docker-compose up --build</code></pre>

    <div class="highlight">
        Swagger доступен по адресу: <strong>http://localhost:8080/swagger-ui/index.html</strong>
    </div>
</div>

</body>
</html>
