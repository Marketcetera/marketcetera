package org.marketcetera.trade;

import java.io.Serializable;
import java.math.BigDecimal;

import org.marketcetera.admin.HasUser;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */
/**
 * Identifies data needed for an order suggestion.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since 1.0.0
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public interface Suggestion
        extends Serializable,HasUser
{
    /**
     * The unique identifier for this suggestion.
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
     * The score for this suggestion.
     * 
     * <p>Higher the value, the stronger
     * is the recommendation for this order.</p> 
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
