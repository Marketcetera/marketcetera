package org.marketcetera.event;

import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * An event that is used to communicate time between modules. Instances
 * of this event are emitted when a module wants to only communicate
 * time to its downstream module.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class TimeEvent implements TimestampCarrier {
    /**
     * Creates an instance, given the time in milliseconds.
     *
     * @param inTimeMillis the time in milliseconds
     */
    public TimeEvent(long inTimeMillis) {
        mTimeMillis = inTimeMillis;
    }

    @Override
    public long getTimeMillis() {
        return mTimeMillis;
    }
    private final long mTimeMillis;
}
