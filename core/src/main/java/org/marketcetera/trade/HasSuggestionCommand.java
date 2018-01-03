package org.marketcetera.trade;

/* $License$ */

/**
 * Indicates a {@link SuggestionCommand} carrier.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public interface HasSuggestionCommand
{
    /**
     * Get the suggestion command indicating how to process this suggestion.
     *
     * @return a <code>SuggestionCommand</code> value
     */
    SuggestionCommand getSuggestionCommand();
    /**
     * Set the suggestion command value.
     *
     * @param inCommand a <code>SuggestionCmmand</code> value
     */
    void setSuggestionCommand(SuggestionCommand inCommand);
}
