package org.marketcetera.trade;

/* $License$ */

/**
 * Indicates that the implementing class has a suggestion identifier.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasSuggestionIdentifier
{
    /**
     * Uniquely identifies a suggestion.
     *
     * @return a <code>String</code> value
     */
    String getIdentifier();
}
