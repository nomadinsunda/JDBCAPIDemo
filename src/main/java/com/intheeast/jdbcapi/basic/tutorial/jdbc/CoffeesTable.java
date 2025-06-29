/*
 * Copyright (c) 1995, 2020, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.intheeast.jdbcapi.basic.tutorial.jdbc;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CoffeesTable {

  private String dbName;
  private Connection con;
  private String dbms;


  public CoffeesTable(Connection connArg, String dbNameArg, String dbmsArg) {
    super();
    this.con = connArg;
    this.dbName = dbNameArg;
    this.dbms = dbmsArg;

  }

  public void createTable() throws SQLException {
    String createString =
      "create table COFFEES " + "(COF_NAME varchar(32) NOT NULL, " +
      "SUP_ID int NOT NULL, " + "PRICE numeric(10,2) NOT NULL, " +
      "SALES integer NOT NULL, " + "TOTAL integer NOT NULL, " +
      "PRIMARY KEY (COF_NAME), " +
      "FOREIGN KEY (SUP_ID) REFERENCES SUPPLIERS (SUP_ID))";
    
    try (Statement stmt = con.createStatement()) {
      int ret = stmt.executeUpdate(createString);
      int hello = ret;
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }

  public void populateTable() throws SQLException {
    try (Statement stmt = con.createStatement()) {
      stmt.executeUpdate("insert into COFFEES " +
                         "values('Colombian', 00101, 7.99, 0, 0)");
      stmt.executeUpdate("insert into COFFEES " +
                         "values('French_Roast', 00049, 8.99, 0, 0)");
      stmt.executeUpdate("insert into COFFEES " +
                         "values('Espresso', 00150, 9.99, 0, 0)");
      stmt.executeUpdate("insert into COFFEES " +
                         "values('Colombian_Decaf', 00101, 8.99, 0, 0)");
      stmt.executeUpdate("insert into COFFEES " +
                         "values('French_Roast_Decaf', 00049, 9.99, 0, 0)");
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }


  public void updateCoffeeSales(HashMap<String, Integer> salesForWeek) throws SQLException {
    String updateString =
      "update COFFEES set SALES = ? where COF_NAME = ?";
    String updateStatement =
      "update COFFEES set TOTAL = TOTAL + ? where COF_NAME = ?";

    try (PreparedStatement updateSales = con.prepareStatement(updateString);
         PreparedStatement updateTotal = con.prepareStatement(updateStatement))
    
    {
      con.setAutoCommit(false);
      for (Map.Entry<String, Integer> e : salesForWeek.entrySet()) {
        updateSales.setInt(1, e.getValue().intValue());
        updateSales.setString(2, e.getKey());
        updateSales.executeUpdate();

        updateTotal.setInt(1, e.getValue().intValue());
        updateTotal.setString(2, e.getKey());
        updateTotal.executeUpdate();
        con.commit();
      }
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
      if (con != null) {
        try {
          System.err.print("Transaction is being rolled back");
          con.rollback();
        } catch (SQLException excep) {
          JDBCTutorialUtilities.printSQLException(excep);
        }
      }
    }
  }

  public void modifyPrices(float percentage) throws SQLException {
    try (Statement stmt =
      con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
      ResultSet uprs = stmt.executeQuery("SELECT * FROM COFFEES");
      while (uprs.next()) {
        float f = uprs.getFloat("PRICE");
        uprs.updateFloat("PRICE", f * percentage);
        uprs.updateRow();
      }
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }

  public void modifyPricesByPercentage(
    String coffeeName,
    float priceModifier,
    float maximumPrice) throws SQLException {
	  
    con.setAutoCommit(false);
    
    ResultSet rs = null;
    String priceQuery = "SELECT COF_NAME, PRICE FROM COFFEES " +
                        "WHERE COF_NAME = ?";
    String updateQuery = "UPDATE COFFEES SET PRICE = ? " +
                         "WHERE COF_NAME = ?";
    try (PreparedStatement getPrice = con.prepareStatement(priceQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
         PreparedStatement updatePrice = con.prepareStatement(updateQuery))
    {
      Savepoint save1 = con.setSavepoint();
      getPrice.setString(1, coffeeName);
      if (!getPrice.execute()) {
        System.out.println("Could not find entry for coffee named " + coffeeName);
      } else {
        rs = getPrice.getResultSet();
        rs.first();
        float oldPrice = rs.getFloat("PRICE");
        float newPrice = oldPrice + (oldPrice * priceModifier);
        System.out.printf("Old price of %s is $%.2f%n", coffeeName, oldPrice);
        System.out.printf("New price of %s is $%.2f%n", coffeeName, newPrice);
        System.out.println("Performing update...");
        updatePrice.setFloat(1, newPrice);
        updatePrice.setString(2, coffeeName);
        updatePrice.executeUpdate();
        System.out.println("\nCOFFEES table after update:");
        CoffeesTable.viewTable(con);
        if (newPrice > maximumPrice) {
          System.out.printf("The new price, $%.2f, is greater " +
                            "than the maximum price, $%.2f. " +
                            "Rolling back the transaction...%n",
                            newPrice, maximumPrice);
          con.rollback(save1);
          System.out.println("\nCOFFEES table after rollback:");
          CoffeesTable.viewTable(con);
        }
        con.commit();
      }
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    } finally {
      con.setAutoCommit(true);
    }
  }

  public void insertRow(String coffeeName, int supplierID, float price,
                        int sales, int total) throws SQLException {
    
    try (Statement stmt =
          con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE))
    {      
      ResultSet uprs = stmt.executeQuery("SELECT * FROM COFFEES");
      uprs.moveToInsertRow();
      uprs.updateString("COF_NAME", coffeeName);
      uprs.updateInt("SUP_ID", supplierID);
      uprs.updateFloat("PRICE", price);
      uprs.updateInt("SALES", sales);
      uprs.updateInt("TOTAL", total);

      uprs.insertRow();
      uprs.beforeFirst();

    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }

  public void batchUpdate() throws SQLException {
	  System.out.println("+batchUpdate");
    con.setAutoCommit(false);
    try (Statement stmt = con.createStatement()) {

      stmt.addBatch("INSERT INTO COFFEES " +
                    "VALUES('Arabica', 101, 7.99, 0, 0)");
      
      stmt.addBatch("INSERT INTO COFFEES " +
              "VALUES('Robusta', 49, 8.99, 0, 0)");
      
      stmt.addBatch("INSERT INTO COFFEES " +
              "VALUES('Liberica', 150, 9.99, 0, 0)");
      
      stmt.addBatch("INSERT INTO COFFEES " +
              "VALUES('Excelsa', 101, 8.99, 0, 0)");
      
      stmt.addBatch("INSERT INTO COFFEES " +
              "VALUES('Washed', 049, 9.99, 0, 0)"); 
      
      
//    	stmt.addBatch("INSERT INTO COFFEES " +
//                "VALUES('hello1', 101, 7.99, 0, 0)");
//  
//	  stmt.addBatch("INSERT INTO COFFEES " +
//	          "VALUES('hello2', 49, 8.99, 0, 0)");
//	  
//	  stmt.addBatch("INSERT INTO COFFEES " +
//	          "VALUES('hello3', 150, 9.99, 0, 0)");
//	  
//	  stmt.addBatch("INSERT INTO COFFEES " +
//	          "VALUES('hello4', 101, 8.99, 0, 0)");
//	  
//	  stmt.addBatch("INSERT INTO COFFEES " +
//	          "VALUES('hello5', 049, 9.99, 0, 0)");     

      int[] updateCounts = stmt.executeBatch();
      con.commit();
    } catch (BatchUpdateException b) {
      JDBCTutorialUtilities.printBatchUpdateException(b);
    } catch (SQLException ex) {
      JDBCTutorialUtilities.printSQLException(ex);
    } finally {
      con.setAutoCommit(true);
      System.out.println("-batchUpdate");
    }
  }
  
  public static void viewTable(Connection con) throws SQLException {
    String query = "select COF_NAME, SUP_ID, PRICE, SALES, TOTAL from COFFEES";
    try (Statement stmt = con.createStatement()) {
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        String coffeeName = rs.getString("COF_NAME");
        int supplierID = rs.getInt("SUP_ID");
        float price = rs.getFloat("PRICE");
        int sales = rs.getInt("SALES");
        int total = rs.getInt("TOTAL");
        System.out.println(coffeeName + ", " + supplierID + ", " + price +
                           ", " + sales + ", " + total);
      }
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }

  public static void alternateViewTable(Connection con) throws SQLException {
    String query = "select COF_NAME, SUP_ID, PRICE, SALES, TOTAL from COFFEES";
    try (Statement stmt = con.createStatement()) {
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        String coffeeName = rs.getString(1);
        int supplierID = rs.getInt(2);
        float price = rs.getFloat(3);
        int sales = rs.getInt(4);
        int total = rs.getInt(5);
        System.out.println(coffeeName + ", " + supplierID + ", " + price +
                           ", " + sales + ", " + total);
      }
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }
  
  public Set<String> getKeys() throws SQLException {
    HashSet<String> keys = new HashSet<String>();
    String query = "select COF_NAME from COFFEES";
    try (Statement stmt = con.createStatement()) {
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        keys.add(rs.getString(1));
      }
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
    return keys;
  }

  public void dropTable() throws SQLException {
    try (Statement stmt = con.createStatement()) {
      if (this.dbms.equals("mysql")) {
        stmt.executeUpdate("DROP TABLE IF EXISTS COFFEES");
      } else if (this.dbms.equals("derby")) {
        stmt.executeUpdate("DROP TABLE COFFEES");
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

      JDBCTutorialUtilities.initializeTables(myConnection,
                                             myJDBCTutorialUtilities.dbName,
                                             myJDBCTutorialUtilities.dbms);

      CoffeesTable myCoffeeTable =
        new CoffeesTable(myConnection, myJDBCTutorialUtilities.dbName,
                         myJDBCTutorialUtilities.dbms);

      System.out.println("\nContents of COFFEES table:");
      CoffeesTable.viewTable(myConnection);

      System.out.println("\nRaising coffee prices by 25%");
      myCoffeeTable.modifyPrices(1.25f);

      System.out.println("\nInserting a new row:");
      myCoffeeTable.insertRow("Kona", 150, 10.99f, 0, 0);
      CoffeesTable.viewTable(myConnection);

      System.out.println("\nUpdating sales of coffee per week:");
      HashMap<String, Integer> salesCoffeeWeek =
        new HashMap<String, Integer>();
      salesCoffeeWeek.put("Colombian", 175);
      salesCoffeeWeek.put("French_Roast", 150);
      salesCoffeeWeek.put("Espresso", 60);
      salesCoffeeWeek.put("Colombian_Decaf", 155);
      salesCoffeeWeek.put("French_Roast_Decaf", 90);
      myCoffeeTable.updateCoffeeSales(salesCoffeeWeek);
      CoffeesTable.viewTable(myConnection);

      System.out.println("\nModifying prices by percentage");

      myCoffeeTable.modifyPricesByPercentage("Colombian", 0.10f, 9.00f);
      
      System.out.println("\nCOFFEES table after modifying prices by percentage:");
      
      myCoffeeTable.viewTable(myConnection);

      System.out.println("\nPerforming batch updates; adding new coffees");
      myCoffeeTable.batchUpdate();
      myCoffeeTable.viewTable(myConnection);

//      System.out.println("\nDropping Coffee and Suplliers table:");
//      
//      myCoffeeTable.dropTable();
//      mySuppliersTable.dropTable();

    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    } finally {
      JDBCTutorialUtilities.closeConnection(myConnection);
    }
  }
}
