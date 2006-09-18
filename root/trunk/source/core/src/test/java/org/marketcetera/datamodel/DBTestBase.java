package org.marketcetera.datamodel;

import org.marketcetera.core.ClassVersion;
import org.hibernate.Session;
import junit.framework.TestCase;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public abstract class DBTestBase extends TestCase {
    protected static DBTestSuite suite;
    protected Session dbSession;

    public DBTestBase(String string) {
        super(string);
    }

    protected void setUp() throws Exception {
        super.setUp();
        dbSession = suite.dbSession;
    }
}
