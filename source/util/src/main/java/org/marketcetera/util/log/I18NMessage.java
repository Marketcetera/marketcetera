package org.marketcetera.util.log;

import java.io.Serializable;
import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.misc.ClassVersion;

/**
 * An internationalized message, represented using a pair of textual
 * keys referencing the message text; keys-text maps are stored
 * outside the source code. This key pair comprises a message ID and
 * an entry ID: the entry ID selects a specific variant of the message
 * and can be omitted (in which case it defaults to {@link
 * #UNKNOWN_ENTRY_ID}).
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public abstract class I18NMessage
    implements Serializable
{

    // CLASS DATA.

    private static final long serialVersionUID=1L;
        
    /**
     * The default entry ID.
     */

    public static final String UNKNOWN_ENTRY_ID=
        "msg"; //$NON-NLS-1$


    // INSTANCE DATA.

    private I18NLoggerProxy mLoggerProxy;
    private String mMessageId;
    private String mEntryId;


    // CONSTRUCTORS.

    /**
     * Creates a new internationalized message with the given message
     * and entry IDs. The logger proxy that can log the receiver is
     * also supplied.
     *
     * @param loggerProxy The logger proxy.
     * @param messageId The message ID.
     * @param entryId The entry ID.
     */

    public I18NMessage
        (I18NLoggerProxy loggerProxy,
         String messageId,
         String entryId)
    {
        mLoggerProxy=loggerProxy;
        mMessageId=messageId;
        mEntryId=entryId;
    }

    /**
     * Creates a new internationalized message with the given message
     * ID and the default entry ID. The logger proxy that can log the
     * receiver is also supplied.
     *
     * @param loggerProxy The logger proxy.
     * @param messageId The message ID.
     */

    public I18NMessage
        (I18NLoggerProxy loggerProxy,
         String messageId)
    {
        this(loggerProxy,messageId,UNKNOWN_ENTRY_ID);
    }


    // INSTANCE METHODS.
    
    /**
     * Returns the logger proxy that can log the receiver.
     *
     * @return The proxy.
     */

    public I18NLoggerProxy getLoggerProxy()
    {
        return mLoggerProxy;
    }

    /**
     * Returns the message provider that can map the receiver.
     *
     * @return The message provider.
     */

    public I18NMessageProvider getMessageProvider()
    {
        return getLoggerProxy().getMessageProvider();
    }

    /**
     * Returns the receiver's message ID.
     *
     * @return The ID.
     */

    public String getMessageId()
    {
        return mMessageId;
    }

    /**
     * Returns the receiver's entry ID.
     *
     * @return The ID.
     */

    public String getEntryId()
    {
        return mEntryId;
    }

    /**
     * Returns the number of parameters the receiver expects.
     *
     * @return The number of parameters; -1 indicates a variable number.
     */

    public abstract int getParamCount();


    // Object.

    @Override
    public int hashCode()
    {
        return (getParamCount()+
                ObjectUtils.hashCode(getLoggerProxy())+
                ObjectUtils.hashCode(getMessageId())+
                ObjectUtils.hashCode(getEntryId()));
    }

    @Override
    public boolean equals
        (Object other)
    {
        if (this==other) {
            return true;
        }
        if ((other==null) || !getClass().equals(other.getClass())) {
            return false;
        }
        I18NMessage o=(I18NMessage)other;
        return ((getParamCount()==o.getParamCount()) &&
                ObjectUtils.equals(getLoggerProxy(),o.getLoggerProxy()) &&
                ObjectUtils.equals(getMessageId(),o.getMessageId()) &&
                ObjectUtils.equals(getEntryId(),o.getEntryId()));
    }
}
