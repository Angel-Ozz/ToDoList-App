export type TaskPriority = "HIGH" | "MEDIUM" | "LOW";

export type Task = {
  id: number;
  taskName: string;
  taskPriority: TaskPriority | string;
  creationDate: string;
  completed: boolean;
  taskDueDate: string | null;
  doneDate: string | null;
};
