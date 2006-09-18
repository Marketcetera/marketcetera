package org.marketcetera.datamodel;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MarketceteraTestSuite;
import org.marketcetera.core.MessageBundleInfo;
import org.hibernate.cfg.Settings;
import org.hibernate.Session;

import javax.xml.transform.Templates;

/**
 * Common test suite that initializes db based on the user that runs it
 *
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class DBTestSuite extends MarketceteraTestSuite {
    public DBTestSuite() {
        super();
    }

    public DBTestSuite(Class aClass) {
        super(aClass);
    }

    public DBTestSuite(Class aClass, MessageBundleInfo extraBundle) {
        super(aClass, extraBundle);
    }

    public DBTestSuite(Class aClass, MessageBundleInfo[] extraBundles) {
        super(aClass, extraBundles);
    }

    protected Session dbSession;

    // todo: change server01 to be modifiable
    public void init(MessageBundleInfo[] inBundles) {
        super.init(inBundles);
        String user = System.getProperty("user.name");
        dbSession = HibernateUtil.initialize("localhost/junit-"+user).getCurrentSession();
    }
}
