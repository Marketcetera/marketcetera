package org.marketcetera.trade;

import java.io.Serializable;
import java.math.BigDecimal;

import org.marketcetera.util.misc.ClassVersion;

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
        extends Serializable,HasSuggestionIdentifier
{
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
