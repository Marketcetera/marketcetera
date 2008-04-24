package org.marketcetera.event;

import java.util.Date;

import quickfix.Message;

/**
 * Base class for all market events.
 *
 * @author gmiller
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.43-SNAPSHOT
 */
public abstract class EventBase 
{
    /**
     * unique identifier for this market event
     */
    private final long messageId;
    /**
     * milliseconds since EPOCH in GMT
     */
    private final long timestamp;
    /**
     * underlying FIX message for this market event
     */
	private final Message fixMessage;

    /**
     * Create a new <code>EventBase</code> instance.
     *
     * @param messageId a <code>long</code> value uniquely identifying this market event
     * @param timestamp a <code>long</code> value expressing the time this event occurred in milliseconds
     *  since <code>EPOCH</code> in GMT
     */
    protected EventBase(long messageId, 
                        long timestamp) 
    {
        this(messageId,
             timestamp,
             null);
    }

    /**
     * Create a new EventBase instance.
     *
     * @param messageId a <code>long</code> value uniquely identifying this market event
     * @param timestamp a <code>long</code> value expressing the time this event occurred in milliseconds since
     *   EPOCH in GMT
     * @param fixMessage a <code>Message</code> value encapsulating the underlying market event
     */
    protected EventBase(long messageId, 
                        long timestamp, 
                        Message fixMessage) 
    {
        this.messageId = messageId;
        this.timestamp = timestamp;
    	this.fixMessage = fixMessage;
    }

    /**
     * Returns the unique message identifier.
     *
     * @return a <code>long</code> value
     */
    public long getMessageId() 
    {
        return messageId;
    }

    /**
     * Returns the time the event took place.
     *
     * @return a <code>long</code> value containing the number of milliseconds since EPOCH in GMT
     */
    public long getTimestamp() 
    {
        return timestamp;
    }
    
    /**
     * Returns the underlying FIX message for this event.
     *
     * @return a <code>Message</code> value or null if no <code>Message</code> was specified at creation
     */
    public Message getFIXMessage()
    {
    	return fixMessage;
    }
    
    /**
     * Returns the time the event took place expressed as a <code>Date</code>.
     *
     * @return a <code>Date</code> value
     */
    public Date getTimestampAsDate()
    {
        return new Date(getTimestamp());
    }
}