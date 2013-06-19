package org.marketcetera.core.trade;

import java.math.BigDecimal;

/* $License$ */
/**
 * Implementation for Single Order Suggestions.
 *
 * @version $Id$
 * @since 1.0.0
 */
class OrderSingleSuggestionImpl
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

    @Override
    public OrderSingle getOrder() {
        return mOrder == null
                ? null
                : mOrder.clone();
    }

    @Override
    public void setOrder(OrderSingle inOrder) {
        mOrder = inOrder == null
                ? null
                : inOrder.clone();
    }

    @Override
    public String toString() {
        return Messages.ORDER_SINGLE_SUGGESTION_TO_STRING.getText(
                String.valueOf(getIdentifier()),
                String.valueOf(getScore()),
                String.valueOf(getOrder())
        );
    }

    private String mIdentifier;
    private BigDecimal mScore;
    private OrderSingle mOrder;
    private static final long serialVersionUID = 1L;
}
