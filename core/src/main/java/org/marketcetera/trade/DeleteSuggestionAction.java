package org.marketcetera.trade;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/* $License$ */

/**
 * Represents a suggestion that has been deleted.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class DeleteSuggestionAction
        implements HasSuggestionAction,HasSuggestionIdentifier
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasSuggestionAction#getSuggestionAction()
     */
    @Override
    public SuggestionAction getSuggestionAction()
    {
        return SuggestionAction.DELETE;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasSuggestionAction#setSuggestionAction(org.marketcetera.trade.SuggestionAction)
     */
    @Override
    public void setSuggestionAction(SuggestionAction inAction)
    {
        throw new UnsupportedOperationException();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.HasSuggestionIdentifier#getIdentifier()
     */
    @Override
    public String getIdentifier()
    {
        return identifier;
    }
    /**
     * Sets the identifier value.
     *
     * @param inIdentifier a <code>String</code> value
     */
    public void setIdentifier(String inIdentifier)
    {
        identifier = inIdentifier;
    }
    /**
     * Create a new DeleteSuggestionAction instance.
     */
    public DeleteSuggestionAction() {}
    
    /**
     * Create a new DeleteSuggestionAction instance.
     *
     * @param inIdentifier a <code>String</code> value
     */
    public DeleteSuggestionAction(String inIdentifier)
    {
        identifier = inIdentifier;
    }
    /**
     * identifies the suggestion
     */
    @XmlAttribute
    private String identifier;
}
