/*
 * Copyright 2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.commons.i18n;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * @author Mattias Jiderhamn
 */
public class JdbcMessageProviderTest extends MessageProviderTestBase {

    private static Connection getNewConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:hsqldb:.", "sa", ""); // Connect to in-memory database
    }

    public void setUp() throws Exception {
        super.setUp();

        Class.forName("org.hsqldb.jdbcDriver"); // Load HSQLDB database driver
        Connection conn = getNewConnection();
        Statement stmt = conn.createStatement();
        stmt.execute(
                "CREATE TABLE messages ( " +
                "  'id' VARCHAR(30), " +
                "  'language' VARCHAR(2), " +
                "  'title' VARCHAR(100), " +
                "  'text' VARCHAR(100)" +
                ")");
        stmt.execute(
                "INSERT INTO messages VALUES (" +
                "  'helloWorld', 'en', " +
                "  'Hello World', 'I wish you a merry christmas!'" +
                ")"
        );
        stmt.execute(
                "INSERT INTO messages VALUES (" +
                "  'helloWorld', 'de', " +
                "  'Hallo Welt', 'Ich wünsche Dir alles Gute und ein frohes Fest!'" +
                ")"
        );
        stmt.close();
        conn.close();
    }

    public void tearDown() throws Exception {
        super.tearDown();
        Connection conn = getNewConnection();
        conn.createStatement().execute(
                "DROP TABLE messages"
        );
        conn.close();
    }

    public void testConstructors() throws Exception {
        // Connection constructor
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:.", "sa", ""); // Connect to in-memory database
        JdbcMessageProvider jdbcMessageProvider = new JdbcMessageProvider(conn, "messages", "id", "language");
        conn.close();
        assertEquals("Hello World", jdbcMessageProvider.getText("helloWorld", "title", Locale.ENGLISH));

        // DataSource constructor
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:hsqldb:.");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        jdbcMessageProvider = new JdbcMessageProvider(dataSource, "messages", "id", "language");
        assertEquals("Hello World", jdbcMessageProvider.getText("helloWorld", "title", Locale.ENGLISH));

        // Map/Properties constructor
        Properties props = new Properties();
        props.setProperty("jdbc.connect.driver", "org.hsqldb.jdbcDriver");
        props.setProperty("jdbc.connect.url", "jdbc:hsqldb:.");
        props.setProperty("jdbc.connect.login", "sa");
        props.setProperty("jdbc.connect.password", "");

        props.setProperty("jdbc.sql.table", "messages");
        props.setProperty("jdbc.sql.key.column", "id");
        props.setProperty("jdbc.sql.locale.column", "language");
        jdbcMessageProvider = new JdbcMessageProvider(props);
        assertEquals("Hello World", jdbcMessageProvider.getText("helloWorld", "title", Locale.ENGLISH));

        // Test install
        MessageManager.addMessageProvider("messages", jdbcMessageProvider);
        assertEquals("Hello World", MessageManager.getText("helloWorld", "title", null, Locale.ENGLISH));
    }

    public void testGetText() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:.", "sa", ""); // Connect to in-memory database
        JdbcMessageProvider jdbcMessageProvider = new JdbcMessageProvider(conn, "messages", "id", "language");
        conn.close();

        // Explicit default locale
        assertEquals("Hello World", jdbcMessageProvider.getText("helloWorld", "title", Locale.ENGLISH));
        assertEquals("I wish you a merry christmas!", jdbcMessageProvider.getText("helloWorld", "text", Locale.ENGLISH));

        // Default locale with country
        assertEquals("Hello World", jdbcMessageProvider.getText("helloWorld", "title", Locale.US));
        assertEquals("I wish you a merry christmas!", jdbcMessageProvider.getText("helloWorld", "text", Locale.US));

        // Default locale with country and variant
        Locale scottish = new Locale("en", "", "scottish");
        assertEquals("Hello World", jdbcMessageProvider.getText("helloWorld", "title", scottish));
        assertEquals("I wish you a merry christmas!", jdbcMessageProvider.getText("helloWorld", "text", scottish));

        assertEquals("Hallo Welt", jdbcMessageProvider.getText("helloWorld", "title", Locale.GERMAN));
        assertEquals("Ich wünsche Dir alles Gute und ein frohes Fest!", jdbcMessageProvider.getText("helloWorld", "text", Locale.GERMAN));

        // Default locale with country
        assertEquals("Hallo Welt", jdbcMessageProvider.getText("helloWorld", "title", Locale.GERMANY));
        assertEquals("Ich wünsche Dir alles Gute und ein frohes Fest!", jdbcMessageProvider.getText("helloWorld", "text", Locale.GERMANY));

        // Test use of defaule
        assertEquals("Hello World", jdbcMessageProvider.getText("helloWorld", "title", Locale.JAPANESE));
        assertEquals("I wish you a merry christmas!", jdbcMessageProvider.getText("helloWorld", "text", Locale.JAPANESE));

        // Test non-existent
        assertNull(jdbcMessageProvider.getText("helloWorld", "foobar", Locale.ENGLISH));
        assertNull(jdbcMessageProvider.getText("foo", "bar", Locale.ENGLISH));
    }

    public void testGetEntries() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:hsqldb:.", "sa", ""); // Connect to in-memory database
        JdbcMessageProvider jdbcMessageProvider = new JdbcMessageProvider(conn, "messages", "id", "language");
        conn.close();

        // Explicit default locale
        Map entries = jdbcMessageProvider.getEntries("helloWorld", Locale.ENGLISH);
        assertEquals("No of entries", 2, entries.size());
        assertEquals("Hello World", (String)entries.get("title"));
        assertEquals("I wish you a merry christmas!", (String)entries.get("text"));

        // Default locale with country
        entries = jdbcMessageProvider.getEntries("helloWorld", Locale.US);
        assertEquals("No of entries", 2, entries.size());
        assertEquals("Hello World", (String)entries.get("title"));
        assertEquals("I wish you a merry christmas!", (String)entries.get("text"));

        // Default locale with country and variant
        Locale scottish = new Locale("en", "", "scottish");
        entries = jdbcMessageProvider.getEntries("helloWorld", scottish);
        assertEquals("No of entries", 2, entries.size());
        assertEquals("Hello World", (String)entries.get("title"));
        assertEquals("I wish you a merry christmas!", (String)entries.get("text"));

        entries = jdbcMessageProvider.getEntries("helloWorld", Locale.GERMAN);
        assertEquals("No of entries", 2, entries.size());
        assertEquals("Hallo Welt", (String)entries.get("title"));
        assertEquals("Ich wünsche Dir alles Gute und ein frohes Fest!", (String)entries.get("text"));

        // Default locale with country
        entries = jdbcMessageProvider.getEntries("helloWorld", Locale.GERMANY);
        assertEquals("No of entries", 2, entries.size());
        assertEquals("Hallo Welt", (String)entries.get("title"));
        assertEquals("Ich wünsche Dir alles Gute und ein frohes Fest!", (String)entries.get("text"));

        // Test use of defaule
        entries = jdbcMessageProvider.getEntries("helloWorld", Locale.JAPANESE);
        assertEquals("No of entries", 2, entries.size());
        assertEquals("Hello World", (String)entries.get("title"));
        assertEquals("I wish you a merry christmas!", (String)entries.get("text"));
    }
}
