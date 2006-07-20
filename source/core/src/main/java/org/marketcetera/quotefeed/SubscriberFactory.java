/*
 * Created by IntelliJ IDEA.
 * User: gmiller
 * Date: May 30, 2006
 * Time: 1:42:05 PM
 */
package org.marketcetera.quotefeed;

import java.util.Hashtable;

public interface SubscriberFactory {
    public MessageSubscriber createMessageSubscriber(Hashtable env) throws QuoteFeedException;
}