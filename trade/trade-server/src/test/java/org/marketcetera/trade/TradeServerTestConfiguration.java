package org.marketcetera.trade;

import org.assertj.core.util.Lists;
import org.marketcetera.brokers.SessionCustomization;
import org.marketcetera.fix.FixServerTestConfiguration;
import org.marketcetera.fix.provisioning.SimpleSessionCustomization;
import org.marketcetera.trade.service.FieldSetterMessageModifier;
import org.marketcetera.trade.service.TestBrokerSelector;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

/* $License$ */

/**
 * Provides test configuration for trade server tests.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@SpringBootConfiguration
@EnableAutoConfiguration
public class TradeServerTestConfiguration
        extends FixServerTestConfiguration
{
    /**
     * Get the test broker selector value.
     *
     * @return a <code>TestBrokerSelector</code> value
     */
    @Bean
    public TestBrokerSelector getTestBrokerSelector()
    {
        return new TestBrokerSelector();
    }
    /**
     * Get the session customization1 value.
     *
     * @return a <code>SessionCustomization</code>
     */
    @Bean("sessionCustomization1")
    public static SessionCustomization getSessionCustomization1()
    {
        SimpleSessionCustomization sessionCustomization = new SimpleSessionCustomization();
        sessionCustomization.setName("sessionCustomization1");
        FieldSetterMessageModifier modifier1 = new FieldSetterMessageModifier();
        modifier1.setField(10000);
        modifier1.setValue("bogus-value");
        FieldSetterMessageModifier modifier2 = new FieldSetterMessageModifier();
        modifier2.setField(10000);
        modifier2.setValue("10000-sessionCustomization1");
        FieldSetterMessageModifier modifier3 = new FieldSetterMessageModifier();
        modifier3.setField(10001);
        modifier3.setValue("10001-sessionCustomization1");
        sessionCustomization.setOrderModifiers(Lists.newArrayList(modifier1,modifier2,modifier3));
        return sessionCustomization;
    }
    /**
     * Get the session customization2 value.
     *
     * @return a <code>SessionCustomization</code>
     */
    @Bean("sessionCustomization2")
    public static SessionCustomization getSessionCustomization2()
    {
        SimpleSessionCustomization sessionCustomization = new SimpleSessionCustomization();
        sessionCustomization.setName("sessionCustomization2");
        FieldSetterMessageModifier modifier1 = new FieldSetterMessageModifier();
        modifier1.setField(10001);
        modifier1.setValue("bogus-value");
        FieldSetterMessageModifier modifier2 = new FieldSetterMessageModifier();
        modifier2.setField(10001);
        modifier2.setValue("10001-sessionCustomization2");
        FieldSetterMessageModifier modifier3 = new FieldSetterMessageModifier();
        modifier3.setField(10002);
        modifier3.setValue("10002-sessionCustomization2");
        sessionCustomization.setOrderModifiers(Lists.newArrayList(modifier1,modifier2,modifier3));
        return sessionCustomization;
    }
}
