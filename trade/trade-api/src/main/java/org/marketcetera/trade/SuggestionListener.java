package org.marketcetera.trade;

/* $License$ */

/**
 * Listens for trade suggestions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface SuggestionListener
{
    /**
     * Receive a trade suggestion.
     *
     * @param inSuggestion a <code>Suggestion</code> value
     */
    void receiveSuggestion(Suggestion inSuggestion);
}
