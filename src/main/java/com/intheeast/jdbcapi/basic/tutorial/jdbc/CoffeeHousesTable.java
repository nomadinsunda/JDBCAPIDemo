package com.intheeast.jdbcapi.basic.tutorial.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

public class CoffeeHousesTable {
  private final Connection con;
  private final String dbms;

  public CoffeeHousesTable(Connection con, String dbName, String dbms) {
    this.con = con;
    this.dbms = dbms;
  }

  public void createTable() throws SQLException {
    String sql = "CREATE TABLE COFFEE_HOUSES (" +
                 "STORE_ID INTEGER NOT NULL, " +
                 "CITY VARCHAR(32), " +
                 "COFFEE INT NOT NULL, " +
                 "MERCH INT NOT NULL, " +
                 "TOTAL INT NOT NULL, " +
                 "PRIMARY KEY (STORE_ID))";
    try (Statement stmt = con.createStatement()) {
      stmt.executeUpdate(sql);
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }

  public void dropTable() throws SQLException {
    try (Statement stmt = con.createStatement()) {
      if (this.dbms.equals("mysql")) {
        stmt.executeUpdate("DROP TABLE IF EXISTS COFFEE_HOUSES");
      } else {
        stmt.executeUpdate("DROP TABLE COFFEE_HOUSES");
      }
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }
  
  public void populateTable() throws SQLException {
	    try (Statement stmt = con.createStatement()) {
	      stmt.executeUpdate("insert into COFFEE_HOUSES " +
	              "values(10023, 'Mendocino', 3450, 2005, 5455)");
	      stmt.executeUpdate("insert into COFFEE_HOUSES " +
	              "values(33002, 'Seattle', 4699, 3109, 7808)");
	      stmt.executeUpdate("insert into COFFEE_HOUSES " +
	              "values(10040, 'SF', 5386, 2841, 8227)");
	      stmt.executeUpdate("insert into COFFEE_HOUSES " +
	              "values(32001, 'Portland', 3147, 3579, 6726)");
	      stmt.executeUpdate("insert into COFFEE_HOUSES " +
	              "values(10042, 'SF', 2863, 1874, 4710)");
	      stmt.executeUpdate("insert into COFFEE_HOUSES " +
	              "values(10024, 'Sacramento', 1987, 2341, 4328)");
	      stmt.executeUpdate("insert into COFFEE_HOUSES " +
	              "values(10039, 'Carmel', 2691, 1121, 3812)");
	      stmt.executeUpdate("insert into COFFEE_HOUSES " +
	              "values(10041, 'LA', 1533, 1007, 2540)");
	      stmt.executeUpdate("insert into COFFEE_HOUSES " +
	              "values(33005, 'Olympia', 2733, 1550, 4283)");
	      stmt.executeUpdate("insert into COFFEE_HOUSES " +
	              "values(33010, 'Seattle', 3210, 2177, 5387)");
	      stmt.executeUpdate("insert into COFFEE_HOUSES " +
	              "values(10035, 'SF', 1922, 1056, 2978)");
	      stmt.executeUpdate("insert into COFFEE_HOUSES " +
	              "values(10037, 'LA', 2143, 1876, 4019)");
	      stmt.executeUpdate("insert into COFFEE_HOUSES " +
	              "values(10034, 'San_Jose', 1234, 1032, 2266)");
	      stmt.executeUpdate("insert into COFFEE_HOUSES " +
	              "values(32004, 'Eugene', 1356, 1112, 2468)");
	    } catch (SQLException e) {
	      JDBCTutorialUtilities.printSQLException(e);
	    }
	  }

	  public void viewCoffeeHouses() throws SQLException {
	    
	    String query = "select CITY, STORE_ID from COFFEE_HOUSES";
	    try (Statement stmt = con.createStatement()) {
	      ResultSet rs = stmt.executeQuery(query);
	      System.out.println("COFFEE_HOUSES and their ID Numbers:");
	      while (rs.next()) {
	        String s = rs.getString("CITY");
	        int n = rs.getInt("STORE_ID");
	        System.out.println(s + "   " + n);
	      }
	    } catch (SQLException e) {
	      JDBCTutorialUtilities.printSQLException(e);
	    }
	  }

	  public static void viewTable(Connection con) throws SQLException {
	    String query =
	      "select STORE_ID, CITY, COFFEE, MERCH, TOTAL from COFFEE_HOUSES";
	    try (Statement stmt = con.createStatement()) {
	      ResultSet rs = stmt.executeQuery(query);
	      while (rs.next()) {
	        int storeID = rs.getInt("STORE_ID");
	        String city = rs.getString("CITY");
	        int coffee = rs.getInt("COFFEE");
	        int merch = rs.getInt("MERCH");
	        int total = rs.getInt("TOTAL");

	        System.out.println(city + "(" + storeID + "): " + coffee +
	                           ", " + merch + ", " + total);
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
	      JDBCTutorialUtilities.createDatabase(myConnection,
	                                           myJDBCTutorialUtilities.dbName,
	                                           myJDBCTutorialUtilities.dbms);
	//
	      JDBCTutorialUtilities.initializeTables(myConnection,
	                                             myJDBCTutorialUtilities.dbName,
	                                             myJDBCTutorialUtilities.dbms);
	      
	      System.out.println("\nContents of COFFEE_HOUSES table:");
	      
	      CoffeeHousesTable.viewTable(myConnection);

	    } catch (SQLException e) {
	      JDBCTutorialUtilities.printSQLException(e);
	    } finally {
	      JDBCTutorialUtilities.closeConnection(myConnection);
	    }
	  }
}
