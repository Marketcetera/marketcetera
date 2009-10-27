package org.marketcetera.event;

import org.marketcetera.util.misc.ClassVersion;

/**
 * Indicates the action to be taken.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 0.6.0
 */
@ClassVersion("$Id$")
public enum QuoteAction 
{
    /**
     * the quote should be added
     */
    ADD,
    /**
     * the quote should replace an existing quote
     */
    CHANGE,
    /**
     * the quote should be deleted
     */
    DELETE
}