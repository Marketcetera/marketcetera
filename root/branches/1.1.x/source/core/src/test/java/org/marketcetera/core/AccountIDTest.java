package org.marketcetera.core;

import junit.framework.TestCase;


/**
 *
 * @author gmiller
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class AccountIDTest extends TestCase {

    public AccountIDTest(String testName) {
        super(testName);
    }

    public static junit.framework.Test suite() {
        return new MarketceteraTestSuite(AccountIDTest.class);
    }

    /**
     * Test of {@link AccountID#getAccountNickname} method
     */
    public void testEquals() {
        AccountID id1 = new AccountID("asdf"); //$NON-NLS-1$
        AccountID id2 = new AccountID("asdf", "somenick"); //$NON-NLS-1$ //$NON-NLS-2$
        AccountID id3 = new AccountID("qwer"); //$NON-NLS-1$
        String stringID = "asdf"; //$NON-NLS-1$

        assertTrue(id1.equals(id2));
        assertTrue(id2.equals(id1));
        assertFalse(id2.equals(id3));
        assertFalse(id3.equals(id2));
        assertFalse(id1.equals(id3));
        assertFalse(id3.equals(id1));

        assertFalse(id1.equals(stringID));

    }

    // TODO add test methods here. The name must begin with 'test'. For example:
    // public void testHello() {}

}
