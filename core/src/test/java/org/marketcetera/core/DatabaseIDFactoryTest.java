package org.marketcetera.core;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.persist.PersistTestBase;

/**
 * Tests the database ID factory to make sure that if the DB is not there
 * we still get a nice fail-over {@link InMemoryIDFactory}
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$") //$NON-NLS-1$
public class DatabaseIDFactoryTest {
    private static DataSource mDataSource;

    @BeforeClass
    public static void setUp() throws Exception {
        new File("junit").delete(); //$NON-NLS-1$
        new File("junit").deleteOnExit(); //$NON-NLS-1$
        if(mDataSource == null) {
            mDataSource = (DataSource) PersistTestBase.springSetup(
                    new String[]{"persist.xml"}).getBean("poolDataSource", //$NON-NLS-1$ //$NON-NLS-2$
                    DataSource.class);
        }
    }

    /**
     * needs to have a valid mySQL db in order to run this code
     * @throws Exception if there were exceptions
     */
    @Test
    public void testDatabaseIDs() throws Exception {
        DatabaseIDFactory factory = new DatabaseIDFactory(mDataSource,
                DatabaseIDFactory.TABLE_NAME, DatabaseIDFactory.COL_NAME, 13);

        // create the table
        Connection dbConnection = mDataSource.getConnection();
        try {
            Statement stmt = dbConnection.createStatement();
            stmt.execute("drop table if exists " + DatabaseIDFactory.TABLE_NAME); //$NON-NLS-1$
            stmt.execute("create table " + DatabaseIDFactory.TABLE_NAME + //$NON-NLS-1$
                    "(id bigint default null auto_increment primary key, " + //$NON-NLS-1$
                    DatabaseIDFactory.COL_NAME + " bigint not null default 0)"); //$NON-NLS-1$

            factory.init();
            String next = factory.getNext();
            assertNotNull(next);
            assertFalse("looks like we got an inMemoryIDFactory id: " + next, //$NON-NLS-1$
                    next.contains(InetAddress.getLocalHost().toString()));
            String previous = next;
            for(long i=0;i < DatabaseIDFactory.NUM_IDS_GRABBED*2; i++){
                String cur = factory.getNext();
                assertNotNull(cur);
                assertNotSame("getting same ids in a row", cur, previous); //$NON-NLS-1$
                assertTrue((new Long(cur) > new Long(previous)));
                previous = cur;
            }
        } finally {
            if(dbConnection != null) {
                dbConnection.close();
            }
        }
    }
}
