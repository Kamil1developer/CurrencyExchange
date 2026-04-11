# Currency Exchange API

REST API для работы с валютами, курсами обмена и расчёта конвертации.

## О проекте

`Currency Exchange API` — учебный backend-проект на Java, реализованный с использованием `Servlet API`, `JDBC`, `SQLite` и `Tomcat`.

Приложение позволяет:

* получать список валют;
* получать валюту по коду;
* добавлять новую валюту;
* получать список обменных курсов;
* получать курс по валютной паре;
* добавлять новый курс;
* обновлять существующий курс;
* рассчитывать сумму обмена между валютами.

## Возможности

### Валюты

* `GET /currencies` — список всех валют;
* `GET /currency/{code}` — получение валюты по коду;
* `POST /currencies` — создание новой валюты.

### Курсы валют

* `GET /exchangeRates` — список всех курсов;
* `GET /exchangeRate/{pair}` — получение курса по паре валют;
* `POST /exchangeRates` — создание нового курса;
* `PATCH /exchangeRate/{pair}` — обновление курса.

### Конвертация

* `GET /exchange?from=USD&to=EUR&amount=10` — расчёт перевода суммы из одной валюты в другую.

## Технологический стек

* Java
* Maven
* Servlet API
* JDBC
* SQLite
* HikariCP
* Jackson
* Apache Tomcat

## Архитектура

Проект построен по многослойной архитектуре:

* **Servlet layer** — обработка HTTP-запросов и формирование HTTP-ответов;
* **Service layer** — бизнес-логика;
* **DAO layer** — работа с базой данных;
* **Entity / DTO / Mapper** — модели, DTO и преобразование данных;
* **Infrastructure layer** — инициализация `DataSource`, базы данных и контейнера зависимостей.

### Общая схема

`HTTP Request -> Servlet -> Service -> DAO -> SQLite -> JSON Response`

## Структура проекта

```text
src/
└── main/
    ├── java/
    │   └── org/kamilkhusainov/currency/
    │       ├── dao/
    │       ├── dto/
    │       ├── entity/
    │       ├── exceptions/
    │       ├── filter/
    │       ├── infrastructure/
    │       ├── mapper/
    │       ├── model/
    │       ├── service/
    │       ├── servlet/
    │       └── CurrencyConstants.java
    └── resources/
        └── config.properties
```

## Требования

Перед запуском убедись, что у тебя установлены:

* JDK 25
* Maven
* Apache Tomcat 9+ или Tomcat 10
* SQLite JDBC драйвер подтянется через Maven

## Перед запуском

### 1. Настрой путь к базе данных

В файле `src/main/resources/config.properties` укажи свой путь к SQLite базе:

```properties
database.url=jdbc:sqlite:/absolute/path/to/database.db
```

### 2. При необходимости поправь `deploy.sh`

Скрипт `deploy.sh` содержит абсолютные локальные пути до:

* Tomcat
* проекта
* Maven

Если запускаешь проект не на той же машине, измени их под себя.

## Сборка проекта

```bash
mvn clean package
```

После сборки будет создан WAR-файл:

```text
target/CurrencyExchange.war
```

## Локальный запуск

### Вариант 1. Ручной деплой в Tomcat

Скопируй WAR в директорию `webapps` Tomcat:

```bash
cp target/CurrencyExchange.war <TOMCAT_HOME>/webapps/
```

После запуска Tomcat приложение будет доступно по адресу:

```text
http://localhost:8080/CurrencyExchange/
```

### Вариант 2. Через `deploy.sh`

В репозитории есть скрипт деплоя, который:

* собирает проект;
* удаляет старый `ROOT.war`;
* копирует новый WAR в Tomcat как `ROOT.war`.

После такого деплоя приложение будет доступно по адресу:

```text
http://localhost:8080/
```

## Используемые хосты и порты

В процессе локальной разработки и деплоя использовались следующие хосты и порты.

### Backend

Backend-приложение запускается на Apache Tomcat.

Локальный адрес backend:

```text
http://localhost:8080
```

Типичный путь при деплое:

```text
http://localhost:8080/CurrencyExchange/
```

Если приложение развёрнуто как `ROOT.war`, то backend доступен по адресу:

```text
http://localhost:8080/
```

### Frontend

Frontend поднимался через `nginx`, обычно на порту `80`.

Локальный адрес frontend:

```text
http://localhost
```

или

```text
http://localhost:80
```

### Взаимодействие в разработке

Frontend и backend были разделены по портам:

* frontend: `localhost:80`
* backend: `localhost:8080`

Это означает, что frontend отправлял API-запросы на backend по порту `8080`.

### Удалённый сервер

Backend также разворачивался на удалённом сервере.

Хост сервера:

```text
142.132.231.235
```

Типичный адрес backend на сервере:

```text
http://142.132.231.235:8080/CurrencyExchange/
```

### Дополнительные порты

* `22` — SSH-доступ к серверу
* `5005` — порт удалённой Java-отладки

## Деплой на удалённый сервер

### 1. Подключение к серверу

```bash
ssh root@<SERVER_IP>
```

### 2. Переход в директорию проекта

```bash
cd /root/CurrencyExchange
```

### 3. Обновление репозитория

```bash
git pull
```

### 4. Сборка проекта

```bash
mvn clean package
```

### 5. Конвертация WAR из javax в jakarta

Tomcat 10 использует Jakarta Servlet API, поэтому собранный WAR-файл перед деплоем нужно конвертировать.

```bash
javax2jakarta /root/CurrencyExchange/target/CurrencyExchange.war /root/CurrencyExchange/target/CurrencyExchange-jakarta.war
```

### 6. Удаление предыдущего деплоя

```bash
rm -rf /var/lib/tomcat10/webapps/CurrencyExchange /var/lib/tomcat10/webapps/CurrencyExchange.war
```

### 7. Копирование нового WAR в Tomcat

```bash
cp /root/CurrencyExchange/target/CurrencyExchange-jakarta.war /var/lib/tomcat10/webapps/CurrencyExchange.war
```

### 8. Перезапуск Tomcat

```bash
systemctl restart tomcat10
```

После деплоя приложение будет доступно по адресу:

```text
http://<SERVER_IP>:8080/CurrencyExchange/
```

## Инициализация базы данных

При старте приложения:

* создаётся `DataSource` через `HikariCP`;
* автоматически создаются таблицы `Currencies` и `ExchangeRates`;
* выполняется начальная инициализация данных.

Инициализация запускается через `@WebListener` при старте веб-приложения.

## API

### 1. Получить список валют

**GET** `/currencies`

#### Пример ответа

```json
[
  {
    "id": 1,
    "code": "USD",
    "name": "United States dollar",
    "sign": "$"
  },
  {
    "id": 2,
    "code": "EUR",
    "name": "Euro",
    "sign": "€"
  }
]
```

### 2. Получить валюту по коду

**GET** `/currency/USD`

#### Пример ответа

```json
{
  "id": 1,
  "code": "USD",
  "name": "United States dollar",
  "sign": "$"
}
```

### 3. Создать валюту

**POST** `/currencies`

#### Content-Type

```text
application/x-www-form-urlencoded
```

#### Параметры формы

* `name`
* `code`
* `sign`

#### Пример запроса

```text
name=United States dollar&code=USD&sign=$
```

#### Пример ответа

```json
{
  "id": 1,
  "code": "USD",
  "name": "United States dollar",
  "sign": "$"
}
```

### 4. Получить список курсов

**GET** `/exchangeRates`

#### Пример ответа

```json
[
  {
    "id": 1,
    "baseCurrency": {
      "id": 1,
      "code": "USD",
      "name": "United States dollar",
      "sign": "$"
    },
    "targetCurrency": {
      "id": 2,
      "code": "EUR",
      "name": "Euro",
      "sign": "€"
    },
    "rate": 0.92
  }
]
```

### 5. Получить курс по валютной паре

**GET** `/exchangeRate/USDEUR`

#### Пример ответа

```json
{
  "id": 1,
  "baseCurrency": {
    "id": 1,
    "code": "USD",
    "name": "United States dollar",
    "sign": "$"
  },
  "targetCurrency": {
    "id": 2,
    "code": "EUR",
    "name": "Euro",
    "sign": "€"
  },
  "rate": 0.92
}
```

### 6. Создать курс

**POST** `/exchangeRates`

#### Content-Type

```text
application/x-www-form-urlencoded
```

#### Параметры формы

* `baseCurrencyCode`
* `targetCurrencyCode`
* `rate`

#### Пример запроса

```text
baseCurrencyCode=USD&targetCurrencyCode=EUR&rate=0.92
```

#### Пример ответа

```json
{
  "id": 1,
  "baseCurrency": {
    "id": 1,
    "code": "USD",
    "name": "United States dollar",
    "sign": "$"
  },
  "targetCurrency": {
    "id": 2,
    "code": "EUR",
    "name": "Euro",
    "sign": "€"
  },
  "rate": 0.92
}
```

### 7. Обновить курс

**PATCH** `/exchangeRate/USDEUR`

#### Тело запроса

```text
rate=0.95
```

#### Пример ответа

```json
{
  "id": 1,
  "baseCurrency": {
    "id": 1,
    "code": "USD",
    "name": "United States dollar",
    "sign": "$"
  },
  "targetCurrency": {
    "id": 2,
    "code": "EUR",
    "name": "Euro",
    "sign": "€"
  },
  "rate": 0.95
}
```

### 8. Выполнить конвертацию

**GET** `/exchange?from=USD&to=EUR&amount=10`

#### Пример ответа

```json
{
  "baseCurrency": {
    "id": 1,
    "code": "USD",
    "name": "United States dollar",
    "sign": "$"
  },
  "targetCurrency": {
    "id": 2,
    "code": "EUR",
    "name": "Euro",
    "sign": "€"
  },
  "rate": 0.92,
  "amount": 10,
  "convertedAmount": 9.20
}
```

## Логика конвертации

Сервис конвертации подде
