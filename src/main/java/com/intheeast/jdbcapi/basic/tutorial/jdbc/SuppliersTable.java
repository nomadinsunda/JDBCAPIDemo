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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SuppliersTable {

  private String dbName;
  private Connection con;
  private String dbms;

  public SuppliersTable(Connection connArg, String dbNameArg, String dbmsArg) {
    super();
    this.con = connArg;
    this.dbName = dbNameArg;
    this.dbms = dbmsArg;
  }

  public void createTable() throws SQLException {
    String createString =
      "create table SUPPLIERS " + "(SUP_ID integer NOT NULL, " +
      "SUP_NAME varchar(40) NOT NULL, " + "STREET varchar(40) NOT NULL, " +
      "CITY varchar(20) NOT NULL, " + "STATE char(2) NOT NULL, " +
      "ZIP char(5), " + "PRIMARY KEY (SUP_ID))";
    
    
    try (Statement stmt = con.createStatement()) {
      stmt.executeUpdate(createString);
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }

  public void dropTable() throws SQLException {    
    try (Statement stmt = con.createStatement()) {
      if (this.dbms.equals("mysql")) {
        System.out.println("Dropping table SUPPLIERS from MySQL");
        stmt.executeUpdate("DROP TABLE IF EXISTS SUPPLIERS");
      } else if (this.dbms.equals("derby")) {
        stmt.executeUpdate("DROP TABLE SUPPLIERS");
      }
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }

  public void populateTable() throws SQLException {
    try (Statement stmt = con.createStatement()) {
      stmt.executeUpdate("insert into SUPPLIERS " +
                         "values(49, 'Superior Coffee', '1 Party Place', " +
                         "'Mendocino', 'CA', '95460')");
      stmt.executeUpdate("insert into SUPPLIERS " +
                         "values(101, 'Acme, Inc.', '99 Market Street', " +
                         "'Groundsville', 'CA', '95199')");
      stmt.executeUpdate("insert into SUPPLIERS " +
                         "values(150, 'The High Ground', '100 Coffee Lane', " +
                         "'Meadows', 'CA', '93966')");
      stmt.executeUpdate("insert into SUPPLIERS " +
              "values(456, 'Restaurant Supplies, Inc.', '200 Magnolia Street', 'Meadows', 'CA', '93966')");
      
      stmt.executeUpdate("insert into SUPPLIERS " +
              "values(927, 'Professional Kitchen', '300 Daisy Avenue', 'Groundsville', 'CA', '95199')");
      
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }

  public void viewSuppliers() throws SQLException {
    
    String query = "select SUP_NAME, SUP_ID from SUPPLIERS";
    try (Statement stmt = con.createStatement()) {
      ResultSet rs = stmt.executeQuery(query);
      System.out.println("Suppliers and their ID Numbers:");
      while (rs.next()) {
        String s = rs.getString("SUP_NAME");
        int n = rs.getInt("SUP_ID");
        System.out.println(s + "   " + n);
      }
    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    }
  }

  public static void viewTable(Connection con) throws SQLException {
    String query =
      "select SUP_ID, SUP_NAME, STREET, CITY, STATE, ZIP from SUPPLIERS";
    try (Statement stmt = con.createStatement()) {
      ResultSet rs = stmt.executeQuery(query);
      while (rs.next()) {
        int supplierID = rs.getInt("SUP_ID");
        String supplierName = rs.getString("SUP_NAME");
        String street = rs.getString("STREET");
        String city = rs.getString("CITY");
        String state = rs.getString("STATE");
        String zip = rs.getString("ZIP");
        System.out.println(supplierName + "(" + supplierID + "): " + street +
                           ", " + city + ", " + state + ", " + zip);
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
    	// tcp socket을 연결을 설정합니다(Client -> Database) : 
    	// 연결 설정 해제: 하나의 트랜잭션이 완료되면 커넥션 해제
    	// Hicari CP : socket 생성한 후에, 삭제를 하지 않고 재사용!!! 
      myConnection = myJDBCTutorialUtilities.getConnection();

      // Java DB does not have an SQL create database command; it does require createDatabase
//      JDBCTutorialUtilities.createDatabase(myConnection,
//                                           myJDBCTutorialUtilities.dbName,
//                                           myJDBCTutorialUtilities.dbms);
//
//      JDBCTutorialUtilities.initializeTables(myConnection,
//                                             myJDBCTutorialUtilities.dbName,
//                                             myJDBCTutorialUtilities.dbms);
      
      System.out.println("\nContents of SUPPLIERS table:");
      
      SuppliersTable.viewTable(myConnection);

    } catch (SQLException e) {
      JDBCTutorialUtilities.printSQLException(e);
    } finally {
      JDBCTutorialUtilities.closeConnection(myConnection);
    }
  }
}
