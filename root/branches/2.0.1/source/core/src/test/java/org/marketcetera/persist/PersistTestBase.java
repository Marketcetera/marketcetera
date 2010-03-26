package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.RandomStrings;
import org.marketcetera.util.misc.UCPFilter;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.ApplicationContext;

import javax.persistence.TemporalType;
import java.security.SecureRandom;
import java.util.*;
import java.beans.PropertyDescriptor;
import java.beans.IntrospectionException;
import java.beans.BeanInfo;
import java.beans.Introspector;

import static org.junit.Assert.*;

/* $License$ */
/**
 * Base class for persistence tests.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class PersistTestBase {

    /**
     * Sets up the spring configuration for the given spring configuration
     * files
     *
     * @param configFiles the spring configuration file names
     *
     * @throws Exception if there was an error
     * @return the initialized spring context
     */
    public static AbstractApplicationContext springSetup(
            String[] configFiles) throws Exception {
        return springSetup(configFiles,null);
    }
    /**
     * Sets up the spring configuration for the given spring configuration
     * files
     *
     * @param configFiles the spring configuration file names
     *
     * @param parent the parent spring configuration, if one is available.
     *  Can be null.
     * @throws Exception if there was an error
     * @return the initialized spring context
     */
    public static AbstractApplicationContext springSetup(
            String[] configFiles,
            ApplicationContext parent) throws Exception {
        try {
            generator = SecureRandom.getInstance("SHA1PRNG"); //$NON-NLS-1$
            LoggerConfiguration.logSetup();
            //runs multiple tests in the same vm (which it currently does)
            ClassPathXmlApplicationContext context;
            if (parent == null) {
                context = new ClassPathXmlApplicationContext(configFiles);
            } else {
                context = new ClassPathXmlApplicationContext(
                        configFiles, parent);
            }
            context.registerShutdownHook();
            return context;
        } catch (Exception e) {
            SLF4JLoggerProxy.error(PersistTestBase.class, "FailedSetup:", e); //$NON-NLS-1$
            throw e;
        }
    }

    /**
     * Returns a random string for testing.
     * The string can contain any letter or digit unicode character between
     * \u0000 & \uFFFF, except for surrogate characters.
     *
     * @return a random string.
     */
    public static String randomString() {
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
     * Returns the random number generator.
     *
     * @return the random number generator
     */
    protected static Random getGenerator() {
        return generator;
    }

    /**
     * Compares two collections of entities by comparing the IDs
     * of the entities contained within the collection. The
     * order of entities in the collections do not matter
     *
     * @param expected the first collection of entities
     * @param actual the second collection of entities
     */
    protected final <T extends SummaryEntityBase> void assertCollectionPermutation(
            Collection<T> expected, Collection<T> actual) {
        if((expected == null || expected.isEmpty()) ^
                (actual == null || actual.isEmpty())) {
            org.junit.Assert.fail("expected<" + expected + //$NON-NLS-1$
                    "> actual<" + actual +">"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if((expected == null || expected.isEmpty()) &&
                (actual == null || actual.isEmpty())) {
            return;
        }
        org.junit.Assert.assertEquals(expected.size(),actual.size());
        HashSet<Long> expIds = new HashSet<Long>();
        for(T t:expected) {
            expIds.add(t.getId());
        }
        HashSet<Long> actIds = new HashSet<Long>();
        for(T t:actual) {
            actIds.add(t.getId());
        }
        org.junit.Assert.assertTrue("expected<" + expected + "> actual<" + //$NON-NLS-1$ //$NON-NLS-2$
                actual + ">",expIds.equals(actIds)); //$NON-NLS-1$
    }

    /**
     * Fetches the bean property descriptor for the given property name
     * on the supplied class
     *
     * @param clazz The class who's property needs to be introspected
     * @param attributeName the property name
     *
     * @return The property descriptor for the specified property.
     *
     * @throws IntrospectionException If there was an error introspecting
     * the properties
     * @throws IllegalArgumentException if the property descriptor
     * wasn't found.
     */
    public static PropertyDescriptor getPropertyDescriptor(
            Class clazz, String attributeName) throws IntrospectionException {
        PropertyDescriptor pd = null;
        BeanInfo info= Introspector.getBeanInfo(clazz,
                Introspector.IGNORE_ALL_BEANINFO);
        for(PropertyDescriptor p: info.getPropertyDescriptors()) {
            if(attributeName.equals(p.getName())) {
                pd = p;
                break;
            }
        }
        if(pd == null) {
            throw new IllegalArgumentException(attributeName);
        }
        return pd;
    }

    /**
     * verify if the two dates are equals
     *
     * @param d1 the first date
     * @param d2 the second date
     * @param type the date comparison precision
     */
    public static void assertCalendarEquals(Date d1, Date d2,
                                               TemporalType type) {
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
     * Sleeps for the minimal time that the db can differentiate between.
     *
     * @throws InterruptedException if interrupted.
     */
    public static void sleepForSignificantTime() throws InterruptedException {
        //The db can only tell time atleast a second apart
        Thread.sleep(1000);
    }

    /**
     * UCP Filter used for generating characters for testing.
     */
    private static class PersistUCPFilter extends UCPFilter {
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
    private static class PersistNameStringFilter extends UCPFilter {
        static final PersistNameStringFilter INSTANCE = new PersistNameStringFilter();
        public boolean isAcceptable(int ucp) {
            return ucp >= 32 && ucp <= 127 && Character.isLetterOrDigit(ucp); 
        }
    }

    private static SecureRandom generator;
}
