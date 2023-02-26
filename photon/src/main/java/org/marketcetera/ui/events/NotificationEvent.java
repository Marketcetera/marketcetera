package org.marketcetera.ui.events;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class NotificationEvent
{
    /**
     * Create a new NotificationEvent instance.
     *
     * @param inMessage
     */
    public NotificationEvent(String inMessage)
    {
        message = inMessage;
    }
    /**
     * Get the message value.
     *
     * @return a <code>String</code> value
     */
    public String getMessage()
    {
        return message;
    }
    /**
     * Sets the message value.
     *
     * @param inMessage a <code>String</code> value
     */
    public void setMessage(String inMessage)
    {
        message = inMessage;
    }
    private String message;
}
