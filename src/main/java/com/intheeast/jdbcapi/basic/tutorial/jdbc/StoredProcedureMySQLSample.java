package com.intheeast.jdbcapi.basic.tutorial.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class StoredProcedureMySQLSample {

  private String dbName;
  private Connection con;
  private String dbms;

  public StoredProcedureMySQLSample(Connection connArg, String dbName,
                                    String dbmsArg) {
    super();
    this.con = connArg;
    this.dbName = dbName;
    this.dbms = dbmsArg;
  }
  
  public void createProcedureRaisePrice() throws SQLException {
    
    String queryDrop = "DROP PROCEDURE IF EXISTS RAISE_PRICE";

    String createProcedure =
        "create procedure RAISE_PRICE(IN coffeeName varchar(32), IN maximumPercentage float, INOUT newPrice numeric(10,2)) " +
          "begin " +
            "main: BEGIN " +
              "declare maximumNewPrice numeric(10,2); " +
              "declare oldPrice numeric(10,2); " +
              "select COFFEES.PRICE into oldPrice " +
                "from COFFEES " +
                "where COFFEES.COF_NAME = coffeeName; " +
              "set maximumNewPrice = oldPrice * (1 + maximumPercentage); " +
              "if (newPrice > maximumNewPrice) " +
                "then set newPrice = maximumNewPrice; " +
              "end if; " +
              "if (newPrice <= oldPrice) " +
                "then set newPrice = oldPrice;" +
                "leave main; " +
              "end if; " +
              "update COFFEES " +
                "set COFFEES.PRICE = newPrice " +
                "where COFFEES.COF_NAME = coffeeName; " +
              "select newPrice; " +
            "END main; " +
          "end";

    try (Statement stmtDrop = con.createStatement()) {
      System.out.println("Calling DROP PROCEDURE");
      stmtDrop.execute(queryDrop);
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }

    try (Statement stmt = con.createStatement()) {
      stmt.executeUpdate(createProcedure);
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }
    
  public void createProcedureGetSupplierOfCoffee() throws SQLException {

    String queryDrop = "DROP PROCEDURE IF EXISTS GET_SUPPLIER_OF_COFFEE";

    String createProcedure =
        "create procedure GET_SUPPLIER_OF_COFFEE(IN coffeeName varchar(32), OUT supplierName varchar(40)) " +
          "begin " +
            "select SUPPLIERS.SUP_NAME into supplierName " +
              "from SUPPLIERS, COFFEES " +
              "where SUPPLIERS.SUP_ID = COFFEES.SUP_ID " +
              "and coffeeName = COFFEES.COF_NAME; " +
            "select supplierName; " +
          "end";

    try (Statement stmtDrop = con.createStatement()) {
      System.out.println("Calling DROP PROCEDURE");
      stmtDrop.execute(queryDrop);
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }

    try (Statement stmt = con.createStatement()) {
      stmt.executeUpdate(createProcedure);
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }
  

  public void createProcedureShowSuppliers() throws SQLException {
    
    String queryDrop = "DROP PROCEDURE IF EXISTS SHOW_SUPPLIERS";

    String createProcedure =
        "create procedure SHOW_SUPPLIERS() " +
          "begin " +
            "select SUPPLIERS.SUP_NAME, COFFEES.COF_NAME " +
              "from SUPPLIERS, COFFEES " +
              "where SUPPLIERS.SUP_ID = COFFEES.SUP_ID " +
              "order by SUP_NAME; " +
          "end";

    try (Statement stmtDrop = con.createStatement()) {
      System.out.println("Calling DROP PROCEDURE");
      stmtDrop.execute(queryDrop);
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    } 

    try (Statement stmt = con.createStatement()) {
      stmt.executeUpdate(createProcedure);
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }

  public void runStoredProcedures(String coffeeNameArg, float maximumPercentageArg, float newPriceArg) throws SQLException {
    CallableStatement cs = null;
    try {
      System.out.println("\nCalling the procedure GET_SUPPLIER_OF_COFFEE");
      cs = this.con.prepareCall("{call GET_SUPPLIER_OF_COFFEE(?, ?)}");
      cs.setString(1, coffeeNameArg);
      cs.registerOutParameter(2, Types.VARCHAR);
      cs.executeQuery();
      String supplierName = cs.getString(2);
      if (supplierName != null) {
        System.out.println("\nSupplier of the coffee " + coffeeNameArg + ": " + supplierName);          
      } else {
        System.out.println("\nUnable to find the coffee " + coffeeNameArg);        
      }
      System.out.println("\nCalling the procedure SHOW_SUPPLIERS");
      cs = this.con.prepareCall("{call SHOW_SUPPLIERS}");
      ResultSet rs = cs.executeQuery();
      while (rs.next()) {
        String supplier = rs.getString("SUP_NAME");
        String coffee = rs.getString("COF_NAME");
        System.out.println(supplier + ": " + coffee);
      }
      System.out.println("\nContents of COFFEES table before calling RAISE_PRICE:");
      CoffeesTable.viewTable(this.con);
      System.out.println("\nCalling the procedure RAISE_PRICE");
      cs = this.con.prepareCall("{call RAISE_PRICE(?,?,?)}");
      cs.setString(1, coffeeNameArg);
      cs.setFloat(2, maximumPercentageArg);
      cs.registerOutParameter(3, Types.NUMERIC);
      cs.setFloat(3, newPriceArg);
      cs.execute();
      System.out.println("\nValue of newPrice after calling RAISE_PRICE: " + cs.getFloat(3));
      System.out.println("\nContents of COFFEES table after calling RAISE_PRICE:");
      CoffeesTable.viewTable(this.con);
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    } finally {
      if (cs != null) { cs.close(); }
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
      myConnection = myJDBCTutorialUtilities.getConnectionToDatabase();

      StoredProcedureMySQLSample myStoredProcedureSample =
        new StoredProcedureMySQLSample(myConnection,
                                       myJDBCTutorialUtilities.dbName,
                                       myJDBCTutorialUtilities.dbms);

//      JDBCTutorialUtilities.initializeTables(myConnection,
//                                             myJDBCTutorialUtilities.dbName,
//                                             myJDBCTutorialUtilities.dbms);


      System.out.println("\nCreating SHOW_SUPPLIERS stored procedure");
      myStoredProcedureSample.createProcedureShowSuppliers();
      
      System.out.println("\nCreating GET_SUPPLIER_OF_COFFEE stored procedure");
      myStoredProcedureSample.createProcedureGetSupplierOfCoffee();

      System.out.println("\nCreating RAISE_PRICE stored procedure");
      myStoredProcedureSample.createProcedureRaisePrice();
      

      System.out.println("\nCalling all stored procedures:");
      myStoredProcedureSample.runStoredProcedures("Colombian", 0.10f, 19.99f);

    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    } finally {
      JDBCTutorialUtilities.closeConnection(myConnection);
    }
  }
}
