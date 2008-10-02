package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.management.JMX;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.HashMap;
import java.math.BigDecimal;
import java.math.BigInteger;

/* $License$ */
/**
 * Tests the {@link PropertiesConfigurationProvider}
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class PropertiesConfigurationProviderTest
        extends ConfigurationProviderTestBase {
    @Before
    public void setup() throws Exception {
        mLoader.clear();
    }

    /**
     * Tests the provider that doesn't supply any default values.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void providerNoValues() throws Exception {
        // Create a module manager with provider that has no values
        // and verify that module factory and instances
        // get default values
        mManager = new ModuleManager();
        PropertiesConfigurationProvider provider =
                new PropertiesConfigurationProvider(
                        getClass().getClassLoader());
        mManager.setConfigurationProvider(provider);
        mManager.init();
        verifyEmptyValues();
    }

    /**
     * Tests the provider that provides default values for various attribute
     * types.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void providerWithValues() throws Exception {
        mManager = new ModuleManager();
        PropertiesConfigurationProvider provider =
                new PropertiesConfigurationProvider(mLoader);
        mManager.setConfigurationProvider(provider);
        ModuleURN providerURN = ConfigurationProviderTestFactory.PROVIDER_URN;
        ModuleURN instanceURN = new ModuleURN(ConfigurationProviderTestFactory.PROVIDER_URN,
                "propuhtees");
        Properties properties = new Properties();
        properties.setProperty("MaxLimit","123456.123456");
        properties.setProperty(".Boolean","true");
        properties.setProperty("Decimal", "987.654");
        properties.setProperty(".Decimal","123.123");
        properties.setProperty(instanceURN.instanceName() + ".Decimal","123.123");
        properties.setProperty("whatever.Decimal","34234.234");
        properties.setProperty("wherever.Decimal","34234.234");
        properties.setProperty("String","yes");
        properties.setProperty(".File","/tmp/yes");
        properties.setProperty(instanceURN.instanceName() + ".FactoryAnnotation","annoDomini");
        properties.setProperty("int","312");
        properties.setProperty("whatever.PrimFloat","312");

        String propertiesName = new StringBuilder().
                append(providerURN.providerType()).
                append("_").append(providerURN.providerName()).
                append(".properties").toString();
        mLoader.addResource(propertiesName,
                properties);
        mManager.init();

        ConfigurationProviderFactoryMXBean factory = JMX.newMXBeanProxy(
                getMBeanServer(),
                providerURN.toObjectName(),
                ConfigurationProviderFactoryMXBean.class);
        assertEquals(new BigDecimal("123456.123456"),factory.getMaxLimit());
        assertEquals(new BigInteger("0"),factory.getInstances());

        mManager.createModule(ConfigurationProviderTestFactory.PROVIDER_URN,
                instanceURN);
        JMXTestModuleMXBean module = JMX.newMXBeanProxy(getMBeanServer(),
                instanceURN.toObjectName(), JMXTestModuleMXBean.class);
        //value for all instances
        assertEquals(true, module.getBoolean());
        //specific value for this instance
        assertEquals(new BigDecimal("123.123"), module.getDecimal());
        //value set for factory but not for this instance
        assertNull(module.getString());
        //value set for all instances
        assertEquals("/tmp/yes",module.getFile());
        //value only set for this instance
        assertEquals("annoDomini", module.getFactoryAnnotation());
        //value set but with incorrect property name case
        assertNull(module.getInt());
        //value set for a different instance
        assertEquals(0.0f, module.getPrimFloat(), 0.0f);
        //value not specified in the properties.
        assertNull(module.getURL());
    }

    /**
     * Tests the provider refresh functionality.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void providerRefresh() throws Exception {
        mManager = new ModuleManager();
        PropertiesConfigurationProvider provider = new PropertiesConfigurationProvider(mLoader);
        mManager.setConfigurationProvider(provider);
        ModuleURN providerURN = ConfigurationProviderTestFactory.PROVIDER_URN;
        Properties properties = new Properties();
        properties.setProperty("MaxLimit","897.98327");
        properties.setProperty(".String","before");
        String propertiesName = new StringBuilder().
                append(providerURN.providerType()).
                append("_").append(providerURN.providerName()).
                append(".properties").toString();
        mLoader.addResource(propertiesName,
                properties);
        mManager.init();
        ConfigurationProviderFactoryMXBean factory = JMX.newMXBeanProxy(
                getMBeanServer(),
                providerURN.toObjectName(),
                ConfigurationProviderFactoryMXBean.class);
        assertEquals(new BigDecimal("897.98327"),factory.getMaxLimit());

        ModuleURN instanceURN = new ModuleURN(ConfigurationProviderTestFactory.PROVIDER_URN,
                "refresh");
        mManager.createModule(ConfigurationProviderTestFactory.PROVIDER_URN,
                instanceURN);
        JMXTestModuleMXBean module = JMX.newMXBeanProxy(getMBeanServer(),
                instanceURN.toObjectName(), JMXTestModuleMXBean.class);
        //value for all instances
        assertEquals("before", module.getString());
        //verify that another value is not set.
        assertNull(module.getURL());

        //now refresh the module manager
        mManager.refresh();
        //modify the property and add another one
        properties.setProperty("MaxLimit","8938893.48393");
        properties.setProperty(".String","after");
        properties.setProperty(".URL","http://whatever.com");
        //Create a new instance and verify its properties.
        ModuleURN instance2URN = new ModuleURN(ConfigurationProviderTestFactory.PROVIDER_URN,
                "arefresh");
        mManager.createModule(ConfigurationProviderTestFactory.PROVIDER_URN,
                instance2URN);
        JMXTestModuleMXBean module2 = JMX.newMXBeanProxy(getMBeanServer(),
                instance2URN.toObjectName(), JMXTestModuleMXBean.class);
        assertEquals("after",module2.getString());
        assertEquals("http://whatever.com",module2.getURL());

        //verify that the factory and the old instance are unchanged
        assertEquals(new BigDecimal("897.98327"),factory.getMaxLimit());
        assertEquals("before", module.getString());
        assertNull(module.getURL());
    }

    /**
     * Verifies behavior when the configuration provider is unable to
     * read a properties file.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void propertyReadFailure() throws Exception {
        mManager = new ModuleManager();
        PropertiesConfigurationProvider provider = new PropertiesConfigurationProvider(mLoader);
        mManager.setConfigurationProvider(provider);
        mLoader.setFail(true);
        ModuleURN providerURN = ConfigurationProviderTestFactory.PROVIDER_URN;
        String propertiesName = new StringBuilder().
                append(providerURN.providerType()).
                append("_").append(providerURN.providerName()).
                append(".properties").toString();
        mLoader.addResource(propertiesName,
                new Properties());
        new ExpectedFailure<ModuleException>(Messages.ERROR_READ_PROPERTIES,
                propertiesName){
            protected void run() throws Exception {
                mManager.init();
            }
        };
    }

    /**
     * A class loader that is used to test provider refresh behavior. The
     * class loader dynamically returns properties files based on the
     * resources that have been added to it.
     */
    private static class DynamicResourceLoader extends ClassLoader {
        @Override
        public InputStream getResourceAsStream(String name) {
            try {
                if(mResources.containsKey(name)) {
                    if(mFail) {
                        return new InputStream(){
                            public int read() throws IOException {
                                throw new IOException();
                            }
                        };
                    }
                    Properties p = mResources.get(name);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    p.store(baos,"");
                    baos.close();
                    return new ByteArrayInputStream(baos.toByteArray());
                }
            } catch (IOException ignore) {
            }
            return super.getResourceAsStream(name);
        }

        /**
         * Adds the property file with the specified name and contents to
         * the set of resources that should be returned by this class loader.
         *
         * @param inName the resource name.
         * @param inProperties the property value.
         */
        public void addResource(String inName, Properties inProperties) {
            mResources.put(inName, inProperties);
        }

        /**
         * if the returne input stream for a resource should
         * throw an IOException when an attempt is  made to read it.
         *
         * @param inFail if the returne input stream for a resource should
         * throw an IOException when an attempt is  made to read it.
         */
        public void setFail(boolean inFail) {
            mFail = inFail;
        }

        /**
         * Clears the classloader state.
         */
        public void clear() {
            mResources.clear();
            mFail = false;
        }
        private HashMap<String, Properties> mResources =
                new HashMap<String, Properties>();
        private boolean mFail = false;
    }
    private DynamicResourceLoader mLoader = new DynamicResourceLoader();
}
