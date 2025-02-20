import React, { useEffect, useState } from "react";
import {
  fetchAverageCompletionTime,
  fetchAverageCompletionTimePriority,
  fetchTasks,
} from "../services/taskService";
import { Task } from "../types/Task";
import { DataTable } from "../tasks/data-table";
import { columns } from "../tasks/columns";
import { InputForm } from "../tasks/form";

import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "../components/ui/dialog";
import { Label } from "@radix-ui/react-label";
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
  CardFooter,
} from "../components/ui/card";

const TaskListPage: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [isDialogOpen, setDialogOpen] = useState(false);
  const [filters, setFilters] = useState<{
    taskName?: string;
    priority?: string;
    completed?: boolean;
  }>({});

  const [page, setPage] = useState(0);
  const [sortBy, setSortBy] = useState<string[]>([]);

  useEffect(() => {
    const loadTasks = async () => {
      try {
        const data = await fetchTasks(
          page,
          sortBy,
          filters.taskName,
          filters.priority,
          filters.completed
        );
        setTasks(data);
      } catch (err) {
        setError("Error fetching tasks");
        console.error(err);
      }
    };

    loadTasks();
  }, [isDialogOpen, page, filters, sortBy]);

  const closeDialog = () => setDialogOpen(false);

  if (error) {
    return <p>{error}</p>;
  }

  const AverageCompletionTime = () => {
    const [averageTime, setAverageTime] = useState<number | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
      const loadAverageTime = async () => {
        try {
          const avgTime = await fetchAverageCompletionTime();
          setAverageTime(avgTime);
        } catch (err) {
          setError("Error fetching average completion time");
        }
      };

      loadAverageTime();
    }, [averageTime, tasks]);

    if (error) {
      return <p>{error}</p>;
    }

    return <p>{averageTime} minutes</p>;
  };

  const AverageCompletionTimePerPriority = () => {
    const [averageTimes, setAverageTimes] = useState<
      | {
          HIGH: number;
          MEDIUM: number;
          LOW: number;
        }
      | {}
    >({});
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
      const loadAverageTimes = async () => {
        try {
          const data = await fetchAverageCompletionTimePriority();
          setAverageTimes(data);
        } catch (err) {
          setError("Error fetching average completion times by priority");
        }
      };

      loadAverageTimes();
    }, [averageTimes, tasks]);

    if (error) {
      return <p>{error}</p>;
    }

    if (Object.keys(averageTimes).length === 0) {
      return <p>0 minutes</p>;
    }

    return (
      <ul>
        {Object.entries(averageTimes).map(([priority, value]) => (
          <li key={priority}>
            <strong>{priority}:</strong> {value} minutes
          </li>
        ))}
      </ul>
    );
  };

  return (
    <div>
      <div className="container mx-auto py-15 items-center px-10 pb-20">
        <Dialog open={isDialogOpen} onOpenChange={setDialogOpen}>
          <DialogTrigger
            className="p-3 text-white bg-black dark:text-white dark:bg-gray-500 rounded-lg mb-2"
            onClick={() => setDialogOpen(true)}
          >
            Add New Task
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>New task</DialogTitle>
              <DialogDescription>Create a new task</DialogDescription>
            </DialogHeader>
            <InputForm onClose={closeDialog} />{" "}
          </DialogContent>
        </Dialog>

        <DataTable
          columns={columns(setSortBy, sortBy)}
          data={tasks}
          pageI={setPage}
          onFilterChange={setFilters}
        />
        <Label className="flex justify-center items-center">
          Page {page + 1}
        </Label>
      </div>
      <div className="flex justify-center space-x-10 p-4">
        <Card className="w-1/3 bg-white rounded-lg shadow-lg">
          <CardHeader>
            <CardTitle>Average completion time of tasks</CardTitle>
            <CardDescription></CardDescription>
          </CardHeader>
          <CardContent>
            <AverageCompletionTime />
          </CardContent>
          <CardFooter></CardFooter>
        </Card>
        <Card className="w-1/3 bg-white rounded-lg shadow-lg">
          <CardHeader>
            <CardTitle>Average completion time of tasks per priority</CardTitle>
            <CardDescription></CardDescription>
          </CardHeader>
          <CardContent>
            <AverageCompletionTimePerPriority />
          </CardContent>
          <CardFooter></CardFooter>
        </Card>
      </div>
    </div>
  );
};

export default TaskListPage;
