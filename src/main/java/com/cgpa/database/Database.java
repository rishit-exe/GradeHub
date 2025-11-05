package com.cgpa.database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Centralized JDBC connection factory that reads properties from classpath file `db.properties`.
 */
public final class Database {
    private static final String PROPERTIES_PATH = "/db.properties";
    private static final Properties PROPERTIES = new Properties();
    // Cache for alternate profile property sets (e.g. db.student.properties, db.faculty.properties)
    private static final java.util.Map<String, Properties> PROFILE_PROPERTIES = new java.util.HashMap<>();

    static {
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Load default properties
            try (InputStream in = Database.class.getResourceAsStream(PROPERTIES_PATH)) {
                if (in == null) {
                    throw new IllegalStateException("Missing db.properties on classpath at " + PROPERTIES_PATH);
                }
                PROPERTIES.load(in);
                PROFILE_PROPERTIES.put("default", PROPERTIES);
            }
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("MySQL JDBC driver not found: " + e.getMessage());
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Failed to load database properties: " + e.getMessage());
        }
    }

    private Database() {}

    public static Connection getConnection() throws SQLException {
        return getConnection((String) null);
    }

    /**
     * Get a connection for a named profile. Looks for a resource `/db.{profile}.properties` on the classpath.
     * If profile is null or corresponding properties file is missing, falls back to the default `db.properties`.
     */
    public static Connection getConnection(String profile) throws SQLException {
    String key = profile == null ? "default" : profile;
    Properties props = PROFILE_PROPERTIES.get(key);
        if (props == null) {
            // attempt to load profile properties file from classpath
            String path = "/db." + profile + ".properties";
            try (InputStream in = Database.class.getResourceAsStream(path)) {
                if (in != null) {
                    Properties p = new Properties();
                    try {
                        p.load(in);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to load " + path, e);
                    }
                    PROFILE_PROPERTIES.put(profile, p);
                    props = p;
                } else {
                    // If a specific profile was requested, fail fast so callers cannot silently
                    // fall back to the default DB (this enforces strict separation).
                    if (profile != null) {
                        throw new IllegalStateException("Missing database properties for profile '" + profile + "'. Please add 'db." + profile + ".properties' to the classpath.");
                    }
                    // No profile requested — use default
                    props = PROFILE_PROPERTIES.get("default");
                }
            } catch (IOException ioe) {
                // Shouldn't happen since we use getResourceAsStream in try-with-resources.
                if (profile != null) {
                    throw new RuntimeException("Failed to load database properties for profile '" + profile + "'", ioe);
                }
                props = PROFILE_PROPERTIES.get("default");
            }
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String pass = props.getProperty("db.password");
        return DriverManager.getConnection(url, user, pass);
    }
} 