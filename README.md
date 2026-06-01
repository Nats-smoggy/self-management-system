# Self Management System

A full-stack self-management platform built with Spring Boot and Vue 3.

It combines personal planning, finance records, focus tracking, exam planning, course management, weight records, and user administration in one project.

## Preview

- Frontend: `Vue 3 + Vite + Element Plus + ECharts`
- Backend: `Spring Boot 3 + MyBatis-Plus + MySQL`
- Database script: `student.sql`

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
- MySQL
- Maven

## Quick Start

### 1. Prepare database

Create a MySQL database and import the SQL file:

```sql
student_management_db
```

Import:

```text
student.sql
```

### 2. Configure backend

Edit [application.yml](</D:/新建文件夹 (2)/弓/self-management/self-management/src/main/resources/application.yml:1>) and replace the database account info with your local values:

```yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/student_management_db?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&allowPublicKeyRetrieval=true&useSSL=false
    username: your_mysql_username
    password: your_mysql_password
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

## Repository

- GitHub: [Nats-smoggy/self-management-system](https://github.com/Nats-smoggy/self-management-system)
