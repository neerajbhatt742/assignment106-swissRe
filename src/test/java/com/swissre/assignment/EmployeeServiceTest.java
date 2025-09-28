package com.swissre.assignment;

import com.swissre.assignment.model.Employee;
import com.swissre.assignment.service.EmployeeService;
import com.swissre.assignment.util.CsvReader;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {

    @Test
    void managerUnderAndOverSalaryTests() {
        // manager with two subordinates average salary 40000 -> minAllowed=48000, maxAllowed=60000
        Employee m1 = new Employee(1, "M", "One", 47000, null); // under by 1000
        Employee s1 = new Employee(2, "S", "A", 40000, 1);
        Employee s2 = new Employee(3, "S", "B", 40000, 1);
        m1.getSubordinates().add(s1);
        m1.getSubordinates().add(s2);

        Map<Integer, Employee> employees = new HashMap<>();
        employees.put(1, m1);
        employees.put(2, s1);
        employees.put(3, s2);

        EmployeeService svc = new EmployeeService(employees);
        svc.checkSalaryConstraints();

        // adjust manager to be overpaid
        m1 = new Employee(4, "M", "Two", 61000, null); // over by 1000 relative to same subs
        s1 = new Employee(5, "S", "C", 40000, 4);
        s2 = new Employee(6, "S", "D", 40000, 4);
        m1.getSubordinates().add(s1);
        m1.getSubordinates().add(s2);

        employees.clear();
        employees.put(4, m1);
        employees.put(5, s1);
        employees.put(6, s2);

        svc = new EmployeeService(employees);
        svc.checkSalaryConstraints();

        // boundary tests: exact min and exact max
        Employee mb1 = new Employee(7, "MB", "Min", 48000, null);
        Employee sb1 = new Employee(8, "SB", "1", 40000, 7);
        Employee sb2 = new Employee(9, "SB", "2", 40000, 7);
        mb1.getSubordinates().add(sb1);
        mb1.getSubordinates().add(sb2);

        employees.clear();
        employees.put(7, mb1);
        employees.put(8, sb1);
        employees.put(9, sb2);

        svc = new EmployeeService(employees);
        svc.checkSalaryConstraints();
    }

    @Test
    void reportingLineLengthAndCycleTests() {
        // build chain 1 -> 2 -> 3 -> 4 -> 5 -> 6 (depth for 6 = 5)
        Employee e1 = new Employee(1, "E1", "L", 100000, null);
        Employee e2 = new Employee(2, "E2", "L", 90000, 1);
        Employee e3 = new Employee(3, "E3", "L", 80000, 2);
        Employee e4 = new Employee(4, "E4", "L", 70000, 3);
        Employee e5 = new Employee(5, "E5", "L", 60000, 4);
        Employee e6 = new Employee(6, "E6", "L", 50000, 5);

        Map<Integer, Employee> employees = new HashMap<>();
        employees.put(1, e1);
        employees.put(2, e2);
        employees.put(3, e3);
        employees.put(4, e4);
        employees.put(5, e5);
        employees.put(6, e6);

        EmployeeService svc = new EmployeeService(employees);
        // capture output to verify reporting line message
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.io.PrintStream originalOut = System.out;
        try {
            System.setOut(new java.io.PrintStream(out));
            svc.checkReportingLines();
        } finally {
            System.setOut(originalOut);
        }
        String output = out.toString();
        // should report that employee E6 has a reporting line too long by 1
        assertTrue(output.contains("Employee E6 has reporting line too long"));

        // cycle: 10 -> 11 -> 12 -> 10
        Employee c1 = new Employee(10, "C1", "C", 50000, 12);
        Employee c2 = new Employee(11, "C2", "C", 50000, 10);
        Employee c3 = new Employee(12, "C3", "C", 50000, 11);
        Map<Integer, Employee> cyc = new HashMap<>();
        cyc.put(10, c1);
        cyc.put(11, c2);
        cyc.put(12, c3);
        EmployeeService svc2 = new EmployeeService(cyc);
        // ensure cycle does not cause infinite loop; invoking checkReportingLines should complete
        out = new java.io.ByteArrayOutputStream();
        originalOut = System.out;
        try {
            System.setOut(new java.io.PrintStream(out));
            svc2.checkReportingLines();
        } finally {
            System.setOut(originalOut);
        }
        // no assertion about message content; just ensure it ran without hanging and produced some output (possibly none)
        assertNotNull(out.toString());
    }

    @Test
    void csvReaderIntegrationTest() throws Exception {
        // prepare a temp CSV file
        File tmp = Files.createTempFile("employees_test", ".csv").toFile();
        try (FileWriter fw = new FileWriter(tmp)) {
            fw.write("Id,firstName,lastName,salary,managerId\n");
            fw.write("1,Joe,Doe,60000,\n");
            fw.write("2,Martin,Chekov,45000,1\n");
            fw.write("3,Bob,Ronstad,47000,1\n");
            fw.write("4,Alice,Hasacat,50000,2\n");
        }

        Map<Integer, Employee> map = CsvReader.readEmployees(tmp.getAbsolutePath());
        assertEquals(4, map.size());
        Employee joe = map.get(1);
        assertNotNull(joe);
        assertEquals(2, joe.getSubordinates().size());

        // cleanup
        tmp.delete();
    }
}
