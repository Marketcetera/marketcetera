package org.marketcetera.dao.hibernate;

import org.hibernate.SessionFactory;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.dao.DataAccessService;
import org.marketcetera.systemmodel.SystemAuthority;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/* $License$ */

/**
 * Provides common utilities for hibernate-based data tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class HibernateTestBase
{
    /**
     * Run once before all tests. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void onceBefore()
            throws Exception
    {
        LoggerConfiguration.logSetup();
        app = new MockHibernateApp();
        app.start();
    }
    /**
     * Run once after all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @AfterClass
    public static void onceAfter()
            throws Exception
    {
        app.stop();
    }
    /**
     * Run before each test. 
     *
     * @throws Exception if an unexpected error occurs
     */
    @Before
    public void setup()
            throws Exception
    {
        dao = (DataAccessService)getApp().getContext().getBean("hibernateDataAccessService");
        TestingAuthenticationToken testingAuthentication = new TestingAuthenticationToken("testing-admin",
                                                                                          "testing-admin-password",
                                                                                          SystemAuthority.ROLE_ADMIN.name(),
                                                                                          SystemAuthority.ROLE_USER.name());
        testingAuthentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(testingAuthentication);
    }
    /**
     * Get the dao value.
     *
     * @return a <code>DataAccessService</code> value
     */
    public DataAccessService getDao()
    {
        return dao;
    }
    /**
     * Gets the current <code>SessionFactory</code> value. 
     *
     * @return a <code>SessionFactory</code> value
     */
    protected static SessionFactory getSessionFactory()
    {
        return (SessionFactory)getApp().getContext().getBean("sessionFactory");
    }
    /**
     * Gets the test application.
     *
     * @return a <code>MockHibnerateApp</code> value
     */
    protected static MockHibernateApp getApp()
    {
        return app;
    }
    /**
     * mock application value
     */
    private static MockHibernateApp app;
    /**
     * data access service object
     */
    private DataAccessService dao;
}
