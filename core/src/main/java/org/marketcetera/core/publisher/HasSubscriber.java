package org.marketcetera.core.publisher;

/* $License$ */

/**
 * Indicates the implementor has a {@link Subscriber} value.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasSubscriber
{
    /**
     * Get the subscriber value.
     *
     * @return a <code>Subscriber</code> value
     */
    Subscriber getSubscriber();
    /**
     * Set the subscriber value.
     *
     * @param inSubscriber a <code>Subscriber</code> value
     */
    void setSubscriber(Subscriber inSubscriber);
}
