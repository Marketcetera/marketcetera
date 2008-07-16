package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MessageBundleManager;
import org.marketcetera.core.LoggerAdapter;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
@ClassVersion("$Id$")
public class PersistTestBase {
    private static SecureRandom generator;

    /**
     * Sets up the spring configuration for the given spring configuration
     * files
     *
     * @param configFiles the spring configuration file names
     *
     * @throws Exception if there was an error
     */
    protected static void springSetup(String[] configFiles) throws Exception {
        try {
            generator = SecureRandom.getInstance("SHA1PRNG");
            logSetup();
            //runs multiple tests in the same vm (which it currently does)
            new ClassPathXmlApplicationContext(configFiles);
        } catch (Exception e) {
            SLF4JLoggerProxy.error(PersistTestBase.class, "FailedSetup:", e);
            throw e;
        }
    }

    /**
     * Sets up logging.
     */
    protected static void logSetup() {
        MessageBundleManager.registerCoreMessageBundle();
        LoggerAdapter.initializeLogger("unit-testing");
    }

    /**
     * Returns a random string for testing.
     *
     * @return a random string.
     */
    protected static String randomString() {
        return Long.toString(Math.abs(generator.nextLong()),
                Character.MAX_RADIX);
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
            org.junit.Assert.fail("expected<" + expected +
                    "> actual<" + actual +">");
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
        org.junit.Assert.assertTrue("expected<" + expected + "> actual<" +
                actual + ">",expIds.equals(actIds));
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
    protected static void assertCalendarEquals(Date d1, Date d2,
                                               TemporalType type) {
        if(d1 == null && d2 == null) {
            return;
        }
        if(d1 == null || d2 == null) {
            fail("expected<" + d1 + "> actual<" + d2 + ">");
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
}
