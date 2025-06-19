package com.intheeast.jdbcapi.basic.tutorial.jdbc.util;

import java.nio.file.*;
import java.sql.*;
import java.util.stream.Collectors;

public class SqlScriptRunner {

    public static void runScript(Connection connection, Path scriptPath, String delimiter) throws Exception {
        String sql = Files.readAllLines(scriptPath)
                          .stream()
                          .map(String::trim)
                          .filter(line -> !line.startsWith("--") && !line.isEmpty())
                          .collect(Collectors.joining(" "));

        String[] statements = sql.split(delimiter);

        try (Statement stmt = connection.createStatement()) {
            for (String statement : statements) {
                String trimmed = statement.trim();
                if (!trimmed.isEmpty()) {
                    stmt.execute(trimmed);
                    System.out.println("[실행됨] " + trimmed);
                }
            }
        }
    }
}
