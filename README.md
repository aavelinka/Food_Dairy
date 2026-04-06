# Food Diary API

## Required links

- [SonarCloud analysis](https://sonarcloud.io/summary/overall?id=aavelinka_Food_Dairy&branch=main)
- [ER-Diagram](./docs/ER-Diagramm.png)

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

## Local run with Docker Compose

1. Copy `.env.example` to `.env`
2. Fill in database credentials if needed
3. Run:

```bash
docker compose up --build
```

Services:

- Frontend: `http://localhost:4173`
- Backend API: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Healthcheck: `http://localhost:8080/actuator/health`

## Environment variables

Backend:

- `PORT`
- `DB_HOST`
- `DB_PORT`
- `DB_NAME`
- `DB_USER`
- `DB_PASSWORD`
- `APP_CORS_ALLOWED_ORIGINS`

Frontend:

- `VITE_API_BASE_URL`

## GitHub CI/CD

The repository includes a GitHub Actions workflow named `CI/CD` with the following jobs:

- `Backend Build and Test`
- `Frontend Build`
- `Deploy to Railway`
- `Post-deploy Healthcheck`

To make the deployment and healthcheck jobs work, configure these GitHub repository settings.

Secrets:

- `RAILWAY_TOKEN`

Variables:

- `RAILWAY_PROJECT_ID`
- `RAILWAY_ENVIRONMENT_NAME`
- `RAILWAY_BACKEND_SERVICE`
- `RAILWAY_FRONTEND_SERVICE`
- `BACKEND_HEALTHCHECK_URL`
- `FRONTEND_HEALTHCHECK_URL`

Example values:

- `RAILWAY_ENVIRONMENT_NAME=production`
- `RAILWAY_BACKEND_SERVICE=Food_Dairy`
- `RAILWAY_FRONTEND_SERVICE=frontend`
- `BACKEND_HEALTHCHECK_URL=https://fooddairy-production.up.railway.app/actuator/health`
- `FRONTEND_HEALTHCHECK_URL=https://frontend-production-d674.up.railway.app/`
