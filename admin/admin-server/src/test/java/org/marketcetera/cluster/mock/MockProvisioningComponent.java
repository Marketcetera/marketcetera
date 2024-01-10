package org.marketcetera.cluster.mock;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.PostConstruct;

import org.marketcetera.admin.provisioning.ProvisioningAgent;
import org.marketcetera.core.Cacheable;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Mock provisioning component used to test {@link ProvisioningAgent}.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MockProvisioningComponent
        implements Cacheable
{
    /* (non-Javadoc)
     * @see org.marketcetera.core.Cacheable#clear()
     */
    @Override
    public void clear()
    {
        invoked.set(0);
        instance = null;
    }
    /**
     * Validate and start the object.
     *
     * @throws Exception if an error occurs
     */
    @PostConstruct
    public void start()
            throws Exception
    {
        instance = this;
        SLF4JLoggerProxy.info(this,
                              "{} invoked",
                              getClass().getSimpleName());
        invoked.incrementAndGet();
    }
    /**
     * Get the instance value.
     *
     * @return a <code>MockProvisioningComponent</code> value
     */
    public static MockProvisioningComponent getInstance()
    {
        return instance;
    }
    /**
     * Get the invoked value.
     *
     * @return an <code>int</code> value
     */
    public int getInvoked()
    {
        return invoked.get();
    }
    /**
     * static instance
     */
    private static MockProvisioningComponent instance;
    /**
     * number of times this object has been invoked
     */
    private final AtomicInteger invoked = new AtomicInteger(0);
}
