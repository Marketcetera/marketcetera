package org.marketcetera.photon;

import org.marketcetera.trade.Suggestion;

/* $License$ */

/**
 * Indicates that the implementer is interested in suggestions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface ISuggestionReceiver
{
    /**
     * Accepts suggestions.
     *
     * @param inSuggestion a <code>Suggestion</code> value
     * @param inSource a <code>String</code> value
     */
    public void accept(Suggestion inSuggestion,
                       String inSource);
}
