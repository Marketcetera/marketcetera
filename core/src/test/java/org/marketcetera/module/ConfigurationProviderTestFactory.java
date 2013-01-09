package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import java.math.BigDecimal;
import java.math.BigInteger;

/* $License$ */
/**
 * A factory to test module configuration provider.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class ConfigurationProviderTestFactory
        extends ModuleFactory
        implements ConfigurationProviderFactoryMXBean {

    public ConfigurationProviderTestFactory() {
        this(PROVIDER_URN);
    }
    protected ConfigurationProviderTestFactory(ModuleURN inProviderURN) {
        super(inProviderURN, TestMessages.MULTIPLE_4_PROVIDER,
                true, false, ModuleURN.class);
    }
    public JMXTestModule create(Object... parameters)
            throws ModuleCreationException {
        mInstances = mInstances.add(ONE);
        return new JMXTestModule((ModuleURN) parameters[0]);
    }

    /**
     * A writable attribute. The value can be null.
     *
     * @return the value.
     */
    public BigDecimal getMaxLimit() {
        return mMaxLimit;
    }

    public void setMaxLimit(BigDecimal inMaxLimit) {
        mMaxLimit = inMaxLimit;
    }

    /**
     * A readonly attribute.
     *
     * @return the value.
     */
    public BigInteger getInstances() {
        return mInstances;
    }

    private BigDecimal mMaxLimit;
    private BigInteger mInstances = new BigInteger("0");
    static final ModuleURN PROVIDER_URN = new ModuleURN("metc:test:multiple4");
    private static final BigInteger ONE = new BigInteger("1");
}
