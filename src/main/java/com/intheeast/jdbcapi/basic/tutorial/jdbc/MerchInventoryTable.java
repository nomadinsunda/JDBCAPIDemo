package com.intheeast.jdbcapi.basic.tutorial.jdbc;

import java.sql.*;


public class MerchInventoryTable {
	  private final Connection con;
	  private final String dbms;

	  public MerchInventoryTable(Connection con, String dbName, String dbms) {
	    this.con = con;
	    this.dbms = dbms;
	  }

	  public void createTable() throws SQLException {
	    String sql = "CREATE TABLE MERCH_INVENTORY (" +
	                 "ITEM_ID INTEGER NOT NULL, " +
	                 "ITEM_NAME VARCHAR(20), " +
	                 "SUP_ID INT, " +
	                 "QUAN INT, " +
	                 "DATE_VAL TIMESTAMP, " +
	                 "PRIMARY KEY (ITEM_ID), " +
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
	        stmt.executeUpdate("DROP TABLE IF EXISTS MERCH_INVENTORY");
	      } else {
	        stmt.executeUpdate("DROP TABLE MERCH_INVENTORY");
	      }
	    } catch (SQLException e) {
	      JDBCTutorialUtilities.printSQLException(e);
	    }
	  }
	  
	  public void populateTable() throws SQLException {
		    try (Statement stmt = con.createStatement()) {
		      stmt.executeUpdate("insert into MERCH_INVENTORY " +
		              "values(00001234, 'Cup_Large', 456, 28, '2006-04-01')");
		      stmt.executeUpdate("insert into MERCH_INVENTORY " +
		              "values(00001235, 'Cup_Small', 456, 36, '2006-04-01')");
		      stmt.executeUpdate("insert into MERCH_INVENTORY " +
                      "values(00001236, 'Saucer', 456, 64, '2006-04-01')");
		      stmt.executeUpdate("insert into MERCH_INVENTORY " +
                      "values(00001287, 'Carafe', 456, 12, '2006-04-01')");
		      stmt.executeUpdate("insert into MERCH_INVENTORY " +
                      "values(00006931, 'Carafe', 927, 3, '2006-04-01')");
		      stmt.executeUpdate("insert into MERCH_INVENTORY " +
                      "values(00006935, 'PotHolder', 927, 88, '2006-04-01')");
		      stmt.executeUpdate("insert into MERCH_INVENTORY " +
                      "values(00006977, 'Napkin', 927, 108, '2006-04-01')");
		      stmt.executeUpdate("insert into MERCH_INVENTORY " +
                      "values(00006979, 'Towel', 927, 24, '2006-04-01')");
		      stmt.executeUpdate("insert into MERCH_INVENTORY " +
                      "values(00004488, 'CofMaker', 456, 5, '2006-04-01')");
		      stmt.executeUpdate("insert into MERCH_INVENTORY " +
                      "values(00004490, 'CofGrinder', 456, 9, '2006-04-01')");
		      stmt.executeUpdate("insert into MERCH_INVENTORY " +
                      "values(00004495, 'EspMaker', 456, 4, '2006-04-01')");
		      stmt.executeUpdate("insert into MERCH_INVENTORY " +
                      "values(00006914, 'Cookbook', 927, 12, '2006-04-01')");
		      
		    } catch (SQLException e) {
		      JDBCTutorialUtilities.printSQLException(e);
		    }
		  }

		  public void viewMerchInventory() throws SQLException {
		    
		    String query = "select ITEM_NAME, ITEM_ID from MERCH_INVENTORY";
		    try (Statement stmt = con.createStatement()) {
		      ResultSet rs = stmt.executeQuery(query);
		      System.out.println("MERCH_INVENTORY and their ID Numbers:");
		      while (rs.next()) {
		        String s = rs.getString("ITEM_NAME");
		        int n = rs.getInt("ITEM_ID");
		        System.out.println(s + "   " + n);
		      }
		    } catch (SQLException e) {
		      JDBCTutorialUtilities.printSQLException(e);
		    }
		  }

		  public static void viewTable(Connection con) throws SQLException {
		    String query =
		      "select ITEM_ID, ITEM_NAME, SUP_ID, QUAN, DATE_VAL from MERCH_INVENTORY";
		    try (Statement stmt = con.createStatement()) {
		      ResultSet rs = stmt.executeQuery(query);
		      while (rs.next()) {
		        int itemID = rs.getInt("ITEM_ID");
		        String itemName = rs.getString("ITEM_NAME");
		        int subID = rs.getInt("SUP_ID");
		        int quan = rs.getInt("QUAN");
		        Timestamp dateVal = rs.getTimestamp("DATE_VAL");
		        
		        System.out.println(itemName + "(" + itemID + "): " + subID +
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
//		      JDBCTutorialUtilities.createDatabase(myConnection,
//		                                           myJDBCTutorialUtilities.dbName,
//		                                           myJDBCTutorialUtilities.dbms);
		//
//		      JDBCTutorialUtilities.initializeTables(myConnection,
//		                                             myJDBCTutorialUtilities.dbName,
//		                                             myJDBCTutorialUtilities.dbms);
		      
		      System.out.println("\nContents of MERCH_INVENTORY table:");
		      
		      MerchInventoryTable.viewTable(myConnection);

		    } catch (SQLException e) {
		      JDBCTutorialUtilities.printSQLException(e);
		    } finally {
		      JDBCTutorialUtilities.closeConnection(myConnection);
		    }
		  }
	}