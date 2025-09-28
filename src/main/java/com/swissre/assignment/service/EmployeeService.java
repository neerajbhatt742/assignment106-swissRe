package com.swissre.assignment.service;

import com.swissre.assignment.model.Employee;
import java.util.*;

public class EmployeeService {
    private Map<Integer, Employee> employees;

    public EmployeeService(Map<Integer, Employee> employees) {
        this.employees = employees;
    }

    public void checkSalaryConstraints() {
        for (Employee manager : employees.values()) {
            if (!manager.getSubordinates().isEmpty()) {
                double avgSubSalary = manager.getAverageSubordinateSalary();
                double minAllowed = avgSubSalary * 1.2;
                double maxAllowed = avgSubSalary * 1.5;

                if (manager.getSalary() < minAllowed) {
                    System.out.printf("Manager %s earns %.2f less than allowed%n",
                        manager.getFirstName(), (minAllowed - manager.getSalary()));
                } else if (manager.getSalary() > maxAllowed) {
                    System.out.printf("Manager %s earns %.2f more than allowed%n",
                        manager.getFirstName(), (manager.getSalary() - maxAllowed));
                }
            }
        }
    }

    public void checkReportingLines() {
        for (Employee emp : employees.values()) {
            int depth = getDepth(emp);
            if (depth > 4) {
                System.out.printf("Employee %s has reporting line too long by %d%n",
                    emp.getFirstName(), (depth - 4));
            }
        }
    }

    private int getDepth(Employee emp) {
        int depth = 0;
        while (emp.getManagerId() != null) {
            emp = employees.get(emp.getManagerId());
            depth++;
        }
        return depth;
    }

    // For testing
    public int getDepthForTest(Employee emp) {
        return getDepth(emp);
    }
}
