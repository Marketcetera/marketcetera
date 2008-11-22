package org.marketcetera.messagehistory;

import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.core.ClassVersion;

import quickfix.Message;

/* $License$ */

/**
 * Represents a Photon market data request.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.4.2
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class MessageHolder 
	implements Comparable<MessageHolder> 
{
	private Message message;
	private long mMessageReference;
	private static AtomicLong counter = new AtomicLong();
	private String mGroupID = null;

	public MessageHolder(Message message) {
		this.message = message;
		this.mMessageReference = counter.incrementAndGet();
	}

	public MessageHolder(Message message, String groupID){
		this(message);
		this.mGroupID = groupID;
	}

    public Message getMessage() {
		return message;
	}
	
	public long getMessageReference()
	{
		return mMessageReference;
	}

	public int compareTo(MessageHolder mh) {  
		return (int)(mMessageReference - mh.mMessageReference);
	}

	public String getGroupID() {
		return mGroupID;
	}
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (mMessageReference ^ (mMessageReference >>> 32));
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final MessageHolder other = (MessageHolder) obj;
        if (mMessageReference != other.mMessageReference)
            return false;
        return true;
    }
}
