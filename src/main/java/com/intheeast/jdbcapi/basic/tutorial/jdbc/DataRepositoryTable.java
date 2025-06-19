package com.intheeast.jdbcapi.basic.tutorial.jdbc;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataRepositoryTable {
  private final Connection con;
  private final String dbms;

  public DataRepositoryTable(Connection con, String dbName, String dbms) {
    this.con = con;
    this.dbms = dbms;
  }

  public void createTable() throws SQLException {
    String sql = "CREATE TABLE DATA_REPOSITORY (" +
                 "DOCUMENT_NAME VARCHAR(50), " +
                 "URL VARCHAR(200))";
    try (Statement stmt = con.createStatement()) {
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }

  public void dropTable() throws SQLException {
    try (Statement stmt = con.createStatement()) {
      if (this.dbms.equals("mysql")) {
        stmt.executeUpdate("DROP TABLE IF EXISTS DATA_REPOSITORY");
      } else {
        stmt.executeUpdate("DROP TABLE DATA_REPOSITORY");
      }
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }
  
//public void populateTable() throws SQLException {
//  try (Statement stmt = con.createStatement()) {
//    stmt.executeUpdate();
//    
//    
//  } catch (SQLException e) {
//    JDBCTutorialUtilities.printSQLException(e);
//  }
//}

public void viewDataRepository() throws SQLException {
  
  String query = "select DOCUMENT_NAME from DATA_REPOSITORY";
  try (Statement stmt = con.createStatement()) {
    ResultSet rs = stmt.executeQuery(query);
    System.out.println("DATA_REPOSITORY and their Names:");
    while (rs.next()) {
  	  String documentName = rs.getString("DOCUMENT_NAME");		        
	      System.out.println("Document Name: " + documentName);
    }
  } catch (SQLException e) {
    JDBCTutorialUtilities.printSQLException(e);
  }
}

public static void viewTable(Connection con) throws SQLException {
  String query =
    "select DOCUMENT_NAME, URL from DATA_REPOSITORY";
  try (Statement stmt = con.createStatement()) {
    ResultSet rs = stmt.executeQuery(query);
    while (rs.next()) {
      String documentName = rs.getString("DOCUMENT_NAME");
      String url = rs.getString("URL");
      
      // 콘솔에 출력
      System.out.println("Document Name: " + documentName);
      System.out.println("url: " + url);
    }
  } catch (SQLException e) {
    JDBCTutorialUtilities.printSQLException(e);
  }
}

public static void main(String[] args) {

  JDBCTutorialUtilities myJDBCTutorialUtilities;
  Connection myConnection = null;

  if (args[0] == null) {
    System.err.println("Properties file not specified at command line");
    return;
  } else {
    try {
      myJDBCTutorialUtilities = new JDBCTutorialUtilities(args[0]);
    } catch (Exception e) {
      System.err.println("Problem reading properties file " + args[0]);
      e.printStackTrace();
      return;
    }
  }
  try {
    myConnection = myJDBCTutorialUtilities.getConnection();

    // Java DB does not have an SQL create database command; it does require createDatabase
//    JDBCTutorialUtilities.createDatabase(myConnection,
//                                         myJDBCTutorialUtilities.dbName,
//                                         myJDBCTutorialUtilities.dbms);
//
//    JDBCTutorialUtilities.initializeTables(myConnection,
//                                           myJDBCTutorialUtilities.dbName,
//                                           myJDBCTutorialUtilities.dbms);
    
    System.out.println("\nContents of DATA_REPOSITORY table:");
    
    DataRepositoryTable.viewTable(myConnection);

  } catch (SQLException e) {
    JDBCTutorialUtilities.printSQLException(e);
  } finally {
    JDBCTutorialUtilities.closeConnection(myConnection);
  }
}
}