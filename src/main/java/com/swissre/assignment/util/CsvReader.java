package com.swissre.assignment.util;

import com.swissre.assignment.model.Employee;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CsvReader {
    public static Map<Integer, Employee> readEmployees(String filePath) throws IOException {
        Map<Integer, Employee> employees = new HashMap<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        for (int i = 1; i < lines.size(); i++) {
            String[] tokens = lines.get(i).split(",");
            int id = Integer.parseInt(tokens[0]);
            String firstName = tokens[1];
            String lastName = tokens[2];
            double salary = Double.parseDouble(tokens[3]);
            Integer managerId = tokens.length > 4 && !tokens[4].isEmpty() ? Integer.parseInt(tokens[4]) : null;

            employees.put(id, new Employee(id, firstName, lastName, salary, managerId));
        }

        for (Employee emp : employees.values()) {
            if (emp.getManagerId() != null) {
                Employee manager = employees.get(emp.getManagerId());
                manager.getSubordinates().add(emp);
            }
        }
        return employees;
    }
}
