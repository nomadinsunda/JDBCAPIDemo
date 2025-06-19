package com.intheeast.jdbcapi.introduction;

import java.sql.*;

public class JoinMain {

    private static final String URL = "jdbc:mysql://localhost:3306/testdb?serverTimezone=Asia/Seoul";
    private static Connection con;

    public static void main(String[] args) {
        try {
            getConnection("root", "1234");

            recreateEmployeesTableAndInsertData();
            recreateCarsTableAndInsertData();
            printEmployeesWithCompanyCars();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private static void getConnection(String username, String password) throws SQLException {
        if (con == null || con.isClosed()) {
            con = DriverManager.getConnection(URL, username, password);
            System.out.println("[데이터베이스 연결 완료]");
        }
    }

    private static void closeConnection() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                System.out.println("[데이터베이스 연결 종료]");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Employees 테이블 재생성 및 데이터 삽입
    private static void recreateEmployeesTableAndInsertData() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS Employees");
            System.out.println("[Employees 테이블 삭제 완료]");

            String createTableSql = """
                CREATE TABLE Employees (
                    Employee_Number INT PRIMARY KEY,
                    First_Name VARCHAR(50) NOT NULL,
                    Last_Name VARCHAR(50) NOT NULL,
                    Date_of_Birth DATE,
                    Car_Number INT
                )
            """;
            stmt.execute(createTableSql);
            System.out.println("[Employees 테이블 생성 완료]");
        }

        String insertSql = """
            INSERT INTO Employees (Employee_Number, First_Name, Last_Name, Date_of_Birth, Car_Number)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = con.prepareStatement(insertSql)) {
            insertEmployee(pstmt, 10001, "Axel", "Washington", "1943-08-28", 5);
            insertEmployee(pstmt, 10083, "Arvid", "Sharma", "1954-11-24", null);
            insertEmployee(pstmt, 10120, "Jonas", "Ginsberg", "1969-01-01", null);
            insertEmployee(pstmt, 10005, "Florence", "Wojokowski", "1971-07-04", 12);
            insertEmployee(pstmt, 10099, "Sean", "Washington", "1966-09-21", null);
            insertEmployee(pstmt, 10035, "Elizabeth", "Yamaguchi", "1959-12-24", null);
            System.out.println("[Employees 레코드 삽입 완료]");
        }
    }

    private static void insertEmployee(PreparedStatement pstmt, int number, String first, String last, String birth, Integer carNumber) throws SQLException {
        pstmt.setInt(1, number);
        pstmt.setString(2, first);
        pstmt.setString(3, last);
        pstmt.setDate(4, Date.valueOf(birth));
        if (carNumber != null) {
            pstmt.setInt(5, carNumber);
        } else {
            pstmt.setNull(5, Types.INTEGER);
        }
        pstmt.executeUpdate();
    }

    // ✅ Cars 테이블 재생성 및 데이터 삽입
    private static void recreateCarsTableAndInsertData() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS Cars");
            System.out.println("[Cars 테이블 삭제 완료]");

            String createTableSql = """
                CREATE TABLE Cars (
                    Car_Number INT PRIMARY KEY,
                    Make VARCHAR(50) NOT NULL,
                    Model VARCHAR(100) NOT NULL,
                    Year INT NOT NULL
                )
            """;
            stmt.execute(createTableSql);
            System.out.println("[Cars 테이블 생성 완료]");
        }

        String insertSql = """
            INSERT INTO Cars (Car_Number, Make, Model, Year)
            VALUES (?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = con.prepareStatement(insertSql)) {
            insertCar(pstmt, 5, "Honda", "Civic DX", 1996);
            insertCar(pstmt, 12, "Toyota", "Corolla", 1999);
            System.out.println("[Cars 레코드 삽입 완료]");
        }
    }

    private static void insertCar(PreparedStatement pstmt, int carNumber, String make, String model, int year) throws SQLException {
        pstmt.setInt(1, carNumber);
        pstmt.setString(2, make);
        pstmt.setString(3, model);
        pstmt.setInt(4, year);
        pstmt.executeUpdate();
    }

    // ✅ JOIN 쿼리 실행
    private static void printEmployeesWithCompanyCars() throws SQLException {
        String joinSql = """
            SELECT Employees.First_Name, Employees.Last_Name,
                   Cars.Make, Cars.Model, Cars.Year
            FROM Employees, Cars
            WHERE Employees.Car_Number = Cars.Car_Number
        """;

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(joinSql)) {

            System.out.println("\n[회사 차량을 가진 직원 목록]");
            while (rs.next()) {
                String first = rs.getString("First_Name");
                String last = rs.getString("Last_Name");
                String make = rs.getString("Make");
                String model = rs.getString("Model");
                int year = rs.getInt("Year");

                System.out.printf("직원: %s %s, 차량: %s %s (%d)%n", first, last, make, model, year);
            }
        }
    }
}
