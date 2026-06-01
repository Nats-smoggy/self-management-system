# Self Management System

This repository contains a full-stack self-management system with:

- `self-management/self-management`: Spring Boot backend
- `self-management-ui`: Vue 3 + Vite frontend
- `student.sql`: MySQL database initialization script

## Tech Stack

- Frontend: Vue 3, Vite, Element Plus, Axios, ECharts
- Backend: Spring Boot 3, MyBatis-Plus, MySQL

## Project Structure

```text
.
|-- self-management/
|   `-- self-management/    # backend project
|-- self-management-ui/     # frontend project
`-- student.sql             # database schema and seed data
```

## Getting Started

### 1. Initialize the database

Create a MySQL database and import `student.sql`.

Recommended database name:

```sql
student_management_db
```

### 2. Configure the backend

Edit the backend config file:

`self-management/self-management/src/main/resources/application.yml`

Set your local MySQL username and password:

```yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/student_management_db?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&allowPublicKeyRetrieval=true&useSSL=false
    username: your_mysql_username
    password: your_mysql_password
```

### 3. Start the backend

```bash
cd self-management/self-management
mvnw.cmd spring-boot:run
```

Backend default address:

```text
http://localhost:8080
```

### 4. Start the frontend

```bash
cd self-management-ui
npm install
npm run dev
```

Frontend default address:

```text
http://localhost:5173
```

## Notes

- The published repository does not include real database credentials.
- `node_modules` and backend `target` build outputs are ignored.
- Frontend API requests currently target `http://localhost:8080`.
