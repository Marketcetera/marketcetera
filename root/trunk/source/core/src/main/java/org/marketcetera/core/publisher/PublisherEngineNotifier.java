package org.marketcetera.core.publisher;

import org.marketcetera.core.LoggerAdapter;

/**
 * Executes a single publication.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id: $
 * @since 0.43-SNAPSHOT
 */
public class PublisherEngineNotifier
        implements Runnable
{
    /**
     * the list of subscribers who should be notified in the given order
     */
    private final Subscriber[] mSubscribers;
    /**
     * the data to publish to the subscribers
     */
    private final Object mData;
    
    /**
     * Create a new <code>PublisherEngineNotifier</code> object.
     *
     * @param inSubscribers a <code>Subscriber[]</code> value indicating to whom to publish
     * @param inData an <code>Object</code> value indicating what to publish
     */
    PublisherEngineNotifier(Subscriber[] inSubscribers,
                            Object inData)
    {
        mSubscribers = inSubscribers;
        mData = inData;
    }
    
    public void run()
    {
        if(LoggerAdapter.isDebugEnabled(this)) {
            LoggerAdapter.debug("Publishing " + mData + " to " + (mSubscribers == null ? "0" : mSubscribers.length) + " subscriber(s)", 
                            this);
        }
        if(mSubscribers == null) {
            return;
        }
        for(Subscriber s : mSubscribers) {
            try {
                if(s.isInteresting(mData)) {
                    s.publishTo(mData);
                }
            } catch (Throwable t) {
                if(LoggerAdapter.isDebugEnabled(this)) {
                    LoggerAdapter.debug("Subscriber " + s + " threw an exception during publication, skipping",
                                        t,
                                        this);
                }
            }
        }
    }
}
