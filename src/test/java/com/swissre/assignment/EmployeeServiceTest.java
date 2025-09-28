package com.swissre.assignment;

import com.swissre.assignment.model.Employee;
import com.swissre.assignment.service.EmployeeService;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {

    @Test
    void testDepthCalculation() {
        Employee ceo = new Employee(1, "CEO", "Boss", 100000, null);
        Employee emp = new Employee(2, "John", "Doe", 50000, 1);
        ceo.getSubordinates().add(emp);

        Map<Integer, Employee> employees = new HashMap<>();
        employees.put(1, ceo);
        employees.put(2, emp);

        EmployeeService service = new EmployeeService(employees);
        assertEquals(1, service.getDepthForTest(emp));
    }
}
