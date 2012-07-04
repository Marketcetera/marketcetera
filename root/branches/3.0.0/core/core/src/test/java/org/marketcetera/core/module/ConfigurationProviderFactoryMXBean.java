package org.marketcetera.core.module;

import java.math.BigDecimal;
import java.math.BigInteger;

import javax.management.MXBean;
import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */
/**
 * Management interface for {@link ConfigurationProviderTestFactory}.
 * This interface is used for testing if the configuration provider
 * can be used to set default properties on a factory.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id: ConfigurationProviderFactoryMXBean.java 82330 2012-04-10 16:29:13Z colin $")
@MXBean(true)
@DisplayName("Management interface for configuration provider bean")
public interface ConfigurationProviderFactoryMXBean {
    @DisplayName("Maximum Portfolio size")
    BigDecimal getMaxLimit();

    @DisplayName("Maximum Portfolio size")
    void setMaxLimit(
            @DisplayName("Maximum Portfolio size")
            BigDecimal inMaxLimit);

    @DisplayName("Number of instances created")
    BigInteger getInstances();
}
