import {
  ColumnDef,
  flexRender,
  getCoreRowModel,
  useReactTable,
  getPaginationRowModel,
  PaginationState,
  SortingState,
  getSortedRowModel,
  ColumnFiltersState,
  getFilteredRowModel,
} from "@tanstack/react-table";

import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../components/ui/table";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Dispatch, SetStateAction, useEffect, useState } from "react";
import { ComboboxPriority } from "./priority-Combobox";
import { CompletedCombobox } from "./status-Cbox";

interface DataTableProps<TData, TValue> {
  columns: ColumnDef<TData, TValue>[];
  data: TData[];
  pageI: Dispatch<SetStateAction<number>>;
  onFilterChange: (filters: {
    taskName?: string;
    priority?: string;
    completed?: boolean;
  }) => void;
}

export function DataTable<TData, TValue>({
  columns,
  data,
  onFilterChange,
  pageI,
}: DataTableProps<TData, TValue>) {
  const [columnFilters, setColumnFilters] = useState<ColumnFiltersState>([]);
  const [sorting, setSorting] = useState<SortingState>([]);
  const [filterValueName, setFilterValueName] = useState<string>("");
  const [filterValuePriority, setFilterValuePriority] = useState<
    string | undefined
  >("");
  const [filterValueCompleted, setFilterValueCompleted] = useState<
    boolean | undefined
  >(undefined);
  const [pagination, setPagination] = useState<PaginationState>({
    pageIndex: 0,
    pageSize: 9,
  });

  useEffect(() => {
    pageI(pagination.pageIndex);
  }, [pagination]);

  useEffect(() => {
    onFilterChange({
      taskName: filterValueName,
      priority: filterValuePriority,
      completed: filterValueCompleted,
    });
  }, [filterValueName, filterValuePriority, filterValueCompleted]);

  const table = useReactTable({
    data,
    columns,
    onSortingChange: setSorting,
    getSortedRowModel: getSortedRowModel(),
    getCoreRowModel: getCoreRowModel(),
    getPaginationRowModel: getPaginationRowModel(),
    state: {
      pagination,
      sorting,
      columnFilters,
    },
    manualPagination: true,

    onPaginationChange: setPagination,
    onColumnFiltersChange: setColumnFilters,
    getFilteredRowModel: getFilteredRowModel(),
  });

  return (
    <div>
      <div className="flex items-center justify-between py-4">
        <Input
          placeholder="Filter by task name..."
          value={filterValueName}
          onChange={(event) => setFilterValueName(event.target.value)}
          className="max-w-sm"
        />
        <ComboboxPriority
          value={filterValuePriority ? filterValuePriority : ""}
          onChange={(value) => setFilterValuePriority(value)}
        />
        <CompletedCombobox
          value={filterValueCompleted?.toString()}
          onChange={(value) => {
            setFilterValueCompleted(
              value == "true" && filterValueCompleted?.toString() != value
                ? true
                : value == "false" && filterValueCompleted?.toString() != value
                ? false
                : undefined
            );
          }}
        />
      </div>

      <div className="rounded-md border">
        <Table>
          <TableHeader>
            {table.getHeaderGroups().map((headerGroup) => (
              <TableRow key={headerGroup.id}>
                {headerGroup.headers.map((header) => {
                  return (
                    <TableHead key={header.id}>
                      {header.isPlaceholder
                        ? null
                        : flexRender(
                            header.column.columnDef.header,
                            header.getContext()
                          )}
                    </TableHead>
                  );
                })}
              </TableRow>
            ))}
          </TableHeader>
          <TableBody>
            {table.getRowModel().rows?.length ? (
              table.getRowModel().rows.map((row) => (
                <TableRow
                  key={row.id}
                  data-state={row.getIsSelected() && "selected"}
                >
                  {row.getVisibleCells().map((cell) => (
                    <TableCell key={cell.id}>
                      {flexRender(
                        cell.column.columnDef.cell,
                        cell.getContext()
                      )}
                    </TableCell>
                  ))}
                </TableRow>
              ))
            ) : (
              <TableRow>
                <TableCell
                  colSpan={columns.length}
                  className="h-24 text-center"
                >
                  No results.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
      <div className="flex items-center justify-end space-x-2 py-4 dark:text-gray-800">
        <Button
          variant="outline"
          size="sm"
          onClick={() => table.previousPage()}
          disabled={!table.getCanPreviousPage()}
        >
          Previous
        </Button>
        <Button
          variant="outline"
          size="sm"
          onClick={() => table.nextPage()}
          //disabled={!table.getCanNextPage()}
        >
          Next
        </Button>
      </div>
    </div>
  );
}
