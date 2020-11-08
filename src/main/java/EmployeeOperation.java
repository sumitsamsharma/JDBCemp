import java.sql.*;
import java.util.*;

public class EmployeeOperation {
    private EmployeeOperation() {
    }

    private static EmployeeOperation instance = null;

    public static EmployeeOperation getInstance() {
        if (instance == null)
            instance = new EmployeeOperation();
        return instance;
    }

    JDBCconnection con = new JDBCconnection();
    PreparedStatement updateStmt;

    public List<Employee> readData() {
        String sql = "select * from employee e, payroll p where e.emp_id=p.emp_id ;";
        List<Employee> arrayEmp = new ArrayList<Employee>();
        try {
            Connection c = con.getConnection();
            Statement statement = c.createStatement();
            ResultSet result = statement.executeQuery(sql);
            while (result.next()) {
                Employee e = new Employee();
                e.company_id = result.getInt("company_id");
                e.emp_id = result.getInt("emp_id");
                e.name = result.getString("name");
                e.phone = result.getString("phone");
                e.address = result.getString("address");
                e.gender = result.getString("gender").charAt(0);
                e.basic_pay = result.getInt("basic_pay");
                arrayEmp.add(e);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return arrayEmp;
    }

    public int getBetween(int start, int end) {
        int count = 0;
        Connection c = con.getConnection();
        try {
            updateStmt = c.prepareStatement("select * from employee where emp_id between ? and ?");
            updateStmt.setInt(1, start);
            updateStmt.setInt(2, end);
            ResultSet result = updateStmt.executeQuery();
            while (result.next()) {
                count++;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return count;
    }

    public void createEmployee(Employee e) {
        Connection c = con.getConnection();
        try {
            c.setAutoCommit(false);
            //inserting in Company details
            updateStmt = c.prepareStatement("insert into company(company_name) values (?)");
            updateStmt.setString(1, e.company_name);
            updateStmt.executeUpdate();
            //Getting company_id
            updateStmt = c.prepareStatement("select company_id from company where company_name=?");
            updateStmt.setString(1, e.company_name);
            ResultSet result = updateStmt.executeQuery();
            while (result.next()) {
                e.company_id = result.getInt(1);
            }
            //Pushing employee details
            updateStmt = c.prepareStatement("insert into employee(company_id,name,phone,address,gender) values (?,?,?,?,?)");
            updateStmt.setInt(1, e.company_id);
            updateStmt.setString(2, e.name);
            updateStmt.setString(3, e.phone);
            updateStmt.setString(4, e.address);
            updateStmt.setString(5, Character.toString(e.gender));
            updateStmt.executeUpdate();

            //Pushing department details
            updateStmt = c.prepareStatement("insert into department(department_name) values (?)");
            updateStmt.setString(1, e.department_name);
            updateStmt.executeUpdate();

            //Getting the employee_id
            updateStmt = c.prepareStatement("select emp_id from employee where name=?");
            updateStmt.setString(1, e.name);
            ResultSet result1 = updateStmt.executeQuery();
            while (result1.next()) {
                e.emp_id = result1.getInt(1);
            }
            //Getting the employee_id w
            updateStmt = c.prepareStatement("select department_id from department where department_name=?");
            updateStmt.setString(1, e.department_name);
            ResultSet rs = updateStmt.executeQuery();
            while (rs.next()) {
                e.department_id = rs.getInt(1);
                break;
            }
            //Linking the employee_id to department_id
            updateStmt = c.prepareStatement("insert into employee_department(emp_id,department_id) values (?,?)");
            updateStmt.setInt(1, e.emp_id);
            updateStmt.setInt(2, e.department_id);
            updateStmt.executeUpdate();
            //Pushing payroll details
            updateStmt = c.prepareStatement("insert into payroll(emp_id,basic_pay,deductions,taxable_pay,tax,net_pay) values (?,?,?,?,?,?)");
            updateStmt.setInt(1, e.emp_id);
            updateStmt.setInt(2, e.basic_pay);
            updateStmt.setInt(3, e.deductions);
            updateStmt.setInt(4, e.taxable_pay);
            updateStmt.setInt(5, e.tax);
            updateStmt.setInt(6, e.net_pay);
            updateStmt.executeUpdate();
            c.commit();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                c.rollback();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
    }

    public void cascadingDelete(String name) {
        try {
            Connection c = con.getConnection();
            int id = 0;
            updateStmt = c.prepareStatement("select emp_id from employee where name=?");
            updateStmt.setString(1, name);
            ResultSet result1 = updateStmt.executeQuery();
            while (result1.next()) {
                id = result1.getInt(1);
                break;
            }
            //Clearing all tables:- Payroll,employee_department,employee table
            updateStmt = c.prepareStatement("delete from payroll where emp_id=?");
            updateStmt.setInt(1, id);
            updateStmt.executeUpdate();

            updateStmt = c.prepareStatement("delete from employee_department where emp_id=?");
            updateStmt.setInt(1, id);
            updateStmt.executeUpdate();

            updateStmt = c.prepareStatement("delete from employee where emp_id=?");
            updateStmt.setInt(1, id);
            updateStmt.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int getSum() {
        int count = 0;
        Connection c = con.getConnection();
        try {
            updateStmt = c.prepareStatement("select count(name) from employee where gender='F'");
            ResultSet result = updateStmt.executeQuery();
            while (result.next()) {
                count++;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return count;
    }

    public void update(String name, int salary) {
        try {
            Connection c = con.getConnection();
            updateStmt = c.prepareStatement("update payroll set basic_pay= ? where emp_id=(select emp_id from employee where name = ?);");
            updateStmt.setInt(1, salary);
            updateStmt.setString(2, name);
            updateStmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addEmployeesWithThread(ArrayList<Employee> employeeList) {
        System.out.println("addEmployeesWithThread");
        for (Employee e : employeeList) {
            Runnable task = () -> {
                createEmployee(e);
            };
            Thread thread = new Thread(task);
            thread.start();
            try {
                thread.sleep(150);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
    }

    public void updateSalaryWithThread(String[] name, int[] salary) {
        System.out.println("updateSalaryWithThread");
        for (int i = 0; i < name.length; i++) {
            String n = name[i];
            int s = salary[i];
            Runnable task = () -> {
                update(n, s);
            };
            Thread thread = new Thread(task);
            thread.start();
            try {
                thread.sleep(10000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
    }

}
