# Share It

Веб-приложение, позволяющее делиться вещами между авторизированными пользователями, оставлять заявки, бронировать необходимое и оставлять комментарии.

## Что использовано

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Lombok](https://https://projectlombok.org/)
- [Junit](https://spring.io/projects/spring-security)
- [Hibernate](https://spring.io/projects/spring-data-jpa)
- [Docker](https://spring.io/projects/spring-data-jpa)
- [PostgreSQL](https://www.postgresql.org/)

## Схема БД, используемая в приложении:

![This is an image](https://i.postimg.cc/5NkXYpgD/Share-IT-page-0002.jpg)

## Что умеет приложение

- Добавление/взаимодействие с пользователями
- Управление вещами пользователя: создание, управление статусом, бронирование.
- Создание запросов для шеринга, комментирование закрытых броней.
- Запросы передаются по стандартному адресу localhost:8080/, данные представлены в формате JSON

## Примечания

- Реализация образов для Docker находится в ветке add-docker
- Также реализована микросервисная архитектура в ветке add-docker
