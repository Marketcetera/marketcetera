package org.marketcetera.util.log;

import org.marketcetera.core.ClassVersion;

/**
 * An internationalized message, represented using a pair of textual
 * keys referencing the message text; keys-text maps are stored
 * outside the source code. This key pair comprises a message ID and
 * an entry ID: the entry ID selects a specific variant of the message
 * and can be omitted (in which case it defaults to {@link
 * #UNKNOWN_ENTRY_ID}).
 * 
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class I18NMessage
{

	// CLASS DATA.
	
	/**
     * The default entry ID.
     */

	public static final String UNKNOWN_ENTRY_ID="msg";	
	

    // INSTANCE DATA.

	private String mMessageId;
	private String mEntryId;


    // CONSTRUCTORS.

    /**
     * Creates a new internationalized message with the given message
     * and entry IDs.
     *
     * @param messageId The message ID.
     * @param entryId The entry ID.
     */

	public I18NMessage
        (String messageId,
         String entryId)
	{
		mMessageId=messageId;
		mEntryId=entryId;
	}

    /**
     * Creates a new internationalized message with the given message
     * ID and the default entry ID.
     *
     * @param messageId The message ID.
     */

	public I18NMessage
        (String messageId)
	{
		this(messageId,UNKNOWN_ENTRY_ID);
	}


	// INSTANCE METHODS.
    
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
}
