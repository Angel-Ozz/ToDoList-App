import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { toast } from "../hooks/use-toast";
import { Button } from "../components/ui/button";
import { format } from "date-fns";
import { parseISO } from "date-fns";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "../components/ui/form";
import { Input } from "../components/ui/input";
import { DatePickerDemo } from "./date-picker";
import { ComboboxDemo } from "./priority-selecter";
import { deleteTask, updateTask } from "../services/taskService";
import { Task } from "../types/Task";

const FormSchema = z.object({
  taskName: z.string().min(2, {
    message: "Task name must be between 2 and 120 characters",
  }),
  dueDate: z
    .date()
    .optional()
    .refine(
      (date) => {
        if (!date) return true;
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        return date >= today;
      },
      {
        message: "Due date can't be in the past",
      }
    ),
  priority: z.string().min(1, {
    message: "Select a priority",
  }),
});

interface UpdateFormProps {
  onClose: () => void;
  row: Task;
}

export function UpdateForm({ onClose, row }: UpdateFormProps) {
  const form = useForm<z.infer<typeof FormSchema>>({
    resolver: zodResolver(FormSchema),
    defaultValues: {
      taskName: row.taskName,
      dueDate: row.taskDueDate ? parseISO(row.taskDueDate) : undefined,
      priority: row.taskPriority,
    },
  });

  async function onSubmit(data: z.infer<typeof FormSchema>) {
    const jsonObject = {
      taskName: data.taskName,
      taskPriority: data.priority,
      taskDueDate: data.dueDate ? format(data.dueDate, "yyyy-MM-dd") : null,
      completed: false,
    };

    await updateTask(row.id, jsonObject);
    console.log(jsonObject);

    toast({
      title: "You submitted the following values:",
      description: (
        <pre className="mt-2 w-[340px] rounded-md bg-slate-950 p-4">
          <code className="text-white">
            {JSON.stringify(jsonObject, null, 2)}
          </code>
        </pre>
      ),
    });

    onClose();
    form.reset();
  }

  async function handleDelete() {
    const confirmed = window.confirm(
      "Are you sure you want to delete this task?"
    );

    if (confirmed) {
      await deleteTask(row.id);

      onClose();
    }
  }

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(onSubmit)} className="w-2/3 space-y-6">
        <FormField
          control={form.control}
          name="taskName"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Update</FormLabel>
              <FormControl>
                <Input placeholder="do a task" {...field} />
              </FormControl>
              <FormMessage>
                {form.formState.errors.taskName?.message}
              </FormMessage>
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="priority"
          render={({ field }) => (
            <FormItem className="flex flex-col">
              <FormLabel className="mb-1">Priority</FormLabel>
              <FormControl>
                <ComboboxDemo value={field.value} onChange={field.onChange} />
              </FormControl>
              <FormMessage>
                {form.formState.errors.priority?.message}
              </FormMessage>
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="dueDate"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Due Date</FormLabel>
              <FormControl>
                <DatePickerDemo
                  value={field.value}
                  onChange={field.onChange}
                  name={""}
                  onBlur={function (): void {
                    throw new Error("Function not implemented.");
                  }}
                />
              </FormControl>
              <FormDescription>Optional</FormDescription>

              <FormMessage>
                {form.formState.errors.dueDate?.message}
              </FormMessage>
            </FormItem>
          )}
        />

        <Button type="submit">Edit</Button>
        <Button type="button" className="ml-40 mr-0" onClick={handleDelete}>
          Delete
        </Button>
      </form>
    </Form>
  );
}
