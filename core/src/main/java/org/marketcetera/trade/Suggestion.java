package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

import java.math.BigDecimal;
import java.io.Serializable;

/* $License$ */
/**
 * Identifies data needed for an order suggestion.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$")
public interface Suggestion
        extends Serializable
{
    /**
     * The identifier for this suggestion.
     *
     * @return the identifier for this suggestion.
     */
    String getIdentifier();
    /**
     * Sets the identifier for this suggestion.
     *
     * @param inIdentifier the identifier value.
     */
    void setIdentifier(String inIdentifier);
    /**
     * The score for this suggestion. Higher the value, the stronger
     * is the recommendation for this order. 
     *
     * @return the score for this suggestion.
     */
    BigDecimal getScore();
    /**
     * The score for this suggestion.
     *
     * @param inScore the score value.
     */
    void setScore(BigDecimal inScore);
}
