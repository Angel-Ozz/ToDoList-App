import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import TaskListPage from "./pages/taskListPage";

const App: React.FC = () => {
  return (
    <Router>
      <div className="flex-1 min-h-screen dark:bg-gray-800 dark:text-white pt-3">
        <header>
          <h1 className="text-5xl font-bold tracking-tight text-center py-4">
            To-Do App
          </h1>
        </header>
        <Routes>
          <Route path="/todos" element={<TaskListPage />} />
        </Routes>
      </div>
    </Router>
  );
};

export default App;
