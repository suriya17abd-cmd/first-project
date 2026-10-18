package com.qrid.config;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public final class Database {
    private static final Properties P = new Properties();
    static {
        try (InputStream in = Database.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (in == null) throw new IllegalStateException("application.properties not found");
            P.load(in);
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) { throw new ExceptionInInitializerError(e); }
    }
    private Database() {}
    public static Connection getConnection() throws SQLException {
        String url = env("QRID_DB_URL", "db.url");
        String user = env("QRID_DB_USER", "db.username");
        String pass = env("QRID_DB_PASSWORD", "db.password");
        return DriverManager.getConnection(url, user, pass);
    }
    public static String property(String key) { return P.getProperty(key); }
    private static String env(String name, String key) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? P.getProperty(key, "") : value;
    }
}
