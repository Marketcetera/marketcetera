package org.marketcetera.core.trade;

import java.io.Serializable;
import java.math.BigDecimal;

import org.marketcetera.core.attributes.ClassVersion;

/* $License$ */
/**
 * Identifies data needed for an order suggestion.
 *
 * @author anshul@marketcetera.com
 * @version $Id: Suggestion.java 16063 2012-01-31 18:21:55Z colin $
 * @since 1.0.0
 */
@ClassVersion("$Id: Suggestion.java 16063 2012-01-31 18:21:55Z colin $") //$NON-NLS-1$
public interface Suggestion extends Serializable {
    /**
     * The identifier for this suggestion.
     *
     * @return the identifier for this suggestion.
     */
    public String getIdentifier();

    /**
     * Sets the identifier for this suggestion.
     *
     * @param inIdentifier the identifier value.
     */
    public void  setIdentifier(String inIdentifier);

    /**
     * The score for this suggestion. Higher the value, the stronger
     * is the recommendation for this order. 
     *
     * @return the score for this suggestion.
     */
    public BigDecimal getScore();

    /**
     * The score for this suggestion.
     *
     * @param inScore the score value.
     */
    public void setScore(BigDecimal inScore);
}
