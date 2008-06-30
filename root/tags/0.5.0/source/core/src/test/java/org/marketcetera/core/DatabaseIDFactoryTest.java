package org.marketcetera.core;

import junit.framework.Test;
import junit.framework.TestCase;

import java.net.InetAddress;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.io.File;

/**
 * Tests the database ID factory to make sure that if the DB is not there
 * we still get a nice fail-over {@link InMemoryIDFactory}
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class DatabaseIDFactoryTest extends TestCase {
    public static String SQL_CONNECTION_URL = "jdbc:hsqldb:mem:junit";
    public static String SQL_DRIVER = "org.hsqldb.jdbcDriver";
//    public static String SQL_DRIVER = "com.mysql.jdbc.Driver";
//    public static String SQL_CONNECTION_URL = "jdbc:mysql://localhost/test";
    public static String SQL_USER = "sa";
    public static String SQL_PWD = "";
//    public static String SQL_USER = "test";
//    public static String SQL_PWD = "test";

    public DatabaseIDFactoryTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        new File("junit").delete();
        new File("junit").deleteOnExit();
        return new MarketceteraTestSuite(DatabaseIDFactoryTest.class);
    }

    /** Verify that when a DB is inaccessible we still get an in-memory set of ids */
    public void testFailThroughInMemory() throws Exception {
        DatabaseIDFactory factory = new DatabaseIDFactory(SQL_CONNECTION_URL, SQL_DRIVER, SQL_USER, SQL_PWD, "notable", "nocol", 13);
        boolean dbInaccessble = false;
        try {
            factory.init();
        } catch(Exception ignored ) {
            dbInaccessble = true;
            LoggerAdapter.debug("expected exception: " + ignored.getMessage(), this);
        }
        assertTrue("for some reason db access didn't error out", dbInaccessble);
        String next = factory.getNext();
        assertNotNull(next);
        assertTrue("doesn't look like an inMemoryIDFactory id: "+next, next.contains(InetAddress.getLocalHost().toString()));
        String previous = next;
        for(int i=0;i < 20; i++){
            String cur = factory.getNext();
            assertNotNull(cur);
            assertNotSame("getting same ids in a row", cur, previous);
            previous = cur;
        }
    }



    /** need to have a valid mySQL db in order to run this code */
    public void disabled_testDatabaseIDs() throws Exception {
        DatabaseIDFactory factory = new DatabaseIDFactory(SQL_CONNECTION_URL, SQL_DRIVER, SQL_USER, SQL_PWD,
                                                          DatabaseIDFactory.TABLE_NAME, DatabaseIDFactory.COL_NAME, 13);

        // create the table
        Class.forName(SQL_DRIVER);
        Connection dbConnection = DriverManager.getConnection(SQL_CONNECTION_URL, SQL_USER, SQL_PWD);
        Statement stmt = dbConnection.createStatement();
        stmt.execute("drop table if exists " + DatabaseIDFactory.TABLE_NAME);
        stmt.execute("create table "+DatabaseIDFactory.TABLE_NAME +"(id int, "+DatabaseIDFactory.COL_NAME + " int not null default 0)");

        factory.init();
        String next = factory.getNext();
        assertNotNull(next);
        assertFalse("looks like we got an inMemoryIDFactory id: "+next, next.contains(InetAddress.getLocalHost().toString()));
        String previous = next;
        for(int i=0;i < DatabaseIDFactory.NUM_IDS_GRABBED*2; i++){
            String cur = factory.getNext();
            assertNotNull(cur);
            assertNotSame("getting same ids in a row", cur, previous);
            assertTrue((new Integer(cur) > new Integer(previous)));
            previous = cur;
        }
    }
}
