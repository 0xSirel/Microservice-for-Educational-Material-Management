# Microservice for Educational Material Management

## Overview

This university project is a Spring Boot microservice designed for managing educational materials. It allows teachers to upload, organize, and associate resources with courses, while students can access and download materials. The service supports files, folders, pages, with metadata such as title, description, author, and upload date.

## Features

- **Teachers:** Upload and associate materials with courses, create folders and pages, update metadata, and manage content.
- **Students:** Browse, view, and download educational materials.
- **Role-based access control:** Secured endpoints for teachers and public endpoints for students.
- **RESTful API:** Endpoints for CRUD operations on files, folders, and pages.
- **RabbitMQ integration:** For messaging and event-driven architecture.
- **MariaDB:** As the database backend.

## Technologies

- Spring Boot
- MariaDB
- RabbitMQ
- Swagger (for API documentation)
- Markdown (for page content)

## API Endpoints (Examples)

- `GET /api/v1/public/materials/get_by_course/{courseId}`: List materials for a course
- `GET /api/v1/public/materials/get_details/{id_material}`: Get details of a material
- `POST /api/v1/materials/upload_file/{courseId}`: Upload a new file (teacher only)
- `DELETE /api/v1/materials/file/{id_file}`: Delete a file (teacher only)
- `GET /api/v1/public/materials/file/download/{id_file}`: Download a file
- `POST /api/v1/materials/folder/course/{courseId}`: Create a new folder (teacher only)
- `POST /api/v1/materials/folder/add_file/{id_folder}`: Add a file to a folder (teacher only)
- `GET /api/v1/public/materials/folder/get_details/{id_folder}`: Get folder details
- `POST /api/v1/materials/page/course/{courseId}`: Create a new page (teacher only)
- `GET /api/v1/public/materials/page/get_by_course/{courseId}`: List pages for a course

## Getting Started

1. Clone the repository.
2. Copy `src/main/resources/application.properties.example` to `application.properties` and configure your database and RabbitMQ credentials.
3. Build the project:
   ```sh
   ./mvnw clean install
   ```
4. Run the application:
   ```sh
   ./mvnw spring-boot:run
   ```
5. Access the API via the documented endpoints.

