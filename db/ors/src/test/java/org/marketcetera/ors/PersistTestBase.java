package org.marketcetera.ors;

import static org.junit.Assert.fail;

import java.security.SecureRandom;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.TemporalType;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.ors.dao.ReportService;
import org.marketcetera.ors.dao.UserService;
import org.marketcetera.util.misc.RandomStrings;
import org.marketcetera.util.misc.UCPFilter;
import org.marketcetera.util.test.TestCaseBase;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration(defaultRollback=true)
@ContextConfiguration(locations={"file:src/test/sample_data/conf/server.xml"})
public class PersistTestBase
        extends TestCaseBase
        implements ApplicationContextAware
{
    /**
     * Runs once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
    }
    /* (non-Javadoc)
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext inContext)
            throws BeansException
    {
        context = (ConfigurableApplicationContext)inContext;
        context.registerShutdownHook();
        userService = context.getBean(UserService.class);
    }
    /**
     *
     *
     * @param inConfigFiles
     * @return
     * @throws Exception 
     */
    public static ConfigurableApplicationContext springSetup(String[] inConfigFiles)
            throws Exception
    {
//        return springSetup(inConfigFiles,
//                           null);
        return context;
    }
    public static ConfigurableApplicationContext springSetup(String[] configFiles,
                                                             ApplicationContext parent)
            throws Exception
    {
//        try {
//            generator = SecureRandom.getInstance("SHA1PRNG"); //$NON-NLS-1$
//            if(parent == null) {
//                context = new ClassPathXmlApplicationContext(configFiles);
//            } else {
//                context = new ClassPathXmlApplicationContext(configFiles,
//                                                             parent);
//            }
//            context.registerShutdownHook();
//            return context;
//        } catch (Exception e) {
//            SLF4JLoggerProxy.error(PersistTestBase.class, "FailedSetup:", e); //$NON-NLS-1$
//            throw e;
//        }
        return context;
    }
    /**
     * 
     *
     *
     * @param d1
     * @param d2
     * @param type
     */
    public static void assertCalendarEquals(Date d1,
                                            Date d2,
                                            TemporalType type)
    {
        if(d1 == null && d2 == null) {
            return;
        }
        if(d1 == null || d2 == null) {
            fail("expected<" + d1 + "> actual<" + d2 + ">"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        if (type == TemporalType.DATE || type == TemporalType.TIMESTAMP) {
            org.junit.Assert.assertEquals(c1.get(Calendar.YEAR),c2.get(Calendar.YEAR));
            org.junit.Assert.assertEquals(c1.get(Calendar.MONTH),c2.get(Calendar.MONTH));
            org.junit.Assert.assertEquals(c1.get(Calendar.DAY_OF_MONTH),c2.get(Calendar.DAY_OF_MONTH));
        }
        if (type == TemporalType.TIME || type == TemporalType.TIMESTAMP) {
            org.junit.Assert.assertEquals(c1.get(Calendar.HOUR_OF_DAY),c2.get(Calendar.HOUR_OF_DAY));
            org.junit.Assert.assertEquals(c1.get(Calendar.MINUTE),c2.get(Calendar.MINUTE));
            org.junit.Assert.assertEquals(c1.get(Calendar.SECOND),c2.get(Calendar.SECOND));
        }
    }
    /**
     *
     *
     * @return
     */
    public static String randomString()
    {
        return RandomStrings.genStr(PersistUCPFilter.INSTANCE, 10);
    }
    /**
     * Returns a random name string that only has ASCII characters.
     * The returned string will pass the name validations
     * used within NDEntityTestBase instances
     *
     * @return a random name string.
     */
    public static String randomNameString() {
        return RandomStrings.genStr(PersistNameStringFilter.INSTANCE, 10);
    }
    /**
     * UCP Filter used for generating characters for testing.
     */
    private static class PersistUCPFilter
            extends UCPFilter
    {
        static final PersistUCPFilter INSTANCE = new PersistUCPFilter();
        public boolean isAcceptable(int ucp) {
            // mysql version dependency: this piece of code depends
            // on specific version of mysql and may need to be updated
            // whenever mysql version is updated
            return Character.isLetterOrDigit(ucp) &&
                    //Make sure its a character in the range that mysql can handle
                    UCPFilter.CHAR.isAcceptable(ucp) && 
                    //mysql doesn't support supplementary code points.
                    (!Character.isSupplementaryCodePoint(ucp));
        }
    }
    /**
     * UCP Filter used to generate ASCII characters that are
     * letters & digit for testing.
     */
    private static class PersistNameStringFilter
            extends UCPFilter
    {
        static final PersistNameStringFilter INSTANCE = new PersistNameStringFilter();
        public boolean isAcceptable(int ucp) {
            return ucp >= 32 && ucp <= 127 && Character.isLetterOrDigit(ucp); 
        }
    }
    protected UserService userService;
    protected ReportService reportService;
    private static SecureRandom generator;
    private static ConfigurableApplicationContext context;
}
