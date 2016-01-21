package org.marketcetera.modules.publisher;

import java.util.concurrent.atomic.AtomicInteger;

import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.module.Module;
import org.marketcetera.module.ModuleCreationException;
import org.marketcetera.module.ModuleFactory;
import org.marketcetera.module.ModuleURN;

/* $License$ */

/**
 * Creates {@link PublisherModule} instances.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PublisherModuleFactory
        extends ModuleFactory
{
    /**
     * Create a new PublisherModuleFactory instance.
     */
    public PublisherModuleFactory()
    {
        super(PROVIDER_URN,
              Messages.PROVIDER_DESCRIPTION,
              true,
              false,
              ISubscriber.class);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.module.ModuleFactory#create(java.lang.Object[])
     */
    @Override
    public Module create(Object... inParameters)
            throws ModuleCreationException
    {
        if(inParameters == null || inParameters.length != 1) {
            throw new ModuleCreationException(Messages.PARAMETER_COUNT_ERROR);
        }
        ISubscriber subscriber = (ISubscriber)inParameters[0];
        return new PublisherModule(new ModuleURN(PROVIDER_URN,
                                                 "instance"+counter.incrementAndGet()),
                                                 subscriber);
    }
    /**
     * creates unique instance urn values
     */
    private static final AtomicInteger counter = new AtomicInteger(0);
    /**
     * unique provider URN for the receiver module
     */
    public static final ModuleURN PROVIDER_URN = new ModuleURN("metc:misc:publisher");  //$NON-NLS-1$
}
