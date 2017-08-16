package org.marketcetera.fix.provisioning;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.marketcetera.brokers.service.BrokerService;
import org.marketcetera.fix.FixSession;
import org.marketcetera.fix.FixSessionAttributeDescriptor;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 * Bootstraps {@link FixSession} values.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class FixSessionAttributeDescriptorInitializer
{
    /**
     * Validates and starts the object.
     */
    @PostConstruct
    public void start()
    {
        Validate.notNull(brokerService);
        if(descriptors == null) {
            return;
        }
        for(FixSessionAttributeDescriptor descriptor : descriptors) {
            try {
                brokerService.save(descriptor);
            } catch (Exception e) {
                SLF4JLoggerProxy.debug(this,
                                       "Not adding {}: {}",
                                       descriptor,
                                       ExceptionUtils.getRootCause(e));
            }
        }
    }
    /**
     * Get the brokerService value.
     *
     * @return a <code>BrokerService</code> value
     */
    public BrokerService getBrokerService()
    {
        return brokerService;
    }
    /**
     * Sets the brokerService value.
     *
     * @param inBrokerService a <code>BrokerService</code> value
     */
    public void setBrokerService(BrokerService inBrokerService)
    {
        brokerService = inBrokerService;
    }
    /**
     * Get the descriptors value.
     *
     * @return a <code>List&lt;FixSessionAttributeDescriptor&gt;</code> value
     */
    public List<FixSessionAttributeDescriptor> getDescriptors()
    {
        return descriptors;
    }
    /**
     * Sets the descriptors value.
     *
     * @param inDescriptors a <code>List&lt;FixSessionAttributeDescriptor&gt;</code> value
     */
    public void setDescriptors(List<FixSessionAttributeDescriptor> inDescriptors)
    {
        descriptors = inDescriptors;
    }
    /**
     * provides access to broker services
     */
    @Autowired
    private BrokerService brokerService;
    /**
     * objects to create on start
     */
    private List<FixSessionAttributeDescriptor> descriptors;
}
