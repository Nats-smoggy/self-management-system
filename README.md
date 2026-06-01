# Self Management System

A full-stack self-management platform built with Spring Boot, PostgreSQL, and Vue 3.

It combines personal planning, finance records, focus tracking, exam planning, course management, weight records, and user administration in one project.

## Preview

- Frontend: `Vue 3 + Vite + Element Plus + ECharts`
- Backend: `Spring Boot 3 + MyBatis-Plus + PostgreSQL`
- Database scripts: `student.sql` and `student_postgres.sql`

## Main Features

- User login and registration
- Personal dashboard and profile center
- Daily plan management
- Finance / bookkeeping records
- Focus session tracking
- Course schedule management
- Exam plan management
- Weight record management
- Admin user management

## Project Structure

```text
.
|-- self-management/
|   `-- self-management/            # Spring Boot backend
|-- self-management-ui/             # Vue 3 frontend
`-- student.sql                     # MySQL schema and sample data
```

## Tech Stack

### Frontend

- Vue 3
- Vite
- Vue Router
- Element Plus
- Axios
- ECharts

### Backend

- Spring Boot 3
- MyBatis-Plus
- PostgreSQL
- Maven

## Quick Start

### 1. Prepare database

Create a PostgreSQL database:

```sql
CREATE DATABASE student_management_db;
```

Import the PostgreSQL script:

```bash
psql -U postgres -d student_management_db -f student_postgres.sql
```

### 2. Configure backend

Edit [application.yml](</D:/新建文件夹 (2)/弓/self-management/self-management/src/main/resources/application.yml:1>) and replace the database account info with your local values:

```yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/student_management_db
    username: postgres
    password: your_postgres_password
```

### 3. Run backend

```bash
cd self-management/self-management
mvnw.cmd spring-boot:run
```

Backend URL:

```text
http://localhost:8080
```

Backend environment variables for cloud deployment:

```text
PORT
DB_URL
DB_USERNAME
DB_PASSWORD
```

### 4. Run frontend

```bash
cd self-management-ui
npm install
npm run dev
```

Frontend URL:

```text
http://localhost:5173
```

## API Notes

- The frontend currently calls the backend at `http://localhost:8080`
- Cross-origin access is enabled in the backend controllers

## Publish Notes

- Real database credentials have been removed from the public repository
- Build outputs such as `node_modules` and `target` are ignored
- Backend now supports container deployment through `self-management/self-management/Dockerfile`
- The project has been migrated to PostgreSQL for free-hosting compatibility
- Use `student_postgres.sql` for PostgreSQL environments

## Current Release Mode

This project is currently released in a half-published mode:

- Public frontend page is available online for preview
- Backend runs locally on your own computer
- Database runs locally on your own MySQL service

Online preview:

- Frontend: [https://nats-smoggy.github.io/self-management-system/](https://nats-smoggy.github.io/self-management-system/)

To use the full system with login, CRUD, and charts:

1. Start your local MySQL service
2. Import `student_postgres.sql` into `student_management_db`
3. Start the backend on `http://localhost:8080`
4. Open the frontend locally with `npm run dev`

Important:

- The published GitHub Pages frontend is for public preview
- Full data operations depend on your local backend and local database
- If you want a fully online system later, you will need a paid or externally hosted backend/database

## Free Hosting Path

To move this project toward a fully free online deployment:

1. Deploy the backend as a free web service
2. Use a free PostgreSQL instance
3. Set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` on the backend host
4. Set `VITE_API_BASE_URL` on the frontend host to your deployed backend URL

### Render Setup

Backend web service:

- Root directory: `self-management/self-management`
- Build command: `./mvnw -DskipTests package`
- Start command: `java -jar target/self-management-0.0.1-SNAPSHOT.jar`

Frontend static site:

- Root directory: `self-management-ui`
- Build command: `npm install && npm run build`
- Publish directory: `dist`
- Environment variable: `VITE_API_BASE_URL=<your-backend-url>`

Database:

- Create a free PostgreSQL database on Render
- Set backend `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` from that Render database
- The backend will auto-run `schema.sql` and `data.sql` on startup

## Repository

- GitHub: [Nats-smoggy/self-management-system](https://github.com/Nats-smoggy/self-management-system)
