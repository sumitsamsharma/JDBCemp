import org.junit.*;

import java.sql.*;
import java.time.*;
import java.util.ArrayList;

public class testEmpPayRollJDBC {
    EmployeeOperation p;

    @Test
    public void CountEntriesAfterReadingData() {
        p = EmployeeOperation.getInstance();
        Assert.assertEquals(3, p.readData().size());
    }

    @Test
    public void test2CheckUpdate() {
        p = EmployeeOperation.getInstance();
        p.update("Sumit", 300000);
        Assert.assertEquals(300000, p.readData().get(0).basic_pay);
    }

    @Test
    public void test3CheckBetween() {
        p = EmployeeOperation.getInstance();
        Assert.assertEquals(3, p.getBetween(1, 3));
    }

    @Test
    public void test4CheckSum() {
        p = EmployeeOperation.getInstance();
        Assert.assertEquals(1, p.getSum());
    }

    @Test
    public void checking_AdditionOf_NewEmployee() {
        p = EmployeeOperation.getInstance();
        int count = p.readData().size();
        Employee emp = new Employee("Capgemini", "Sales", "Preeti", "8123467890", "Mumbai", 'F', 50000);
        p.createEmployee(emp);
        Assert.assertEquals(count + 1, p.readData().size());
    }

    @Test
    public void CascadeDelete() {
        p = EmployeeOperation.getInstance();
        int count = p.readData().size();
        p.cascadingDelete("Sumit");
        Assert.assertEquals(count - 1, p.readData().size());
    }

    @Test
    public void Adding_MultipleEmployees() {
        p = EmployeeOperation.getInstance();
        Employee e1 = new Employee("Capgemini", "Sales", "Sumit", "8123467890", "Gurgaon", 'M', 50000);
        Employee e2 = new Employee("Samsung", "HR", "Abc", "8634254321", "Noida", 'F', 50000);
        int count = p.readData().size();
        Instant start = Instant.now();
        p.createEmployee(e1);
        p.createEmployee(e2);
        Instant end = Instant.now();
        System.out.println("Duration Without Thread: " + Duration.between(start, end));
        Assert.assertEquals(count + 2, p.readData().size());
        p.cascadingDelete("Sumit");
        p.cascadingDelete("Abc");
    }

   
}
