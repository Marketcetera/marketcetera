package org.marketcetera.strategy;

import java.util.HashMap;
import java.util.Map;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.module.DataEmitterSupport;
import org.marketcetera.module.RequestID;

/**
 * Describes the types of data that a strategy can emit.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public enum OutputType
{
    /**
     * orders created by this strategy
     */
    ORDERS,
    /**
     * trade suggestions created by this strategy
     */
    SUGGESTIONS;
    /**
     * Adds the given subscriber to the list of subscribers to this request type.
     *
     * @param inSubscriber a <code>DataEmitterSupport</code> value
     */
    synchronized void subscribe(DataEmitterSupport inSubscriber)
    {
        subscribers.put(inSubscriber.getRequestID(),
                        inSubscriber);
    }
    /**
     *  Removes the given subscriber from the appropriate list of subscribers. 
     *
     * @param inRequestID a <code>RequestID</code> value
     */
    static synchronized void unsubscribe(RequestID inRequestID)
    {
        ORDERS.subscribers.remove(inRequestID);
        SUGGESTIONS.subscribers.remove(inRequestID);
    }
    /**
     * Publishes the given value to subscribers of this type. 
     *
     * @param inObject an <code>Object</code> value
     */
    synchronized void publish(Object inObject)
    {
        for(DataEmitterSupport subscriber : subscribers.values()) {
            subscriber.send(inObject);
        }
    }
    /**
     * the collection of subscribers to this request type by requestID
     */
    private final Map<RequestID,DataEmitterSupport> subscribers = new HashMap<RequestID,DataEmitterSupport>();
}