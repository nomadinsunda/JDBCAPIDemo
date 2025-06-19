package com.intheeast.jdbcapi.basic.tutorial.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class CofInventoryTable {
  private final Connection con;
  private final String dbms;

  public CofInventoryTable(Connection con, String dbName, String dbms) {
    this.con = con;
    this.dbms = dbms;
  }

  public void createTable() throws SQLException {
    String sql = "CREATE TABLE COF_INVENTORY (" +
                 "WAREHOUSE_ID INTEGER NOT NULL, " +
                 "COF_NAME VARCHAR(32) NOT NULL, " +
                 "SUP_ID INT NOT NULL, " +
                 "QUAN INT NOT NULL, " +
                 "DATE_VAL TIMESTAMP, " +
                 "FOREIGN KEY (COF_NAME) REFERENCES COFFEES(COF_NAME), " +
                 "FOREIGN KEY (SUP_ID) REFERENCES SUPPLIERS(SUP_ID))";
    try (Statement stmt = con.createStatement()) {
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }

  public void dropTable() throws SQLException {
    try (Statement stmt = con.createStatement()) {
      if (this.dbms.equals("mysql")) {
        stmt.executeUpdate("DROP TABLE IF EXISTS COF_INVENTORY");
      } else {
        stmt.executeUpdate("DROP TABLE COF_INVENTORY");
      }
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }
  
  public void populateTable() throws SQLException {
	    try (Statement stmt = con.createStatement()) {
	      stmt.executeUpdate("insert into COF_INVENTORY " +
	              "values(1234, 'Colombian', 101, 0, '2006-04-01')");
	      stmt.executeUpdate("insert into COF_INVENTORY " +
	              "values(1234, 'French_Roast', 49,  0, '2006-04-01')");
	      stmt.executeUpdate("insert into COF_INVENTORY " +
                "values(1234, 'Espresso', 150, 0, '2006-04-01')");
	      stmt.executeUpdate("insert into COF_INVENTORY " +
                "values(1234, 'Colombian_Decaf', 101, 0, '2006-04-01')");
	      	      
	    } catch (SQLException e) {
	      JDBCTutorialUtilities.printSQLException(e);
	    }
	  }

	  public void viewCofInventory() throws SQLException {
	    
	    String query = "select COF_NAME, WAREHOUSE_ID from COF_INVENTORY";
	    try (Statement stmt = con.createStatement()) {
	      ResultSet rs = stmt.executeQuery(query);
	      System.out.println("COF_INVENTORY and their ID Numbers:");
	      while (rs.next()) {
	        String s = rs.getString("COF_NAME");
	        int n = rs.getInt("WAREHOUSE_ID");
	        System.out.println(s + "   " + n);
	      }
	    } catch (SQLException e) {
	      JDBCTutorialUtilities.printSQLException(e);
	    }
	  }

	  public static void viewTable(Connection con) throws SQLException {
	    String query =
	      "select WAREHOUSE_ID, COF_NAME, SUP_ID, QUAN, DATE_VAL from COF_INVENTORY";
	    try (Statement stmt = con.createStatement()) {
	      ResultSet rs = stmt.executeQuery(query);
	      while (rs.next()) {
	        int wareHouseID = rs.getInt("WAREHOUSE_ID");
	        String cofName = rs.getString("COF_NAME");
	        int subID = rs.getInt("SUP_ID");
	        int quan = rs.getInt("QUAN");
	        Timestamp dateVal = rs.getTimestamp("DATE_VAL");
	        
	        System.out.println(cofName + "(" + wareHouseID + "): " + subID +
	                           ", " + quan + ", " + dateVal);
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
	      
	      System.out.println("\nContents of COF_INVENTORY table:");
	      
	      CofInventoryTable.viewTable(myConnection);

	    } catch (SQLException e) {
	      JDBCTutorialUtilities.printSQLException(e);
	    } finally {
	      JDBCTutorialUtilities.closeConnection(myConnection);
	    }
	  }
}