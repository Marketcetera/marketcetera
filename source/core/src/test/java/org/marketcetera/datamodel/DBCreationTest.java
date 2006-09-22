package org.marketcetera.datamodel;

import junit.framework.Test;
import junit.framework.TestCase;
import org.marketcetera.core.ClassVersion;
import org.marketcetera.datamodel.helpers.HibernateUtil;
import org.marketcetera.datamodel.helpers.PrecannedDataLoader;
import org.hibernate.Session;

/**
 * Instantiates a hibernate session and nothing more
 * Run it to quickly create the DB and see the tables
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class DBCreationTest extends TestCase {
    public DBCreationTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return new DBTestSuite(DBCreationTest.class);
    }

    /** Really, just need an empty method that verifies that the session exists and
     * all tables got loaded correctly.
     * @throws Exception
     */
    public void testCreate() throws Exception {
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.close();
    }

    /** Verify the precanned data gets loaded */
    public void testPrecannedData() throws Exception {
        assertNotNull(PrecannedDataLoader.USD);
        assertNotNull(PrecannedDataLoader.GBP);
        assertNotNull(PrecannedDataLoader.EUR);
        assertEquals("USD", PrecannedDataLoader.USD.getAlphaCode());
        assertNotNull(PrecannedDataLoader.STI);
        assertEquals(SubAccountType.SHORT_TERM_INV, PrecannedDataLoader.STI.getDescription());
    }
}
