import axios from "axios";
import { Task } from "../types/Task";

const API_BASE_URL = "http://localhost:9090/todos";

// Fetch all tasks with optional filtering, sorting, and pagination
export const fetchTasks = async (
  page: number,
  sortBy?: string | string[],
  taskName?: string,
  priority?: string,
  completed?: boolean
): Promise<Task[]> => {
  const params = new URLSearchParams();
  params.append("page", page.toString());
  if (sortBy) {
    if (Array.isArray(sortBy)) {
      sortBy.forEach((sort) => params.append("sortBy", sort));
    } else {
      params.append("sortBy", sortBy);
    }
  }
  if (taskName) params.append("taskName", taskName);
  if (priority) params.append("priority", priority);
  if (completed !== undefined) params.append("completed", completed.toString());

  //console.log(`Fetching tasks with URL: ${API_BASE_URL}?${params.toString()}`);

  try {
    const response = await axios.get(`${API_BASE_URL}?${params.toString()}`);
    return response.data;
  } catch (error) {
    console.error("Error fetching tasks:", error);
    throw error;
  }
};

//Get avg time 4 all
export const fetchAverageCompletionTime = async (): Promise<number> => {
  try {
    const response = await axios.get(`${API_BASE_URL}/avg-done-time`);
    return response.data;
  } catch (error) {
    console.error("Error fetching average completion time:", error);
    throw error;
  }
};

//Get avg time per priority
export const fetchAverageCompletionTimePriority = async (): Promise<{
  HIGH: number;
  MEDIUM: number;
  LOW: number;
}> => {
  try {
    const response = await axios.get(
      `${API_BASE_URL}/avg-done-time-priorities`
    );
    return response.data;
  } catch (error) {
    console.error("Error fetching average completion time:", error);
    throw error;
  }
};

// Fetch a single task by ID
export const fetchTaskById = async (id: number): Promise<Task> => {
  try {
    const response = await axios.get(`${API_BASE_URL}/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching task with ID ${id}:`, error);
    throw error;
  }
};

// Create a new task
export const createTask = async (task: Partial<Task>): Promise<void> => {
  try {
    await axios.post(API_BASE_URL, task);
  } catch (error) {
    console.error("Error creating task:", error);
    throw error;
  }
};

// Mark a task as done
export const markTaskAsDone = async (id: number): Promise<void> => {
  try {
    await axios.patch(`${API_BASE_URL}/${id}/done`);
  } catch (error) {
    console.error(`Error marking task with ID ${id} as done:`, error);
    throw error;
  }
};

// Mark a task as undone
export const markTaskAsUnDone = async (id: number): Promise<void> => {
  try {
    await axios.patch(`${API_BASE_URL}/${id}/undone`);
  } catch (error) {
    console.error(`Error marking task with ID ${id} as undone:`, error);
    throw error;
  }
};

// Partially update a task
export const updateTask = async (
  id: number,
  updates: Partial<Task>
): Promise<void> => {
  try {
    await axios.patch(`${API_BASE_URL}/${id}`, updates);
  } catch (error) {
    console.error(`Error updating task with ID ${id}:`, error);
    throw error;
  }
};

// Delete a task
export const deleteTask = async (id: number): Promise<void> => {
  try {
    await axios.delete(`${API_BASE_URL}/${id}`);
  } catch (error) {
    console.error(`Error deleting task with ID ${id}:`, error);
    throw error;
  }
};
