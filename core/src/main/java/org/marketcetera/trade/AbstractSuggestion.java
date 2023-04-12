package org.marketcetera.trade;

import java.math.BigDecimal;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public abstract class AbstractSuggestion
        implements Suggestion
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Suggestion#getIdentifier()
     */
    @Override
    public String getIdentifier()
    {
        return identifier;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Suggestion#setIdentifier(java.lang.String)
     */
    @Override
    public void setIdentifier(String inIdentifier)
    {
        identifier = inIdentifier;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Suggestion#getScore()
     */
    @Override
    public BigDecimal getScore()
    {
        return score;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.Suggestion#setScore(java.math.BigDecimal)
     */
    @Override
    public void setScore(BigDecimal inScore)
    {
        score = inScore;
    }
    /**
     * Create a new AbstractSuggestion instance.
     */
    protected AbstractSuggestion() {}
    /**
     * Create a new AbstractSuggestion instance.
     *
     * @param inIdentifier a <code>String<code> value
     * @param inScore a <code>BigDecimal</code> value
     */
    protected AbstractSuggestion(String inIdentifier,
                                 BigDecimal inScore)
    {
        setIdentifier(inIdentifier);
        setScore(inScore);
    }
    /**
     * identifier value
     */
    private String identifier;
    /**
     * score value
     */
    private BigDecimal score;
    private static final long serialVersionUID = 7713915144622410613L;
}
