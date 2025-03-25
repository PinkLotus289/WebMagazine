
<h1>🎓 Web Service for Online Shop</h1>

<div class="section">
    <h2>📌 Описание проекта</h2>
    <p>Данный проект представляет собой <strong>RESTful веб-сервис</strong> для управления интернет-магазином, созданный в рамках учебной программы.<br>
        Реализован с использованием <strong>Java 17</strong>, <strong>Spring Boot</strong>, <strong>Maven</strong>, <strong>PostgreSQL</strong> и включает современные практики разработки: валидацию, логирование, асинхронность, кэширование, CI/CD и UI.</p>
</div>

---

## 🚀 Функциональные возможности

### 🔗 REST API
- 🔹 **GET-запросы с Query Parameters**
- 🔹 **GET-запросы с Path Parameters**
- 🔹 **POST-методы**, включая **bulk-операции**
- 🔹 Возвращает данные в формате **JSON**
- 🔹 Документация через **Swagger**

### 🛍 Работа с сущностями
- 🔹 **Сущности:** товары, пользователи, заказы, категории
- 🔹 Связи **@OneToMany** и **@ManyToMany**
- 🔹 **CRUD-операции**
- 🔹 **Каскадные операции**
- 🔹 **Ленивые и жадные загрузки**

### 🧠 Кэш и запросы
- 🔹 **In-memory кэш** (в виде HashMap Bean)
- 🔹 **Фильтрация по вложенным сущностям** через @Query, JPQL и Native Query

### 🛡 Валидация и обработка ошибок
- 🔹 Обработка ошибок через **@ControllerAdvice**
- 🔹 **Кастомные исключения**
- 🔹 **Ошибки валидации (400 Bad Request)**

### 🧾 Логирование
- 🔹 **Aspect-ориентированное логирование** действий и ошибок
- 🔹 **Генерация лог-файлов**
- 🔹 **Эндпоинт для загрузки логов по дате**

### ⏱ Асинхронность
- 🔹 **Асинхронное создание логов**
- 🔹 **Запросы получения статуса задачи и файла по ID**

### 👁 Счетчик посещений
- 🔹 **Учёт URL-запросов**
- 🔹 **Потокобезопасная реализация** с synchronized
- 🔹 **Нагрузочное тестирование** через JMeter и Postman

### 🧪 Тестирование
- 🔹 **Unit-тесты бизнес-логики**
- 🔹 **Покрытие тестами более 80%**

### 💻 UI-интерфейс
- 🔹 Интерфейс на **React / Angular / Spring MVC**
- 🔹 **Просмотр и редактирование сущностей**
- 🔹 Использование **UI-библиотек** (Material UI, Bootstrap и др.)

### 🐳 DevOps и деплой
- 🔹 **Docker-контейнеризация**
- 🔹 **Размещение на бесплатных хостингах** (Render, Railway и др.)
- 🔹 **CI/CD через GitHub Actions**
- 🔹 **Автоматическая сборка и деплой JAR-файла**

---


<div class="section">
    <h2>📂 Структура проекта</h2>
    <pre><code>restservice/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/restservice/
│   │   │       ├── controller/       # REST-контроллеры
│   │   │       ├── dto/              # Классы передачи данных
│   │   │       ├── model/            # Сущности (Entity)
│   │   │       ├── repository/       # Интерфейсы JPA
│   │   │       ├── service/          # Бизнес-логика
│   │   │       └── RestserviceApplication.java  # Главный класс приложения
│   ├── resources/
│   │   └── application.properties    # Настройки приложения
├── test/                             # Unit-тесты
├── target/                           # Скомпилированные файлы (build output)
├── pom.xml                           # Maven зависимости и конфигурация
├── .gitignore                        # Исключения для Git
├── .gitattributes
├── mvnw                              # Maven wrapper (Unix)
├── mvnw.cmd                          # Maven wrapper (Windows)
└── HELP.md                           # Вспомогательная документация
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

</div>

</body>
</html>
