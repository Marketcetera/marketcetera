package org.marketcetera.modules.requester;

import org.marketcetera.core.publisher.ISubscriber;
import org.marketcetera.module.DataRequest;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Provides access to module data flows.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: Requester.java 82384 2012-07-20 19:09:59Z colin $
 * @since $Release$
 */
@ClassVersion("$Id: Requester.java 82384 2012-07-20 19:09:59Z colin $")
public interface Requester
{
    /**
     * Subscribe to the given data flow and deliver the results to the given subscriber.
     *
     *
     * @param inSubscriber
     * @param inRequests
     * @throws DataFlowException if the data flow cannot be created
     */
    public void subscribeToDataFlow(ISubscriber inSubscriber,
                                    DataRequest[] inRequests);
    /**
     * 
     *
     *
     * @param inSubscriber
     */
    public void cancelSubscription(ISubscriber inSubscriber);
}
