import * as React from "react";
import { format } from "date-fns";
import { Calendar as CalendarIcon } from "lucide-react";

import { cn } from "../lib/utils";
import { Button } from "../components/ui/button";
import { Calendar } from "../components/ui/calendar";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "../components/ui/popover";

interface DatePickerDemoProps {
  value?: Date;
  onChange: (date: Date | undefined) => void;
  onBlur: () => void;
  disabled?: boolean;
  name: string;
}

export const DatePickerDemo = React.forwardRef<
  HTMLButtonElement,
  DatePickerDemoProps
>(({ value, onChange }: DatePickerDemoProps, ref) => {
  return (
    <Popover>
      <PopoverTrigger asChild>
        <Button
          variant={"outline"}
          className={cn(
            "w-[280px] justify-start text-left font-normal",
            !value && "text-muted-foreground"
          )}
          ref={ref}
        >
          <CalendarIcon className="mr-2 h-4 w-4" />
          {value ? format(value, "PPP") : <span>Due Date</span>}
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-auto p-0">
        <Calendar
          mode="single"
          selected={value}
          onSelect={(date) => {
            if (date) {
              const normalizedDate = new Date(
                date.getFullYear(),
                date.getMonth(),
                date.getDate()
              );

              if (value && normalizedDate.getTime() === value.getTime()) {
                onChange(undefined);
              } else {
                onChange(normalizedDate);
              }
            } else {
              onChange(undefined);
            }
          }}
          initialFocus
        />
      </PopoverContent>
    </Popover>
  );
});
