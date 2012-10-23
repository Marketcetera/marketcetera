package org.marketcetera.core.module;

import java.math.BigInteger;

import javax.management.JMX;
import org.junit.After;

/* $License$ */
/**
 * Base class for testing configuration provider integration
 * with the module manager.
 *
 */
public class ConfigurationProviderTestBase extends ModuleTestBase {
    @After
    public void cleanup() throws Exception {
        if(mManager != null) {
            mManager.stop();
        }
    }

    /**
     * Verify values of provider / instance beans when no default
     * values have been supplied by the configuration provider.
     *
     * @throws org.marketcetera.core.module.ModuleException if there were unexpected errors
     */
    protected void verifyEmptyValues() throws ModuleException {
        ConfigurationProviderFactoryMXBean factory = JMX.newMXBeanProxy(
                getMBeanServer(),
                ConfigurationProviderTestFactory.PROVIDER_URN.toObjectName(),
                ConfigurationProviderFactoryMXBean.class);
        org.junit.Assert.assertNull(factory.getMaxLimit());
        org.junit.Assert.assertEquals(new BigInteger("0"),factory.getInstances());
        //Create a module and verify all its values are empty
        ModuleURN urn = new ModuleURN(ConfigurationProviderTestFactory.PROVIDER_URN,
                "empty");
        mManager.createModule(ConfigurationProviderTestFactory.PROVIDER_URN,
                urn);
        JMXTestModuleMXBean module = JMX.newMXBeanProxy(getMBeanServer(),
                urn.toObjectName(), JMXTestModuleMXBean.class);
        org.junit.Assert.assertNull(module.getBoolean());
        org.junit.Assert.assertNull(module.getByte());
        org.junit.Assert.assertNull(module.getCharacter());
        org.junit.Assert.assertNull(module.getDecimal());
        org.junit.Assert.assertNull(module.getDouble());
        org.junit.Assert.assertNull(module.getFactoryAnnotation());
        org.junit.Assert.assertNull(module.getFile());
        org.junit.Assert.assertNull(module.getFloat());
        org.junit.Assert.assertNull(module.getInt());
        org.junit.Assert.assertNull(module.getInteger());
        org.junit.Assert.assertNull(module.getLong());
        org.junit.Assert.assertEquals(false,module.isPrimBoolean());
        org.junit.Assert.assertEquals(0,module.getPrimByte());
        org.junit.Assert.assertEquals('\u0000', module.getPrimCharacter());
        org.junit.Assert.assertEquals(0.0, module.getPrimDouble(),0.0);
        org.junit.Assert.assertEquals(0.0f, module.getPrimFloat(),0.0);
        org.junit.Assert.assertEquals(0, module.getPrimInt());
        org.junit.Assert.assertEquals(0l, module.getPrimLong());
        org.junit.Assert.assertEquals(0,module.getPrimShort());
        org.junit.Assert.assertNull(module.getShort());
        org.junit.Assert.assertNull(module.getString());
        org.junit.Assert.assertNull(module.getURL());
    }

    protected ModuleManager mManager;
}
