package org.marketcetera.trade;

import java.io.Serializable;

/* $License$ */

/**
 * Provides an {@link HasSuggestionCommand} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SuggestionCommandCarrier
        implements HasSuggestionCommand, Serializable
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Suggestion#getSuggestionCommand()
     */
    @Override
    public SuggestionCommand getSuggestionCommand()
    {
        return suggestionCommand;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Suggestion#setSuggestionCommand(org.marketcetera.trade.SuggestionCommand)
     */
    @Override
    public void setSuggestionCommand(SuggestionCommand inCommand)
    {
        suggestionCommand = inCommand;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("SuggestionCommandCarrier [suggestionCommand=").append(suggestionCommand).append("]");
        return builder.toString();
    }
    /**
     * underlying command
     */
    private SuggestionCommand suggestionCommand;
    private static final long serialVersionUID = 789047109066818745L;
}
