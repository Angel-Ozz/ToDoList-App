import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import TaskListPage from "../pages/TaskListPage";
import {
  fetchTasks,
  fetchAverageCompletionTime,
  fetchAverageCompletionTimePriority,
} from "../services/taskService";
import { Task } from "../types/Task";
import { vi } from "vitest";
import { act } from "@testing-library/react";

// Mock API calls
vi.mock("../services/taskService", () => ({
  fetchTasks: vi.fn(),
  fetchAverageCompletionTime: vi.fn(),
  fetchAverageCompletionTimePriority: vi.fn(),
}));

describe("TaskListPage", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test("renders without crashing", async () => {
    (fetchTasks as jest.Mock).mockResolvedValue([]);
    await act(async () => {
      render(<TaskListPage />);
    });
    expect(screen.getByText("Add New Task")).toBeInTheDocument();
  });

  test("fetches and displays tasks", async () => {
    const mockTasks: Task[] = [
      { id: 1, taskName: "Test Task", taskPriority: "HIGH", completed: false },
    ];

    (fetchTasks as jest.Mock).mockResolvedValue(mockTasks);
    render(<TaskListPage />);
    await waitFor(() => {
      expect(screen.getByText("Test Task")).toBeInTheDocument();
    });
  });

  test("displays error when fetch fails", async () => {
    (fetchTasks as jest.Mock).mockRejectedValue(new Error("Error fetching tasks"));
    render(<TaskListPage />);
    await waitFor(() => {
      expect(screen.getByText("Error fetching tasks")).toBeInTheDocument();
    });
  });

  test("opens and closes the add task dialog", async () => {
    render(<TaskListPage />);
    const addTaskButton = screen.getByText("Add New Task");
    fireEvent.click(addTaskButton);
    expect(screen.getByText("New task")).toBeInTheDocument();
    fireEvent.click(screen.getByLabelText("Close"));
    await waitFor(() => {
      expect(screen.queryByText("New task")).not.toBeInTheDocument();
    });
  });

  test("updates completion time when a task is marked done", async () => {
    const mockTasks: Task[] = [
      { id: 1, taskName: "Test Task", taskPriority: "HIGH", completed: false },
    ];

    (fetchTasks as jest.Mock).mockResolvedValue(mockTasks);
    (fetchAverageCompletionTime as jest.Mock).mockResolvedValue(15);
    render(<TaskListPage />);
    await waitFor(() => {
      expect(screen.getByText("Test Task")).toBeInTheDocument();
    });

    await waitFor(() => {
      expect(screen.getByText("15 minutes")).toBeInTheDocument();
    });
  });


});
