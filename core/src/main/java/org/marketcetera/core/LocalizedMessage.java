package org.marketcetera.core;

/**
 * Interface to introduce convenience methods of extracting localized message string
 * @author toli
 * @version $Id$
 */
public interface LocalizedMessage {
    /** Returns the localized and formatted message for this key.   */
    String getLocalizedMessage();

    /** Returns the localized and formatted message for this key with the specified arguments.   */
    String getLocalizedMessage(Object ... args);

}
