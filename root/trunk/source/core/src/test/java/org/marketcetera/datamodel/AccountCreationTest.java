package org.marketcetera.datamodel;

import org.marketcetera.core.ClassVersion;
import org.hibernate.Session;
import junit.framework.Test;

import java.util.Date;
import java.text.DateFormat;


/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class AccountCreationTest extends DBTestBase {
    public AccountCreationTest(String inName) {
        super(inName);
    }

    public static Test suite() {
        suite = new DBTestSuite(AccountCreationTest.class);
        return suite;
    }

    public void testCreate() throws Exception {
        dbSession.beginTransaction();

        Account acct = new Account();
        acct.setDescription("new acct");
        acct.setInstitutionIdentifier("bear");
        acct.setNickname("acct-"+System.currentTimeMillis());
        Date initCreateDate = acct.getCreatedOn();
        dbSession.save(acct);
        dbSession.getTransaction().commit();
        DateFormat format = DateFormat.getDateInstance();
        assertEquals("initial creation date", format.format(initCreateDate), format.format(acct.getCreatedOn()));

        dbSession = HibernateUtil.getSessionFactory().getCurrentSession();
        dbSession.beginTransaction();
        Account a = (Account) dbSession.load(Account.class, acct.getId());
        assertEquals("initial creation date", format.format(initCreateDate), format.format(a.getCreatedOn()));
        assertNotNull(a);
        assertEquals(acct.getNickname(), a.getNickname());

        // modify the nick and see that mod time changed
        Date updatedOn = acct.getUpdatedOn();
        a.setNickname("new nick");
        Thread.sleep(5000);
        dbSession.save(a);
        dbSession.getTransaction().commit();
        assertEquals("initial creation date", format.format(initCreateDate), format.format(a.getCreatedOn()));

        dbSession = HibernateUtil.getSessionFactory().getCurrentSession();
        dbSession.beginTransaction();
        a =  (Account) dbSession.load(Account.class, acct.getId());
        assertTrue("updated on didn't change", updatedOn.getTime() < a.getUpdatedOn().getTime());
        assertEquals("new nick", a.getNickname());
        assertEquals("createdOn date changed", format.format(initCreateDate), format.format(a.getCreatedOn()));
        dbSession.getTransaction().commit();
    }
}
