package org.marketcetera.datamodel;

import junit.framework.Test;
import org.marketcetera.core.ClassVersion;

/**
 * Instantiates a hibernate session and nothing more
 * Run it to quickly create the DB and see the tables
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class DBCreationTest extends DBTestBase {
    public DBCreationTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        return suite =  new DBTestSuite(DBCreationTest.class);
    }

    /** Really, just need an empty method that verifies that the session exists and
     * all tables got loaded correctly.
     * @throws Exception
     */
    public void testCreate() throws Exception {
        suite.dbSession.close();
    }

    public void testCreateSaveAll() throws Exception {
        Account acct = new Account();
        acct.setDescription("stupid desc");
        acct.setInstitutionIdentifier("bob");
        acct.setNickname("nick");
        //acct.setUpdatedOn();

    }
}
