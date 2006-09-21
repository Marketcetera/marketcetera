package org.marketcetera.datamodel;

import junit.framework.TestCase;
import org.hibernate.Session;
import org.marketcetera.core.*;

/**
 * Common test suite that initializes db based on the user that runs it
 *
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class DBTestSuite extends MarketceteraTestSuite {
    private static final String CURRENCIES_FILE_NAME = "currencies.sql";
    private static final String SUB_ACCOUNT_TYPES_FILE_NAME = "sub_account_types.sql";
    private static Class[] allMappedClasses = new Class[] {
            Account.class, Currency.class, Dividend.class, Equity.class,
            EquityOption.class, EquityOptionSeries.class, EquityOptionUnderlying.class,
            Journal.class, MSymbol.class, Posting.class, SubAccount.class,
            SubAccountType.class, Trade.class,};

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
        dbSession = HibernateUtil.initialize().getCurrentSession();

        try {
            // clear all tables
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            // temporarily disable foreign key checks in order to clean the tables.
            session.createSQLQuery("SET FOREIGN_KEY_CHECKS = 0;").executeUpdate();
            for (Class aClass : allMappedClasses) {
                session.createQuery("delete from "+aClass.getSimpleName()).executeUpdate();
            }
            session.createSQLQuery("SET FOREIGN_KEY_CHECKS = 1;").executeUpdate();
            session.getTransaction().commit();

            // load the currencies
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.createSQLQuery(Util.getStringFromFile(CURRENCIES_FILE_NAME, this)).executeUpdate();
            session.createSQLQuery(Util.getStringFromFile(SUB_ACCOUNT_TYPES_FILE_NAME,  this)).executeUpdate();
            Currency.initializeCommon(session);
            session.getTransaction().commit();

            System.out.println("Finished loading and initializing the database");
        } catch (Exception ex) {
            LoggerAdapter.error("Error initializing suite", ex, this);
            System.exit(1);
        }
    }
}
