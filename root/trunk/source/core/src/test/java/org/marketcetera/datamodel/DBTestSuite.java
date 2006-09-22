package org.marketcetera.datamodel;

import org.hibernate.Session;
import org.marketcetera.core.*;
import org.marketcetera.datamodel.helpers.HibernateUtil;
import org.marketcetera.datamodel.helpers.PrecannedDataLoader;

/**
 * Common test suite that initializes db based on the user that runs it
 *
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class DBTestSuite extends MarketceteraTestSuite {
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

    public void init(MessageBundleInfo[] inBundles) {
        super.init(inBundles);

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

            // load the currencies and sub-account types
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            PrecannedDataLoader.loadAllCurrencies(session);
            PrecannedDataLoader.loadSubAccountTypes(session);
            PrecannedDataLoader.initializeCommon(session);
            session.getTransaction().commit();

            System.out.println("Finished loading and initializing the database");
        } catch (Exception ex) {
            LoggerAdapter.error("Error initializing suite", ex, this);
            System.exit(1);
        }
    }
}
