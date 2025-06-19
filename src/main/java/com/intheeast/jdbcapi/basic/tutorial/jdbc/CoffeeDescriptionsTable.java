package com.intheeast.jdbcapi.basic.tutorial.jdbc;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class CoffeeDescriptionsTable {
  private final Connection con;
  private final String dbms;

  public CoffeeDescriptionsTable(Connection con, String dbName, String dbms) {
    this.con = con;
    this.dbms = dbms;
  }

  public void createTable() throws SQLException {
    String sql = "CREATE TABLE COFFEE_DESCRIPTIONS (" +
                 "COF_NAME VARCHAR(32) NOT NULL, " +
                 "COF_DESC BLOB NOT NULL, " +
                 "PRIMARY KEY (COF_NAME), " +
                 "FOREIGN KEY (COF_NAME) REFERENCES COFFEES(COF_NAME))";
    try (Statement stmt = con.createStatement()) {
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }

  public void dropTable() throws SQLException {
    try (Statement stmt = con.createStatement()) {
      if (this.dbms.equals("mysql")) {
        stmt.executeUpdate("DROP TABLE IF EXISTS COFFEE_DESCRIPTIONS");
      } else {
        stmt.executeUpdate("DROP TABLE COFFEE_DESCRIPTIONS");
      }
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }
  
//  public void populateTable() throws SQLException {
//	    try (Statement stmt = con.createStatement()) {
//	      stmt.executeUpdate();
//	      
//	      
//	    } catch (SQLException e) {
//	      JDBCTutorialUtilities.printSQLException(e);
//	    }
//   }

	  public void viewCoffeeDescriptions() throws SQLException {
	    
	    String query = "select COF_NAME from COFFEE_DESCRIPTIONS";
	    try (Statement stmt = con.createStatement()) {
	      ResultSet rs = stmt.executeQuery(query);
	      System.out.println("COFFEE_DESCRIPTIONS and their Names:");
	      while (rs.next()) {
	    	  String coffeeName = rs.getString("COF_NAME");		        
		      System.out.println("Coffee Name: " + coffeeName);
	      }
	    } catch (SQLException e) {
	      JDBCTutorialUtilities.printSQLException(e);
	    }
	  }

	  public static void viewTable(Connection con) throws SQLException {
	    String query =
	      "select COF_NAME, COF_DESC from COFFEE_DESCRIPTIONS";
	    try (Statement stmt = con.createStatement()) {
	      ResultSet rs = stmt.executeQuery(query);
	      while (rs.next()) {
	        String coffeeName = rs.getString("COF_NAME");
	        Blob coffeeDesc = rs.getBlob("COF_DESC");
	        
	        byte[] bytes = coffeeDesc.getBytes(1, (int) coffeeDesc.length());
	        
	        
	        String descText = new String(bytes, java.nio.charset.StandardCharsets.UTF_8);

	        // 콘솔에 출력
	        System.out.println("Coffee Name: " + coffeeName);
	        System.out.println("Description: " + descText);
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
//	      JDBCTutorialUtilities.createDatabase(myConnection,
//	                                           myJDBCTutorialUtilities.dbName,
//	                                           myJDBCTutorialUtilities.dbms);
	//
//	      JDBCTutorialUtilities.initializeTables(myConnection,
//	                                             myJDBCTutorialUtilities.dbName,
//	                                             myJDBCTutorialUtilities.dbms);
	      
	      System.out.println("\nContents of COFFEE_DESCRIPTIONS table:");
	      
	      CoffeeDescriptionsTable.viewTable(myConnection);

	    } catch (SQLException e) {
	      JDBCTutorialUtilities.printSQLException(e);
	    } finally {
	      JDBCTutorialUtilities.closeConnection(myConnection);
	    }
	  }
}