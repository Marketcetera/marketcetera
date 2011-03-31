package org.marketcetera.server.service;

import org.marketcetera.server.ws.impl.ClientSession;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides publication services.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public interface DataPublicationManager
        extends SessionListener<ClientSession>
{
    /**
     * Receives an <code>Object</code> to disseminate.
     *
     * @param inObject a <code>Object</code> value
     */
    public void publish(Object inObject);
    /**
     * 
     *
     *
     * @param inDataSubscriber
     */
    public void subscribe(DataSubscriber inDataSubscriber);
    /**
     * 
     *
     *
     * @param inDataSubscriber
     */
    public void unsubscribe(DataSubscriber inDataSubscriber);
}
