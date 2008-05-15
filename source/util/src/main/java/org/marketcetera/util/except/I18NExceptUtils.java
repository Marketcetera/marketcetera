package org.marketcetera.util.except;

import org.marketcetera.core.ClassVersion;

/**
 * Utilities supporting message generation.
 * 
 * @author tlerios
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
final class I18NExceptUtils
{

    // CLASS METHODS.

    /**
     * Returns a string combining the two given strings. A null string
     * is ignored; if both strings are null, null is returned.
     *
     * @param first The first string.
     * @param second The second string.
     *
     * @return The combination.
     */

    private static String combine
        (String first,
         String second)
    {
        if ((first!=null) && (second!=null)) {
            return first+" ("+second+")";
        }
        if (first!=null) {
            return first;
        }
        return second;
    }                                  

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
    	if (t.getI18NMessage()==null) {
	    	return t.getMessage();
	    }
    	return t.getI18NMessage().getText(t.getParams());
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
    	if (t.getI18NMessage()!=null) {
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
    	return combine(selfMessage,causeMessage);
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
    	if (t.getI18NProvider()!=null) {
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
    	return combine(selfMessage,causeMessage);
    }


    // CONSTRUCTORS.

    /**
     * Constructor. It is private so that no instances can be created.
     */

    private I18NExceptUtils() {}
}
