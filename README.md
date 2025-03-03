# ToDoList-App

A **To-Do List Application** built using **Spring Boot** for the backend and **React with TypeScript** for the frontend. This project allows users to manage their tasks efficiently with features like adding, updating, marking tasks as completed or undone, filtering, and viewing task statistics.

---

## Features

- **Task Management:** Create, update, delete, and filter tasks.
- **Task Completion Tracking:** Mark tasks as completed or undone.
- **Priority-Based Organization:** Tasks are categorized into high, medium, or low priority.
- **Statistics:** View average task completion times.
- **User-Friendly Interface:** Built using **React**, **TypeScript**, and **ShadCN components**.
- **State Management:** Uses **React Hooks**.
- **API Integration:** Communicates with a **Spring Boot** backend.

---

## Tech Stack

### Frontend (React + TypeScript)
- **Vite** (Development environment)
- **React Router DOM** (Routing)
- **Axios** (API calls)
- **ShadCN UI Components** (Pre-built UI elements)
- **Tailwind CSS** (Styling)
- **Jest & React Testing Library** (Testing)
- **Vitest** (Unit testing framework)

### Backend (Spring Boot)
- **Spring Boot** (REST API framework)
- **Spring Boot Web Starter** (For REST endpoints)
- **Spring Boot Validation** (Input validation)
- **Spring Boot DevTools** (Development tools)
- **Spring Data JPA** (Database management, if implemented)
- **H2 Database** (Currently available but not in use)
- **Maven** (Dependency management)

---

## Project Structure

### Frontend
```
frontend/
│── node_modules/
│── public/
│── src/
│   ├── assets/
│   ├── components/
│   ├── hooks/
│   ├── lib/
│   ├── pages/
│   ├── tasks/
│   ├── tests/
│   ├── types/
│   ├── App.css
│   ├── App.tsx
│   ├── index.css
│   ├── main.tsx
│   ├── vite-env.d.ts
│── .gitignore
│── components.json
│── eslint.config.js
│── index.html
│── package-lock.json
│── package.json
│── postcss.config.js
│── README.md
│── tailwind.config.js
│── tsconfig.app.json
│── tsconfig.json
│── tsconfig.node.json
│── vite.config.ts

```

### Backend
```
backend/
│── .mvn/wrapper/
│── src/
│   ├── main/
│   │   ├── java/com/toDoList/
│   │   │   ├── config/
│   │   │   ├── controllers/
│   │   │   ├── exceptions/
│   │   │   ├── models/
│   │   │   ├── services/
│   │   │   ├── TaskPriority.java
│   │   │   ├── ToDoListApplication.java
│   │   ├── resources/
│   ├── test/
│── target/
│── .gitignore
│── mvnw
│── mvnw.cmd
│── pom.xml

```

---

## Getting Started

### Prerequisites
Make sure you have the following installed:
- **Node.js** (v18 or higher)
- **npm** or **yarn**
- **Java 21**
- **Maven**

### Backend Setup
```sh
cd backend
mvn clean install
mvn spring-boot:run
```
The backend will start at `http://localhost:8080/todos`.

### Frontend Setup
```sh
cd frontend
npm install
npm run dev
```
The frontend will be available at `http://localhost:8080/todos`.

---

##  API Endpoints

### Task Management
| Method | Endpoint | Description |
|--------|---------|-------------|
| **GET** | `/todos` | Get all tasks with optional filters & pagination |
| **GET** | `/todos/{id}` | Get a task by ID |
| **POST** | `/todos` | Create a new task |
| **PATCH** | `/todos/{id}` | Update a task |
| **DELETE** | `/todos/{id}` | Delete a task |

### Task Status Updates
| Method | Endpoint | Description |
|--------|---------|-------------|
| **PATCH** | `/todos/{id}/done` | Mark a task as completed |
| **PATCH** | `/todos/{id}/undone` | Mark a task as not completed |

### Task Statistics
| Method | Endpoint | Description |
|--------|---------|-------------|
| **GET** | `/todos/avg-done-time` | Get the average completion time of tasks |
| **GET** | `/todos/avg-done-time-priorities` | Get the average completion time by priority |

---

## Testing
The project includes unit and integration tests for both frontend and backend.

### Run Frontend Tests
```sh
npm run test
```

### Run Backend Tests
```sh
mvn test
```

---


