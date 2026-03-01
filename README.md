# Food Diary API

## Required links

- [SonarCloud analysis](https://sonarcloud.io/summary/overall?id=aavelinka_Food_Dairy&branch=main)
- [ER-Diagram](./docs/ER-Diagramm.png)
- [Postman collection](./docs/FoodDairyApiDoc.json)
- [Code listing](./docs/code_listing.txt)

## Overview

This project is a Java Spring Boot REST API for managing a food diary.

## Features

- CRUD operations for users, meals, products, notes, and water intake records
- Nutritional values are modeled as embedded value objects (`@Embeddable`) inside user/meal/product
- Search/filter endpoints for users (by name, sex, age)
- Search/filter endpoints for meals (by name, author, product list)
- Search/filter endpoints for products (by name, meal)
- Search/filter endpoints for notes (by date, meal)
- Search/filter endpoints for water intake (by user, by user+date)
- Daily water total endpoint (sum in ml by user and date)
- Update user body measurements
- Nutritional value calculation for a user
- Validation for incoming requests (`jakarta.validation`)
- Composite creation demo with and without transaction support (`with_transaction` / `without_transaction`)
- DTO mapping with MapStruct
- PostgreSQL integration (Spring Data JPA / Hibernate)
- Basic test setup with Testcontainers (PostgreSQL)

## API groups

- `Users`: `/api/users`
- `Meals`: `/api/meal`
- `Products`: `/api/products`
- `Notes`: `/api/note`
- `Water intake`: `/api/water-intakes`
