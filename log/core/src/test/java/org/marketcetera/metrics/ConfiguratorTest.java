package org.marketcetera.metrics;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Tests {@link Configurator}
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 2.0.0
 */
@ClassVersion("$Id$")
public class ConfiguratorTest {
    @BeforeClass
    public static void setup() {
        Configurator.clearReportedValues();
    }

    /**
     * Tests Configurator operations.
     */
    @Test
    public void simple() {
        //Verify the default property file configurator instance
        assertTrue(Configurator.getReportedValues().isEmpty());
        assertNull(Configurator.getProperty("property",null));
        assertThat(Configurator.getReportedValues(), hasEntry("property",(String)null));
        assertEquals(1, Configurator.getReportedValues().size());
        assertEquals("default", Configurator.getProperty("property2", "default"));
        assertThat(Configurator.getReportedValues(), allOf(
                hasEntry("property",(String)null),
                hasEntry("property2","default")));
        assertEquals(2, Configurator.getReportedValues().size());
        Configurator.clearReportedValues();

        //Now test with a mock subclass implementation.
        Map<String,String> properties = new HashMap<String, String>();
        properties.put("key1", "value1");
        properties.put("key2", "value2");
        Configurator.setInstance(new MockConfigurator(properties));

        //Verify no values have been reported so far.
        assertTrue(Configurator.getReportedValues().isEmpty());
        //verify a non-existent property with no default
        assertNull(Configurator.getProperty("notexist", null));
        assertThat(Configurator.getReportedValues(), hasEntry("notexist",(String)null));
        assertEquals(1, Configurator.getReportedValues().size());
        //verify a non-existent property with a default
        assertEquals("default", Configurator.getProperty("notexistwithdefault", "default"));
        assertThat(Configurator.getReportedValues(), allOf(
                hasEntry("notexist",(String)null),
                hasEntry("notexistwithdefault","default")));
        assertEquals(2, Configurator.getReportedValues().size());
        //verify an existing property without default
        assertEquals("value1", Configurator.getProperty("key1", null));
        assertThat(Configurator.getReportedValues(), allOf(
                hasEntry("notexist",(String)null),
                hasEntry("notexistwithdefault","default"),
                hasEntry("key1","value1")));
        assertEquals(3, Configurator.getReportedValues().size());
        //verify an existing property with default
        assertEquals("value2", Configurator.getProperty("key2", "default"));
        assertThat(Configurator.getReportedValues(), allOf(
                hasEntry("notexist",(String)null),
                hasEntry("notexistwithdefault","default"),
                hasEntry("key1","value1"),
                hasEntry("key2","value2")));
        assertEquals(4, Configurator.getReportedValues().size());

    }

    /**
     * A mock configurator for testing {@link Configurator}.
     */
    static class MockConfigurator extends Configurator {
        /**
         * Creates an instance.
         *
         * @param inProperties the properties that are used to furnish
         * configuration property values.
         */
        public MockConfigurator(Map<String, String> inProperties) {
            mProperties = inProperties;
        }

        @Override
        protected String getPropertyValue(String inName) {
            return mProperties.get(inName);
        }
        private final Map<String,String> mProperties;
    }
}
