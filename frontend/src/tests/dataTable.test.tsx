import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { DataTable } from "../tasks/data-table";
import { ColumnDef } from "@tanstack/react-table";
import { vi } from "vitest";

// Mock data
const mockTasks = [
  { id: 1, taskName: "Task 1", taskPriority: "HIGH", completed: false },
  { id: 2, taskName: "Task 2", taskPriority: "LOW", completed: true },
  { id: 3, taskName: "Task 3", taskPriority: "MEDIUM", completed: false },
];

const mockColumns: ColumnDef<typeof mockTasks[0]>[] = [
  {
    accessorKey: "taskName",
    header: "Name",
    cell: ({ row }) => <div>{row.getValue("taskName")}</div>,
  },
  {
    accessorKey: "taskPriority",
    header: "Priority",
    cell: ({ row }) => <div>{row.getValue("taskPriority")}</div>,
  },
  {
    accessorKey: "completed",
    header: "Status",
    cell: ({ row }) => <div>{row.getValue("completed") ? "Completed" : "Not completed"}</div>,
  },
];

describe("DataTable Component", () => {
  test("renders DataTable with provided data", async () => {
    render(
      <DataTable columns={mockColumns} data={mockTasks} pageI={vi.fn()} onFilterChange={vi.fn()} />
    );
    
    expect(screen.getByText("Task 1")).toBeInTheDocument();
    expect(screen.getByText("Task 2")).toBeInTheDocument();
    expect(screen.getByText("Task 3")).toBeInTheDocument();
  });



  test("pagination buttons work correctly", async () => {
    const mockSetPage = vi.fn();
    render(
      <DataTable columns={mockColumns} data={mockTasks} pageI={mockSetPage} onFilterChange={vi.fn()} />
    );
    
    const nextButton = screen.getByText("Next");
    fireEvent.click(nextButton);
    expect(mockSetPage).toHaveBeenCalled();
  });
});
