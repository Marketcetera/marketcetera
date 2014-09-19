package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.management.JMX;
import java.util.Properties;
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
        ModuleConfigurationProvider provider = createProvider(getClass().getClassLoader());
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
        ModuleConfigurationProvider provider = createProvider();
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

        mLoader.addResource(providerURN, properties);
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
        ModuleConfigurationProvider provider = createProvider();
        mManager.setConfigurationProvider(provider);
        ModuleURN providerURN = ConfigurationProviderTestFactory.PROVIDER_URN;
        Properties properties = new Properties();
        properties.setProperty("MaxLimit","897.98327");
        properties.setProperty(".String","before");
        mLoader.addResource(providerURN, properties);
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
        ModuleConfigurationProvider provider = createProvider();
        mManager.setConfigurationProvider(provider);
        mLoader.setFail(true);
        ModuleURN providerURN = ConfigurationProviderTestFactory.PROVIDER_URN;
        String propertiesName = DynamicResourceLoader.getPropertiesName(providerURN);
        mLoader.addResource(providerURN, new Properties());
        new ExpectedFailure<ModuleException>(Messages.ERROR_READ_PROPERTIES,
                propertiesName){
            protected void run() throws Exception {
                mManager.init();
            }
        };
    }

    /**
     * Tests failures from supplying null values to various methods.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void nulls() throws Exception {
        //null loader
        new ExpectedFailure<NullPointerException>(){
            @Override
            protected void run() throws Exception {
                createProvider(null);
            }
        };
        //null URN
        final ModuleConfigurationProvider provider = createProvider();
        new ExpectedFailure<NullPointerException>(){
            @Override
            protected void run() throws Exception {
                provider.getDefaultFor(null,"value");
            }
        };
        //null attribute name
        assertNull(provider.getDefaultFor(ConfigurationProviderTestFactory.PROVIDER_URN, null));
        //empty attribute name
        assertNull(provider.getDefaultFor(ConfigurationProviderTestFactory.PROVIDER_URN, ""));
    }

    /**
     * Creates a new provider instance using the supplied loader.
     * <p>
     * Subclasses may override this method to create a different type
     * of provider.
     *
     * @param inLoader the classloader instance.
     *
     * @return a new provider instance.
     */
    protected ModuleConfigurationProvider createProvider(ClassLoader inLoader) {
        return new PropertiesConfigurationProvider(inLoader);
    }

    /**
     * Creates a provider instance using {@link #mLoader}.
     *
     * @return a new provider instance.
     */
    protected final ModuleConfigurationProvider createProvider() {
        return createProvider(mLoader);
    }

    protected final DynamicResourceLoader mLoader = new DynamicResourceLoader();
}
