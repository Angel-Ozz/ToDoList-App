import { ColumnDef } from "@tanstack/react-table";
import { Task } from "../types/Task";
import { Checkbox } from "../components/ui/checkbox";
import { markTaskAsDone, markTaskAsUnDone } from "../services/taskService";
import { useEffect, useState } from "react";
import { parseISO, format } from "date-fns";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "../components/ui/dialog";
import { UpdateForm } from "./update";
import { ArrowUpDown } from "lucide-react";
import { Button } from "../components/ui/button";

export const columns = (
  setSortBy: (sort: string[]) => void,
  sortBy: string[]
): ColumnDef<Task>[] => [
  {
    id: "select",
    accessorKey: "id",
    header: () => <div className="text-left ">Check</div>,
    cell: ({ row }) => {
      useEffect(() => {
        row.toggleSelected(row.getValue("completed"));
      }, [row]);

      const handleChange = async (value: boolean) => {
        try {
          let rowId = row.original.id;
          row.toggleSelected(!!value);
          if (value) {
            await markTaskAsDone(rowId);
          } else {
            await markTaskAsUnDone(rowId);
          }
        } catch (error) {
          console.error("Error handling checkbox change:", error);
        }
      };

      return (
        <Checkbox
          checked={row.getIsSelected()}
          onCheckedChange={async (value: boolean) => await handleChange(value)}
          aria-label="Select row"
        />
      );
    },

    enableSorting: false,
    enableHiding: false,
  },
  {
    accessorKey: "taskName",
    header: "Name",
    cell: ({ row }) => {
      return (
        <div className="flex items-center dark:text-white">
          {row.getValue("taskName")}
        </div>
      );
    },
  },
  {
    accessorKey: "taskPriority",
    header: ({}) => {
      return (
        <div className="text-left">
          <Button
            className="m-0 p-0 text-xl font-medium "
            variant="ghost"
            onClick={() => {
              if (sortBy.includes("priority")) {
                setSortBy(sortBy.filter((item) => item !== "priority"));
              } else {
                setSortBy([...sortBy, "priority"]);
              }
            }}
          >
            Priority
            <ArrowUpDown />
          </Button>
        </div>
      );
    },
  },
  {
    accessorKey: "completed",
    header: () => <div className="text-left ">Status</div>,
    cell: ({ row }) => {
      const [completed, setCompleted] = useState<boolean>(
        row.getValue("completed")
      );
      useEffect(() => {
        setCompleted(row.getIsSelected());
      }, [row.getIsSelected()]);
      return (
        <div className="text-left font-medium">
          {completed ? "Completed" : "Not completed"}
        </div>
      );
    },
  },
  {
    accessorKey: "taskDueDate",
    header: ({}) => {
      return (
        <div className="text-left">
          <Button
            className="m-0 p-0 text-xl font-medium "
            variant="ghost"
            onClick={() => {
              if (sortBy.includes("taskDueDate")) {
                setSortBy(sortBy.filter((item) => item !== "taskDueDate"));
              } else {
                setSortBy([...sortBy, "taskDueDate"]); //why 3 dots, react states immutable, so we need to copy the array and append the thing to then set it as the state again
              }
            }}
          >
            Due Date
            <ArrowUpDown />
          </Button>
        </div>
      );
    },
    cell: ({ row }) => {
      const dueDate: string | null = row.getValue("taskDueDate");

      const formattedDate: string = dueDate
        ? format(parseISO(dueDate), "MMM dd, yyyy")
        : "---";

      return <div className="text-left font-medium">{formattedDate}</div>;
    },
  },
  {
    accessorKey: "doneDate",
    header: () => <div className="text-left">Done Date</div>,
    cell: ({ row }) => {
      const [completed, setCompleted] = useState<boolean>(
        row.getValue("completed")
      );
      useEffect(() => {
        setCompleted(row.getIsSelected());
      }, [row.getIsSelected()]);
      let doneDate: Date | null = row.getValue("doneDate");

      if ((doneDate === null || doneDate == undefined) && completed) {
        doneDate = new Date();
      }

      const formattedDate: string =
        doneDate && completed
          ? new Intl.DateTimeFormat("en-US", {
              year: "numeric",
              month: "short",
              day: "2-digit",
            }).format(new Date(doneDate))
          : "---";

      return <div className="text-left font-medium">{formattedDate}</div>;
    },
  },
  {
    id: "actions",
    header: "",
    cell: ({ row }) => {
      const [isDialogOpen, setDialogOpen] = useState(false);
      const task = row.original;

      const closeDialog = () => {
        setDialogOpen(false);
        window.location.reload();
      };

      return (
        <Dialog open={isDialogOpen} onOpenChange={setDialogOpen}>
          <DialogTrigger
            className="text-2xl dark:text-white hover:underline"
            onClick={() => setDialogOpen(true)}
          >
            ...
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>{task.taskName}</DialogTitle>
              <DialogDescription>Update task</DialogDescription>
            </DialogHeader>
            <UpdateForm onClose={closeDialog} row={task} />
          </DialogContent>
        </Dialog>
      );
    },
    enableSorting: false,
    enableHiding: false,
  },
];
