import java.sql.*;
import java.util.*;

public class JDBCconnection{
    public static synchronized Connection getConnection() {
        String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?allowPublicKeyRetrieval=true&useSSL=false";
        String userName = "root";
        String password = "Olaybhai";
        Connection connection;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        listDrivers();
        try {
            connection = DriverManager.getConnection(jdbcURL, userName, password);
            return connection;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    private static void listDrivers() {
        Enumeration<Driver> driverList = DriverManager.getDrivers();
        while (driverList.hasMoreElements()) {
            Driver driverClass = (Driver) driverList.nextElement();
        }
    }
}

