package org.marketcetera.core.publisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/**
 * Executes a single publication.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.5.0
 */
@ClassVersion("$Id$")
final class PublisherEngineNotifier
        implements Callable<Object>
{
    /**
     * the list of subscribers who should be notified in the given order
     */
    private final List<? extends ISubscriber> mSubscribers;
    /**
     * the data to publish to the subscribers
     */
    private final Object mData;
    /**
     * Create a new <code>PublisherEngineNotifier</code> object.
     *
     * @param inSubscribers a <code>List&lt;? extends ISubscriber&gt;</code> value indicating to whom to publish
     * @param inData an <code>Object</code> value indicating what to publish
     */
    PublisherEngineNotifier(List<? extends ISubscriber> inSubscribers,
                            Object inData)
    {
        if(inSubscribers == null) {
            mSubscribers = new ArrayList<ISubscriber>();
        } else {
            mSubscribers = Collections.unmodifiableList(inSubscribers);
        }
        mData = inData;
    }
    /* (non-Javadoc)
     * @see java.util.concurrent.Callable#call()
     */
    public Object call()
        throws Exception
    {
        SLF4JLoggerProxy.debug(this, "Publishing {} to {} subscriber(s)", //$NON-NLS-1$
                               mData, (mSubscribers == null ? "0" : mSubscribers.size())); //$NON-NLS-1$
        if(mSubscribers == null) {
            return null;
        }
        for(ISubscriber s : mSubscribers) {
            try {
                if(s.isInteresting(mData)) {
                    s.publishTo(mData);
                }
            } catch (Throwable t) {
                SLF4JLoggerProxy.debug(this, t, "Subscriber {} threw an exception during publication, skipping", s); //$NON-NLS-1$
            }
        }
        return null;
    }
}
