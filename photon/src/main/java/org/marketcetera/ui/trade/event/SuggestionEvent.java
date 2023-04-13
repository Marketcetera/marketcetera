package org.marketcetera.ui.trade.event;

import org.marketcetera.trade.HasSuggestion;
import org.marketcetera.trade.Suggestion;
import org.marketcetera.ui.events.NewWindowEvent;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/* $License$ */

/**
 * Indicates that a trade suggestion has been triggered.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class SuggestionEvent
        extends AbstractOrderTicketEvent
        implements NewWindowEvent,HasSuggestion
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasSuggestion#getSuggestion()
     */
    @Override
    public Suggestion getSuggestion()
    {
        return suggestion;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.web.events.NewWindowEvent#getWindowTitle()
     */
    @Override
    public String getWindowTitle()
    {
        return title;
    }
    /**
     * Create a new SuggestionEvent instance.
     *
     * @param inTitle a <code>String</code> value
     * @param inSuggestion a <code>Suggestion</code> value
     */
    public SuggestionEvent(String inTitle,
                           Suggestion inSuggestion)
    {
        title = inTitle;
        suggestion = inSuggestion;
    }
    /**
     * title value
     */
    private final String title;
    /**
     * suggestion value
     */
    private final Suggestion suggestion;
}
