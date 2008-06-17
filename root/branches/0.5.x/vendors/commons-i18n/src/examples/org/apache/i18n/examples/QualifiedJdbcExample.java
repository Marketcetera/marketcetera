/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.i18n.examples;

import org.apache.commons.i18n.MessageManager;
import org.apache.commons.i18n.JdbcMessageProvider;
import org.apache.commons.i18n.LocalizedRuntimeException;
import org.apache.commons.i18n.bundles.TextBundle;
import org.apache.commons.i18n.bundles.ErrorBundle;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.Locale;
import java.util.Properties;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This example shows
 * a) how to use qualified message providers, instead of searching through all available providers.
 * b) how to use i18n for messages stored in databases. For the sake of the example, we will create
 * an in memory database holding the i18n messages using HSQLDB.
 * @author Mattias Jiderhamn
 * @see <a href="http://hsqldb.org/">HSQLDB</a>
 */
public class QualifiedJdbcExample {
    public static void main(String[] args) throws Exception {
        /////////////////////////////////////////////////////
        // Prepare example
        /////////////////////////////////////////////////////

        // Set up in-memory data for the sake of the example
        prepareTables();

        /////////////////////////////////////////////////////
        // Example of initialization
        /////////////////////////////////////////////////////

        // We can initialize JDBC message provider using a properties file
        Properties props = new Properties();
        props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("i18n-jdbc.properties"));
        MessageManager.addMessageProvider("messages", new JdbcMessageProvider(props));

        // We can also initialize JDBC message provider by providing connection (or DataSource)
        Connection conn = getNewConnection();
        MessageManager.addMessageProvider("errors", new JdbcMessageProvider(conn, "errors", "id", "language"));
        conn.close();

        /////////////////////////////////////////////////////
        // Example of usage
        /////////////////////////////////////////////////////

        // Simulate the locale of the current user in a multi-user environment
        // such as a web application
        Locale currentUsersLocale = new Locale("sv"); // Assume Swedish

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // Get i18n text qualifying the message source (i.e. the MessageProvider) to use; messages
            TextBundle textBundle = new TextBundle("messages", "enterFirstName");
            System.out.println(textBundle.getText(currentUsersLocale) + ":");
            String firstName = reader.readLine();

            textBundle = new TextBundle("messages", "enterLastName");
            System.out.println(textBundle.getText(currentUsersLocale) + ":");
            String lastName = reader.readLine();

            validateNames(firstName, lastName);
        }
        catch(LocalizedRuntimeException lrex) {
            // Retrieve the detailed localized error message
            ErrorBundle errorMessage = lrex.getErrorMessage();

            // Print summary and details using the current users locale
            System.out.println("-- " + errorMessage.getSummary(currentUsersLocale) + " --");
            System.out.println(errorMessage.getDetails(currentUsersLocale));
        }
    }

    private static void validateNames(String firstname, String lastname) {
        if(firstname.equals(lastname))
            throw new LocalizedRuntimeException(new ErrorBundle("errors", "identicalNames"));
    }

    ///////////////////////////////////////////////////////////////////////
    // Utility methods for the example
    ///////////////////////////////////////////////////////////////////////

    /**
     * Create connection to in-memory HSQLDB database
     * @return
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    private static Connection getNewConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.hsqldb.jdbcDriver"); // Load HSQLDB database driver
        return DriverManager.getConnection("jdbc:hsqldb:.", "sa", ""); // Connect to in-memory database
    }

    /**
     * Create tables and insert messages
     */
    private static void prepareTables() throws ClassNotFoundException, SQLException {
        Connection conn = getNewConnection();
        Statement stmt = conn.createStatement();
        stmt.execute(
                "CREATE TABLE messages ( " +
                "  'id' VARCHAR(30), " +
                "  'language' VARCHAR(2), " +
                "  'text' VARCHAR(100)" +
                ")");
        stmt.execute("INSERT INTO messages VALUES ('enterFirstName', 'en', 'Please enter your first name')");
        stmt.execute("INSERT INTO messages VALUES ('enterLastName', 'en', 'Please enter your last name')");
        stmt.execute("INSERT INTO messages VALUES ('enterFirstName', 'sv', 'Vänligen ange ditt förnamn')");
        stmt.execute("INSERT INTO messages VALUES ('enterLastName', 'sv', 'Vänligen ange ditt efternamn')");
        stmt.execute(
                "CREATE TABLE errors ( " +
                "  'id' VARCHAR(30), " +
                "  'language' VARCHAR(2), " +
                "  'summary' VARCHAR(100), " +
                "  'details' VARCHAR(100) " +
                ")");
        stmt.execute("INSERT INTO errors VALUES (" +
                "  'identicalNames', 'en', " +
                "  'Error! Identical names.', 'You entered the same name as both first name and last name'" +
                ")");
        stmt.execute("INSERT INTO errors VALUES (" +
                "  'identicalNames', 'sv', " +
                "  'Fel! Identiska namn.', 'Du angav samma namn som både förnamn och efternamn'" +
                ")");
        stmt.close();
        conn.close();
    }
}
