package org.marketcetera.core.event;

import org.marketcetera.api.attributes.ClassVersion;

/* $License$ */
/**
 * Marker interface for events that carry the time stamp of the event.
 * This interface is used by various {@link org.marketcetera.module modules}
 * to externalize the notion of time. Modules processing such events can
 * be programmed to derive their notion of time from the time stamp on
 * such events.
 *
 * @author anshul@marketcetera.com
 * @version $Id: TimestampCarrier.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: TimestampCarrier.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
public interface TimestampCarrier {
    /**
     * Returns the time stamp on the event, in milliseconds .
     * The returned value should be a long value in UTC per
     * {@link java.util.Date#getTime()}.
     *
     * @return the UTC time stamp of the event in milliseconds.
     */
    public long getTimeMillis();
}
