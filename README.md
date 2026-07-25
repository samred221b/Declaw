# Task Flow

A Spring Boot 3 task management application built with Java 21, PostgreSQL, JWT authentication, and OpenAPI/Swagger documentation.

## Features

- **JWT Authentication**: Secure REST API with token-based authentication using JSON Web Tokens (JWTS)
- **Layered Architecture**: Clean separation of concerns with Controller → Service → Repository layers
- **CRUD Operations**: Full Create, Read, Update, Delete operations for Projects, Tasks, and Comments
- **Search & Filtering**: Advanced search capabilities across resources with pagination support
- **OpenAPI/Swagger Documentation**: Interactive API documentation at `/swagger-ui.html`
- **Validation**: Comprehensive input validation using Spring Validation annotations
- **Lombok Integration**: Reduced boilerplate code with builders and data classes

## Technology Stack

### Core Frameworks & Libraries
- **Spring Boot 3.4.x** - Java framework for rapid development
- **Java 21** - Latest LTS JDK with enhanced features
- **Maven** - Build automation and dependency management

### Database & Persistence
- **PostgreSQL** - Production-grade relational database
- **Spring Data JPA** - Repository abstraction layer
- **Hibernate** - ORM implementation (via Spring Boot starter)

### Security & Authentication
- **JWT (JSON Web Tokens)** - Stateless authentication mechanism
- **Spring Security** - Framework for building secure applications
- **BCrypt Password Hashing** - Secure password storage with BCrypt encoder

### API Documentation
- **SpringDoc OpenAPI/Swagger** - Interactive API documentation and validation
- **Swagger UI** - Web-based interactive API explorer at `/swagger-ui.html`

## Project Structure
```
B/
├── pom.xml                           # Maven build configuration
├── src/main/
│   ├── java/com/taskflow/
│   │   ├── Application.java          # Spring Boot main entry point
│   │   ├── config/                   # Configuration classes
│   │   │   ├── DatabaseInitializer.java  # DB seeding on startup
│   │   │   ├── JwtConfig.java         # JWT token generation/validation
│   │   │   ├── SecurityConfig.java    # Spring Security filter chain
│   │   │   └── SwaggerConfig.java     # OpenAPI specification setup
│   │   ├── controller/               # REST API endpoints (Controllers)
│   │   │   ├── CommentController.java  # Comments CRUD endpoint
│   │   │   ├── GlobalIdController.java  # Polymorphic ID lookup
│   │   │   ├── ProjectController.java  # Projects CRUD endpoint
│   │   │   ├── TaskController.java     # Tasks CRUD endpoint
│   │   │   └── UserController.java    # User registration/profile endpoints
│   │   ├── dto/                      # Data Transfer Objects (DTOs)
│   │   │   ├── CommentDTO.java         # Comment creation/update DTO
│   │   │   ├── CommentResponseDTO.java  # Comment response representation
│   │   │   ├── ProjectDTO.java         # Project creation/update DTO
│   │   │   ├── ProjectResponseDTO.java  # Project response representation
│   │   │   ├── ResponseDTO.java        # Generic API response wrapper
│   │   │   ├── TaskDTO.java           # Task creation/update DTO
│   │   │   └── TaskResponseDTO.java    # Task response representation
│   │   ├── entity/                   # JPA entities (Database tables)
│   │   │   ├── Comment.java          # Comments table mapping
│   │   │   ├── GlobalId.java         # Polymorphic ID lookup table
│   │   │   ├── Project.java         # Projects table mapping
│   │   │   ├── Task.java            # Tasks table mapping
│   │   │   └── User.java           # Users/Authentication table
│   │   ├── repository/               # Spring Data repositories (SQL queries)
│   │   │   ├── CommentRepository.java  # Comments CRUD + filtering queries
│   │   │   ├── GlobalIdRepository.java  # Polymorphic ID lookups
│   │   │   ├── ProjectRepository.java  # Projects search/filtering queries
│   │   │   ├── TaskRepository.java    # Tasks search/filtering queries
│   │   │   └── UserRepository.java  # User authentication queries
│   │   ├── security/                 # JWT token handling
│   │   │   └── JwtTokenFilter.java   # Token extraction & validation filter
│   │   ├── service/                  # Business logic layer (Services)
│   │   │   ├── CommentService.java     # Comments business operations
│   │   │   ├── CustomUserDetailsService.java  # Spring Security user loader
│   │   │   ├── ProjectService.java    # Projects CRUD operations
│   │   │   └── TaskService.java       # Tasks CRUD operations
│   │   └── dto/                     # DTOs for API data transfer
│   ├── resources/
│   │   ├── application.properties  # Application configuration settings
│   │   └── application.yml        # Alternative YAML configuration
├── README.md                         # Project documentation (this file)
└── LICENSE                            # License information
```

## Database Schema

### Users Table
| Column | Type | Description |
|--------|------|-------------|
| id | BIGSERIAL PK | Primary key |
| username | VARCHAR(50) UNIQUE | Login identifier |
| email | VARCHAR(100) UNIQUE | User's email address |
| password_hash | TEXT | BCrypt hashed password |
| first_name | VARCHAR(50) | Display name (first) |
| last_name | VARCHAR(50) | Display name (last) |
| status | ENUM | Account state: ACTIVE, INACTIVE, SUSPENDED |
| created_at | TIMESTAMP | Registration timestamp |
| updated_at | TIMESTAMP | Last profile update |

### Projects Table
| Column | Type | Description |
|--------|------|-------------|
| id | BIGSERIAL PK | Primary key |
| name | VARCHAR(100) | Project display name |
| description | TEXT | Detailed project description |
| owner_id | BIGINT FK → users.id | Creator reference |
| status | ENUM | ACTIVE, ARCHIVED, DELETED |
| priority | ENUM | LOW, MEDIUM, HIGH, CRITICAL |
| created_at | TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | Last modification |

### Tasks Table
| Column | Type | Description |
|--------|------|-------------|
| id | BIGSERIAL PK | Primary key |
| title | VARCHAR(200) | Task name/title |
| description | TEXT | Detailed task information |
| status | ENUM | TODO, IN_PROGRESS, REVIEW, COMPLETED, BLOCKED |
| priority | ENUM | LOW, MEDIUM, HIGH, URGENT |
| due_date | TIMESTAMP | Expected completion date |
| assignee_id | BIGINT FK → users.id (nullable) | Assigned person reference |
| owner_id | BIGINT FK → users.id | Creator reference |
| project_id | BIGINT FK → projects.id | Parent project reference |
| tags | TEXT | Comma-separated categorization tags |
| created_at | TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | Last modification |

### Comments Table
| Column | Type | Description |
|--------|------|-------------|
| id | BIGSERIAL PK | Primary key |
| content | TEXT | Comment message body |
| user_id | BIGINT FK → users.id | Author reference |
| task_id | BIGINT FK → tasks.id | Related task reference |
| parent_id | BIGINT (nullable) | Nested comment parent ID |
| created_at | TIMESTAMP | Creation timestamp |
| updated_at | TIMESTAMP | Last modification (for edits) |

## API Endpoints Overview

### Authentication & Users
- `POST /api/users/register` - Register new user account
- `GET /api/users/byId/{id}` - Retrieve user by ID
- `GET /api/users/byUsername/{username}` - Find user by username
- `GET /api/users/byEmail/{email}` - Find user by email address
- `PUT /api/users/byId/{id}` - Update user profile information

### Projects
- `POST /api/projects` - Create new project with validation
- `GET /api/projects/byId/{id}` - Retrieve specific project details
- `GET /api/projects/search?q=...` - Search projects by name/description
- `GET /api/projects/byStatus?status=ACTIVE&pageSize=20` - Filter & paginate by status
- `GET /api/projects/byPriority?priority=HIGH&pageSize=20` - Filter & paginate by priority
- `PUT /api/projects/byId/{id}` - Update project information
- `DELETE /api/projects/byId/{id}` - Remove project from database

### Tasks
- `POST /api/tasks` - Create new task with validation and defaults
- `GET /api/tasks/byId/{id}` - Retrieve specific task details
- `GET /api/tasks/all?pageSize=20` - Paginated list of active tasks
- `GET /api/tasks/search?q=...` - Search tasks by title/description
- `GET /api/tasks/byProject?projectId=X&status=IN_PROGRESS&pageSize=20` - Filter by project + status
- `GET /api/tasks/byPriority?priority=HIGH&pageSize=20` - Paginated high-priority tasks
- `GET /api/tasks/byAssignee?assigneeId=X&pageSize=20` - Tasks assigned to specific user
- `GET /api/tasks/byDueDateRange?fromDate=X&toDate=Y` - Find tasks with due dates in range
- `GET /api/tasks/byDueDate?dueDate=YYYY-MM-DD&pageSize=20` - Tasks due on specific date
- `PUT /api/tasks/byId/{id}` - Update task information and workflow status
- `DELETE /api/tasks/byId/{id}` - Remove task from project

### Comments
- `POST /api/comments/tasks/{taskId}` - Add comment to a specific task
- `GET /api/comments/byId/{id}` - Retrieve specific comment details
- `GET /api/comments/search?q=...` - Search comments by content keywords
- `GET /api/comments/all?pageSize=20` - Paginated list of all active comments
- `GET /api/comments/byTask?taskId=X&pageSize=20` - Comments for a specific task
- `GET /api/comments/byUser?userId=X&pageSize=20` - Comments authored by a user
- `GET /api/comments/replies?parentId=X` - Nested reply comments under parent
- `PUT /api/comments/byId/{id}` - Edit/update existing comment
- `DELETE /api/comments/byId/{id}` - Remove single comment from task/thread

### Global IDs (Polymorphic Lookup)
- `GET /api/global-ids/byType/{typeId}/byValue/{idValue}` - Retrieve global ID by resource type and actual value (supports PROJECT/TASK types)

## Building & Running

### Prerequisites
- **Java 21+** - JDK 21 or later installed and configured in your PATH
- **Maven 3.9+** - Build tool for dependency resolution and compilation
- **PostgreSQL 14+** - Database server running locally (or specified host)

### Build Commands
```bash
cd B
mvn clean package -DskipTests    # Compile with tests disabled (faster build)
mvn clean install               # Full build including test execution
```

### Run Locally (Development Mode)
```bash
# Start PostgreSQL service first:
# macOS: brew services start postgresql@14
# Linux (Debian/Ubuntu): sudo systemctl start postgresql
# Windows: Use pg_ctlcluster or your preferred method to start PG 14+

# Run Spring Boot application:
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### Environment Variables (Optional)
```bash
# Override JWT configuration via environment variables:
export JWT_SECRET="YourSecretKey2024!"           # Token signing secret
export JWT_EXPIRATION=7200000                    # 2 hours in milliseconds
```

## Default User Credentials
For first-time access, use these temporary credentials (change immediately in production):
- **Username:** `admin`
- **Email:** `admin@taskflow.local`
- **Password:** `admin123!`

**Important:** After initial setup, update the default admin user's password via the `/api/users/byId/` endpoint and remove or disable the admin account.

## Security Best Practices

### JWT Configuration
The application uses BCrypt for password hashing (cost factor 10 by default) and generates JWT tokens with configurable expiration. In production, never use the default secret - configure it via environment variables as shown above.

### Database Initialization
The `DatabaseInitializer` creates a default admin user on first startup only. This ensures the application starts with zero data if deployed to an empty database (clean state).

### Input Validation
All DTOs include Spring Validation annotations (`@NotBlank`, `@Size`, `@Email`) ensuring consistent data quality at API boundaries.

## Pagination
All list endpoints support standard pagination via query parameters:
- `?page=0&pageSize=20` - Zero-indexed pages with configurable size (default: 20, max: 100)
- Sort fields are available for ordering results as needed

## License & Credits
This project demonstrates clean layered architecture patterns using Spring Boot 3 and JWT authentication. All code is provided as-is for educational purposes.

---
**Generated automatically by OpenClaw.** This response includes the complete source code, configuration files, and API documentation for your Maven Spring Boot project.