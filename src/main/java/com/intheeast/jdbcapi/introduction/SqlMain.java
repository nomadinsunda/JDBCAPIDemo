package com.intheeast.jdbcapi.introduction;

import java.sql.*;

public class SqlMain {

    private static final String URL = "jdbc:mysql://localhost:3306/testdb?serverTimezone=Asia/Seoul";
    private static Connection con;

    public static void main(String[] args) {
        try {
            connect("root", "1234");

            dropTables(); // 1. DROP
            createTables(); // 2. CREATE
            insertEmployees(); // 3. INSERT
            insertProjects(); // 4. INSERT
            updateAndDelete(); // 5. UPDATE + DELETE
            alterTables(); // 6. ALTER
            innerJoin(); // 7. INNER JOIN
            leftJoin(); // 8. LEFT JOIN
            useResultSetCursor(); // 9. ResultSet 커서

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            disconnect();
        }
    }

    private static void connect(String user, String password) throws SQLException {
        con = DriverManager.getConnection(URL, user, password);
        System.out.println("[DB 연결됨]");
    }

    private static void disconnect() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                System.out.println("[DB 연결 종료됨]");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🔹 1. DROP TABLES
    private static void dropTables() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS Projects");
            stmt.executeUpdate("DROP TABLE IF EXISTS Employees");
            System.out.println("[테이블 삭제 완료]");
        }
    }

    // 🔹 2. CREATE TABLES
    private static void createTables() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.execute("""
                CREATE TABLE Employees (
                    Employee_ID INT AUTO_INCREMENT PRIMARY KEY,
                    First_Name VARCHAR(50),
                    Last_Name VARCHAR(50),
                    Date_Of_Birth DATE,
                    Car_Number INT
                )
            """);
            stmt.execute("""
                CREATE TABLE Projects (
                    Project_ID INT AUTO_INCREMENT PRIMARY KEY,
                    Project_Name VARCHAR(100),
                    Employee_ID INT,
                    FOREIGN KEY (Employee_ID) REFERENCES Employees(Employee_ID) ON DELETE CASCADE
                )
            """);
            System.out.println("[테이블 생성 완료]");
        }
    }

    // 🔹 3. INSERT (단일/다중)
    private static void insertEmployees() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate("""
                INSERT INTO Employees (First_Name, Last_Name, Date_Of_Birth, Car_Number)
                VALUES ('John', 'Doe', '1985-06-15', 3)
            """);

            stmt.executeUpdate("""
                INSERT INTO Employees (First_Name, Last_Name, Date_Of_Birth, Car_Number)
                VALUES
                ('Jane', 'Smith', '1990-07-22', 4),
                ('Emily', 'Jones', '1978-12-11', 5),
                ('Michael', 'Brown', '1983-04-08', NULL)
            """);

            System.out.println("[Employees 데이터 삽입 완료]");
        }
    }

    private static void insertProjects() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate("""
                INSERT INTO Projects (Project_Name, Employee_ID)
                VALUES
                ('AI Platform', 1),
                ('Data Lake', 2),
                ('CRM Upgrade', 3)
            """);
            System.out.println("[Projects 데이터 삽입 완료]");
        }
    }

    // 🔹 4. UPDATE / DELETE
    private static void updateAndDelete() throws SQLException {
        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate("UPDATE Employees SET Car_Number = 10 WHERE Last_Name = 'Doe'");
            stmt.executeUpdate("UPDATE Employees SET Last_Name = 'Young' WHERE Date_Of_Birth > '1990-01-01'");
            stmt.executeUpdate("DELETE FROM Employees WHERE Car_Number IS NULL");
            stmt.executeUpdate("DELETE FROM Employees WHERE Date_Of_Birth < '1980-01-01'");
            System.out.println("[UPDATE / DELETE 완료]");
        }
    }

    // 🔹 5. ALTER TABLE (ADD, MODIFY, RENAME, DROP 제약조건)
    private static void alterTables() throws SQLException {
        try (Statement stmt = con.createStatement()) {

            // 🔹 사전 정리
            stmt.executeUpdate("DROP TABLE IF EXISTS Projects");
            stmt.executeUpdate("DROP TABLE IF EXISTS Staff");
            System.out.println("[Projects, Staff 테이블 삭제 완료]");

            // 🔹 Employees → Staff 테이블로 이름 변경 (임시 테이블 생성 후 INSERT)
            stmt.executeUpdate("""
                CREATE TABLE Staff (
                    Employee_ID INT AUTO_INCREMENT PRIMARY KEY,
                    First_Name VARCHAR(50),
                    Last_Name VARCHAR(100),
                    Date_Of_Birth DATE,
                    Car_Number INT DEFAULT 0,
                    Email VARCHAR(100)
                )
            """);
            stmt.executeUpdate("""
                INSERT INTO Staff (Employee_ID, First_Name, Last_Name, Date_Of_Birth, Car_Number)
                SELECT Employee_ID, First_Name, Last_Name, Date_Of_Birth, Car_Number FROM Employees
            """);
            stmt.executeUpdate("DROP TABLE Employees");
            System.out.println("[Employees → Staff 테이블 이관 완료]");

            // 🔹 Projects 테이블 재생성
            stmt.execute("""
                CREATE TABLE Projects (
                    Project_ID INT AUTO_INCREMENT PRIMARY KEY,
                    Project_Name VARCHAR(100),
                    Employee_ID INT,
                    FOREIGN KEY (Employee_ID) REFERENCES Staff(Employee_ID) ON DELETE CASCADE
                )
            """);
            System.out.println("[Projects 테이블 재생성 완료]");

            // 🔹 유효한 직원 ID 기준 데이터 삽입 (1, 2만 존재함)
            stmt.executeUpdate("""
                INSERT INTO Projects (Project_Name, Employee_ID)
                VALUES
                ('AI Platform', 1),
                ('Data Lake', 2)
            """);
            System.out.println("[Projects 데이터 재삽입 완료]");

        } catch (SQLException e) {
            System.out.println("[ALTER TABLE 처리 중 오류 발생]");
            e.printStackTrace();
        }
    }

    // 🔹 6. INNER JOIN
    private static void innerJoin() throws SQLException {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("""
                 SELECT e.Employee_ID, e.First_Name, e.Last_Name, p.Project_Name
                 FROM Staff e
                 JOIN Projects p ON e.Employee_ID = p.Employee_ID
             """)) {
            System.out.println("\n[INNER JOIN 결과]");
            while (rs.next()) {
                System.out.printf("직원 %d: %s %s → 프로젝트: %s%n",
                        rs.getInt("Employee_ID"),
                        rs.getString("First_Name"),
                        rs.getString("Last_Name"),
                        rs.getString("Project_Name"));
            }
        }
    }

    // 🔹 7. LEFT JOIN
    private static void leftJoin() throws SQLException {
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("""
                 SELECT p.Project_ID, p.Project_Name, e.First_Name, e.Last_Name
                 FROM Projects p
                 LEFT JOIN Staff e ON p.Employee_ID = e.Employee_ID
             """)) {
            System.out.println("\n[LEFT JOIN 결과]");
            while (rs.next()) {
                System.out.printf("프로젝트: %s (%d) → 직원: %s %s%n",
                        rs.getString("Project_Name"),
                        rs.getInt("Project_ID"),
                        rs.getString("First_Name"),
                        rs.getString("Last_Name"));
            }
        }
    }

    // 🔹 8. ResultSet 커서 사용
    private static void useResultSetCursor() throws SQLException {
        try (Statement stmt = con.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
             ResultSet rs = stmt.executeQuery("SELECT * FROM Staff")) {

            rs.last();
            int rowCount = rs.getRow();
            System.out.println("\n[ResultSet 커서 테스트] 총 직원 수: " + rowCount);

            rs.beforeFirst();
            while (rs.next()) {
                System.out.printf("직원 %d: %s %s, 생년월일: %s%n",
                        rs.getInt("Employee_ID"),
                        rs.getString("First_Name"),
                        rs.getString("Last_Name"),
                        rs.getDate("Date_Of_Birth"));
            }
        }
    }
}
