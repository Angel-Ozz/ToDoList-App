# 📌 ToDoList-App

A **full-stack** To-Do List application built with **React (Vite) + TypeScript + ShadCN** on the frontend and **Spring Boot (Maven)** on the backend.

---

## ⚡ Features

✅ Add, update, and delete tasks  
✅ Mark tasks as completed/uncompleted  
✅ Sort tasks by priority and due date  
✅ Filter tasks by name, priority, or status  
✅ Track **average completion time** of tasks  
✅ Responsive UI with **ShadCN** components  
✅ Unit and integration tests for frontend and backend  

---

## 🛠️ Tech Stack

### **Frontend**
- **React 18** + **Vite** (for fast builds)
- **TypeScript** (strict typing)
- **ShadCN** (UI components)
- **Axios** (for API requests)
- **React Router DOM** (for navigation)
- **TailwindCSS** (for styling)
- **Vitest** + **React Testing Library** (for unit & snapshot tests)

### **Backend**
- **Spring Boot** (with Maven)
- **Spring Boot Web Starter** (REST API)
- **Spring Validation** (for request validation)
- **JUnit** + **Mockito** (for backend tests)

---

## 🚀 Getting Started

### **1️⃣ Clone the Repository**
```sh
git clone https://github.com/Angel-Ozz/ToDoList-App.git
cd ToDoList-App
```

### **2️⃣ Backend Setup**
> Ensure you have **Java 21** and **Maven** installed.

```sh
cd backend
mvn spring-boot:run
```
The backend will start on **`http://localhost:8080`**.

---

### **3️⃣ Frontend Setup**
> Ensure you have **Node.js 18+** and **pnpm/npm/yarn** installed.

```sh
cd frontend
npm install
npm run dev
```
The frontend will start on **`http://localhost:5173/todos`**.

---

## 📂 Project Structure

```
ToDoList-App/
│── backend/        # Spring Boot backend
│   ├── src/main/   # Java source code
│   ├── src/test/   # Unit & integration tests
│   ├── pom.xml     # Maven dependencies
│── frontend/       # React frontend
│   ├── src/
│   │   ├── components/    # ShadCN UI components
│   │   ├── pages/         # React pages
│   │   ├── services/      # API calls with Axios
│   │   ├── types/         # TypeScript interfaces
│   │   ├── tests/         # Unit tests with Vitest
│   ├── vite.config.ts     # Vite configuration
│   ├── package.json       # Frontend dependencies
│── README.md       # Documentation
│── .gitignore      # Git ignore file
```

---

## 🧪 Testing

### **Frontend**
```sh
cd frontend
npm run test
```

### **Backend**
```sh
cd backend
mvn test
```

---

## 📌 API Endpoints

| Method | Endpoint             | Description               |
|--------|----------------------|---------------------------|
| GET    | `/tasks`             | Fetch all tasks          |
| POST   | `/tasks`             | Create a new task        |
| PUT    | `/tasks/{id}`        | Update a task            |
| DELETE | `/tasks/{id}`        | Delete a task            |
| PATCH  | `/tasks/{id}/done`   | Mark a task as done      |
| PATCH  | `/tasks/{id}/undone` | Mark a task as undone    |


---

### 👨‍💻 Made by [Angel-Ozz](https://github.com/Angel-Ozz)

---

