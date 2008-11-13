package org.marketcetera.trade;

import org.marketcetera.util.misc.ClassVersion;

import java.math.BigDecimal;

/* $License$ */
/**
 * Implementation for Single Order Suggestions.
 *
 * @author anshul@marketcetera.com
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
class OrderSingleSuggestionImpl extends OrderSingleImpl
        implements OrderSingleSuggestion {
    @Override
    public String getIdentifier() {
        return mIdentifier;
    }

    @Override
    public void setIdentifier(String inIdentifier) {
        mIdentifier = inIdentifier;
    }

    @Override
    public BigDecimal getScore() {
        return mScore;
    }

    @Override
    public void setScore(BigDecimal inScore) {
        mScore = inScore;
    }

    private String mIdentifier;
    private BigDecimal mScore;
    private static final long serialVersionUID = 1L;
}
