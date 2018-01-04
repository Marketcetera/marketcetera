package org.marketcetera.trade;

import javax.annotation.concurrent.Immutable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/* $License$ */

/**
 * Represents a request to refresh all suggestions.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Immutable
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class RefreshSuggestionAction
        implements HasSuggestionAction
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasSuggestionAction#getSuggestionAction()
     */
    @Override
    public SuggestionAction getSuggestionAction()
    {
        return SuggestionAction.REFRESH;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasSuggestionAction#setSuggestionAction(org.marketcetera.trade.SuggestionAction)
     */
    @Override
    public void setSuggestionAction(SuggestionAction inAction)
    {
        throw new UnsupportedOperationException();
    }
    public static transient final RefreshSuggestionAction instance = new RefreshSuggestionAction();
}
