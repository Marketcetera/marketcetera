package org.marketcetera.trade;

/* $License$ */

/**
 * Indicates a {@link SuggestionAction} carrier.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasSuggestionAction
{
    /**
     * Get the suggestion action indicating how to process this suggestion.
     *
     * @return a <code>SuggestionAction</code> value
     */
    SuggestionAction getSuggestionAction();
    /**
     * Set the suggestion action value.
     *
     * @param inAction a <code>SuggestionAction</code> value
     */
    void setSuggestionAction(SuggestionAction inAction);
}
