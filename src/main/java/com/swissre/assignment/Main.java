package com.swissre.assignment;

import com.swissre.assignment.model.Employee;
import com.swissre.assignment.service.EmployeeService;
import com.swissre.assignment.util.CsvReader;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        String filePath = "src/main/resources/employees.csv";
        Map<Integer, Employee> employees = CsvReader.readEmployees(filePath);

        EmployeeService service = new EmployeeService(employees);
        service.checkSalaryConstraints();
        service.checkReportingLines();
    }
}
