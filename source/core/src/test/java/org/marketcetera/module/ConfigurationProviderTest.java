package org.marketcetera.module;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;

import javax.management.JMX;

import org.junit.Test;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Tests integration of module configuration provider
 * with module manager.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class ConfigurationProviderTest extends ConfigurationProviderTestBase {
    /**
     * Verifies the behavior without configuration provider.
     *
     * @throws Exception if there unexpected errors.
     */
    @Test
    public void noProvider() throws Exception {
        // Create a module manager without provider
        // and verify that module factory and instances
        // get empty values
        mManager = new ModuleManager();
        mManager.init();
        verifyEmptyValues();
    }

    /**
     * Verifies the behavior when the configuration provider has
     * no default values for module factory / instances.
     *
     * @throws Exception if there unexpected errors.
     */
    @Test
    public void providerNoValues() throws Exception {
        // Create a module manager with provider that has no values
        // and verify that module factory and instances
        // get default values
        mManager = new ModuleManager();
        MockConfigurationProvider provider = new MockConfigurationProvider();
        mManager.setConfigurationProvider(provider);
        mManager.init();
        verifyEmptyValues();
        assertEquals(24, provider.getFetches());
    }

    /**
     * Tests the behavior where default values are provider for various
     * data types.
     *
     * @throws Exception if there unexpected errors.
     */
    @Test
    public void providerWithValues() throws Exception {
        // Create a module manager with provider that has values
        // and verify that module factory and instances
        // get supplied values
        mManager = new ModuleManager();
        MockConfigurationProvider provider = new MockConfigurationProvider();
        String maxLimit = "5434543.8934";
        provider.addValue(ConfigurationProviderTestFactory.PROVIDER_URN,
                "MaxLimit", maxLimit);
        ModuleURN instanceURN = new ModuleURN(ConfigurationProviderTestFactory.PROVIDER_URN,
                "values");
        provider.addValue(instanceURN,"Boolean","true");
        provider.addValue(instanceURN,"Byte","13");
        provider.addValue(instanceURN,"Character","m");
        provider.addValue(instanceURN,"Decimal","123456789.123456789");
        provider.addValue(instanceURN,"Double","2345.8789");
        provider.addValue(instanceURN,"FactoryAnnotation","anno");
        provider.addValue(instanceURN,"File","/my/file");
        provider.addValue(instanceURN,"Float","123.123");
        provider.addValue(instanceURN,"Int","123456");
        provider.addValue(instanceURN,"Integer","123456789");
        provider.addValue(instanceURN,"Long","123456789");
        provider.addValue(instanceURN,"PrimByte","124");
        provider.addValue(instanceURN,"PrimCharacter","c");
        provider.addValue(instanceURN,"PrimDouble","123456.123456");
        provider.addValue(instanceURN,"PrimFloat","321.321");
        provider.addValue(instanceURN,"PrimInt","654321");
        provider.addValue(instanceURN,"PrimLong","987654321");
        provider.addValue(instanceURN,"PrimShort","321");
        provider.addValue(instanceURN,"Short","123");
        provider.addValue(instanceURN,"String","theory");
        provider.addValue(instanceURN,"URL","http://blah.com");
        provider.addValue(instanceURN,"PrimBoolean","true");
        mManager.setConfigurationProvider(provider);
        mManager.init();
        ConfigurationProviderFactoryMXBean factory = JMX.newMXBeanProxy(
                getMBeanServer(),
                ConfigurationProviderTestFactory.PROVIDER_URN.toObjectName(),
                ConfigurationProviderFactoryMXBean.class);
        assertEquals(new BigDecimal("5434543.8934"),factory.getMaxLimit());
        assertEquals(new BigInteger("0"),factory.getInstances());
        //Create a module and verify all its values are empty
        mManager.createModule(ConfigurationProviderTestFactory.PROVIDER_URN,
                instanceURN);
        JMXTestModuleMXBean module = JMX.newMXBeanProxy(getMBeanServer(),
                instanceURN.toObjectName(), JMXTestModuleMXBean.class);
        assertEquals(true,module.getBoolean());
        assertEquals(new Byte((byte) 13), module.getByte());
        assertEquals(new Character('m'), module.getCharacter());
        assertEquals(new BigDecimal("123456789.123456789"),module.getDecimal());
        assertEquals(new Double("2345.8789"),module.getDouble());
        assertEquals("anno",module.getFactoryAnnotation());
        assertEquals("/my/file", module.getFile());
        assertEquals(new Float("123.123"), module.getFloat());
        assertEquals(new Integer(123456), module.getInt());
        assertEquals(new BigInteger("123456789"), module.getInteger());
        assertEquals(new Long("123456789"),module.getLong());
        assertEquals(true,module.isPrimBoolean());
        assertEquals(124,module.getPrimByte());
        assertEquals('c', module.getPrimCharacter());
        assertEquals(123456.123456, module.getPrimDouble(),0.0);
        assertEquals(321.321f, module.getPrimFloat(),0.0);
        assertEquals(654321, module.getPrimInt());
        assertEquals(987654321l, module.getPrimLong());
        assertEquals(321,module.getPrimShort());
        assertEquals(new Short((short) 123), module.getShort());
        assertEquals("theory", module.getString());
        assertEquals("http://blah.com", module.getURL());
        assertEquals(24, provider.getFetches());
    }

    /**
     * Verifies that the provider is refreshed when the module manager
     * is refreshed.
     *
     * @throws Exception if there unexpected errors.
     */
    @Test
    public void providerRefresh() throws Exception {
        mManager = new ModuleManager();
        MockConfigurationProvider provider = new MockConfigurationProvider();
        mManager.setConfigurationProvider(provider);
        mManager.init();
        assertFalse(provider.isRefreshInvoked());
        mManager.refresh();
        assertTrue(provider.isRefreshInvoked());
    }

    /**
     * Verifies the behavior when errors are encountered converting the
     * default values from string to the bean attribute's data type.
     *
     * @throws Exception if there unexpected errors.
     */
    @Test
    public void valueConversionFailure() throws Exception {
        mManager = new ModuleManager();
        MockConfigurationProvider provider = new MockConfigurationProvider();
        String maxLimit = "ksdfikjor9390ue93";
        provider.addValue(ConfigurationProviderTestFactory.PROVIDER_URN,
                "MaxLimit", maxLimit);
        mManager.setConfigurationProvider(provider);
        new ExpectedFailure<BeanAttributeSetException>(
                Messages.UNABLE_SET_ATTRIBUTE,
                "MaxLimit", maxLimit,
                ConfigurationProviderTestFactory.PROVIDER_URN.toObjectName()){
            protected void run() throws Exception {
                mManager.init();
            }
        };
    }

    /**
     * A mock configuration provider for unit testing.
     */
    public static class MockConfigurationProvider
            implements ModuleConfigurationProvider {

        public String getDefaultFor(ModuleURN inURN, String inAttribute) {
            SLF4JLoggerProxy.debug(this, "{} {}:", inURN, inAttribute);
            mFetches++;
            return mValues.get(new Tuple(inURN, inAttribute));
        }

        public void refresh() throws ModuleException {
            mRefreshInvoked = true;
        }

        public int getFetches() {
            return mFetches;
        }

        public boolean isRefreshInvoked() {
            return mRefreshInvoked;
        }
        public void addValue(ModuleURN inURN, String inName, String inValue) {
            mValues.put(new Tuple(inURN, inName), inValue);
        }
        private boolean mRefreshInvoked = false;
        private int mFetches = 0;
        private HashMap<Tuple,String> mValues = new HashMap<Tuple, String>();
    }

    /**
     * A class to keep track of attribute names for module
     * factory / instances.
     */
    private static class Tuple {
        private Tuple(ModuleURN inURN, String inName) {
            mURN = inURN;
            mName = inName;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Tuple tuple = (Tuple) o;
            return mName.equals(tuple.mName) && mURN.equals(tuple.mURN);

        }

        public int hashCode() {
            int result;
            result = mURN.hashCode();
            result = 31 * result + mName.hashCode();
            return result;
        }

        private ModuleURN mURN;
        private String mName;
    }
}
