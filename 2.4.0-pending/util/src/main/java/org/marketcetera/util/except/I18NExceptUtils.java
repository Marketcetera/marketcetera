package org.marketcetera.util.except;

import org.marketcetera.util.misc.ClassVersion;

/**
 * Utilities supporting message generation.
 * 
 * @author tlerios@marketcetera.com
 * @since 0.5.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
final class I18NExceptUtils
{

    // CLASS METHODS.

    /**
     * Returns the localized message of the given internationalized
     * throwable, as implemented by {@link
     * Throwable#getLocalizedMessage()}.
     *
     * @param t The throwable.
     *
     * @return The message.
     */

    static String getLocalizedMessage
        (I18NThrowable t)
    {
        if (t.getI18NBoundMessage()==null) {
            return t.getMessage();
        }
        return t.getI18NBoundMessage().getText();
    }

    /**
     * Returns the raw message of the given internationalized
     * throwable, possibly combined with the raw message of the
     * throwable's underlying cause.
     *
     * @param t The throwable.
     *
     * @return The message.
     */

    static String getDetail
        (I18NThrowable t)
    {
        String selfMessage=null;
        if (t.getI18NBoundMessage()!=null) {
            selfMessage=t.getMessage();
        }
        String causeMessage=null;
        Throwable cause=t.getCause();
        if (cause!=null) {
            if (cause instanceof I18NThrowable) {
                causeMessage=((I18NThrowable)cause).getDetail();
            } else {
                causeMessage=cause.getMessage();
            }
        }
        if ((selfMessage!=null) && (causeMessage!=null)) {
            return selfMessage+
                " ("+causeMessage+")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (selfMessage!=null) {
            return selfMessage;
        }
        return causeMessage;
    }

    /**
     * Returns the localized message of the given internationalized
     * throwable, possibly combined with the localized message of the
     * throwable's underlying cause.
     *
     * @param t The throwable.
     *
     * @return The message.
     */

    static String getLocalizedDetail
        (I18NThrowable t)
    {
        String selfMessage=null;
        if (t.getI18NBoundMessage()!=null) {
            selfMessage=t.getLocalizedMessage();
        }
        String causeMessage=null;
        Throwable cause=t.getCause();
        if (cause!=null) {
            if (cause instanceof I18NThrowable) {
                causeMessage=((I18NThrowable)cause).getLocalizedDetail();
            } else {
                causeMessage=cause.getLocalizedMessage();
            }
        }
        if ((selfMessage!=null) && (causeMessage!=null)) {
            return Messages.COMBINE_MESSAGES.getText(selfMessage,causeMessage);
        }
        if (selfMessage!=null) {
            return selfMessage;
        }
        return causeMessage;
    }


    // CONSTRUCTORS.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private I18NExceptUtils() {}
}
