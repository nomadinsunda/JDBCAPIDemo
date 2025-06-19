package com.intheeast.jdbcapi.basic.tutorial.jdbc.util;



import java.nio.file.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class TableManager {

    public static void main(String[] args) throws Exception {
        DbConfigLoader config = new DbConfigLoader("db.properties");

        String url = config.getUrl();
        String user = config.getUser();
        String password = config.getPassword();
        String vendor = config.getVendor();
        String delimiter = config.getDelimiter();

        try (Connection con = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ DB 연결 성공: " + url);

            Path base = Paths.get("src/main/resources/sql", vendor);

            SqlScriptRunner.runScript(con, base.resolve("drop-tables.sql"), delimiter);
            SqlScriptRunner.runScript(con, base.resolve("create-tables.sql"), delimiter);
            SqlScriptRunner.runScript(con, base.resolve("populate-tables.sql"), delimiter);
            SqlScriptRunner.runScript(con, base.resolve("create-procedures.sql"), delimiter);
        }
    }
}
