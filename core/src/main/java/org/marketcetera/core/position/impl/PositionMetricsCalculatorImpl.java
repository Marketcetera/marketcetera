package org.marketcetera.core.position.impl;

import java.math.BigDecimal;
import java.util.LinkedList;

import org.apache.commons.lang.Validate;
import org.marketcetera.core.BigDecimalUtil;
import org.marketcetera.core.position.PositionMetrics;
import org.marketcetera.core.position.Trade;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * An implementation of {@link PositionMetricsCalculator}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.5.0
 */
@ClassVersion("$Id$")
public final class PositionMetricsCalculatorImpl
        implements PositionMetricsCalculator
{

    private final BigDecimal mIncomingPosition;
    private final CostElement mPositionCost = new CostElement();
    private final CostElement tradingCost = new CostElement();
    private final CostElement mUnrealizedCost = new CostElement();
    private final LinkedList<PositionElement> mPositionElements = new LinkedList<PositionElement>();
    private final boolean mClosingPriceAvailable;
    private BigDecimal mLastTradePrice;
    private BigDecimal mPosition;
    private BigDecimal mRealizedPL = BigDecimal.ZERO;
    private BigDecimal lastBidPrice;
    private BigDecimal lastAskPrice;
    private boolean quoteChange;

    /**
     * Constructor.
     * 
     * @param incomingPosition
     *            the incoming position that will be used to calculate position PL
     * @param closingPrice
     *            the closing price that will be used to calculate position PL
     * @throws IllegalArgumentException if incomingPosition is null
     */
    public PositionMetricsCalculatorImpl(final BigDecimal incomingPosition,
                                         BigDecimal closingPrice)
    {
        Validate.notNull(incomingPosition);
        mIncomingPosition = incomingPosition;
        mPosition = incomingPosition;
        closingPrice = closingPrice == null ? BigDecimal.ZERO : closingPrice;
        mClosingPriceAvailable = closingPrice != null;
        if(mClosingPriceAvailable) {
            mPositionCost.add(mPosition,
                              closingPrice);
            mUnrealizedCost.add(mPosition,
                                closingPrice);
            mPositionElements.add(new PositionElement(mPosition,
                                                      closingPrice));
        } else {
            System.out.println("COLIN: closing price unavailable");
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.position.impl.PositionMetricsCalculator#bid(java.math.BigDecimal)
     */
    @Override
    public PositionMetrics bid(BigDecimal inBidPrice)
    {
        lastBidPrice = inBidPrice;
        quoteChange = true;
        return createPositionMetrics();
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.position.impl.PositionMetricsCalculator#ask(java.math.BigDecimal)
     */
    @Override
    public PositionMetrics ask(BigDecimal inAskPrice)
    {
        lastAskPrice = inAskPrice;
        quoteChange = true;
        return createPositionMetrics();
    }
    @Override
    public synchronized PositionMetrics tick(final BigDecimal tradePrice) {
        mLastTradePrice = tradePrice;
        quoteChange = false;
        return createPositionMetrics();
    }

    @Override
    public synchronized PositionMetrics trade(final Trade<?> trade) {
        processTrade(trade.getQuantity(), trade.getPrice());
        return createPositionMetrics();
    }

    /**
     * Processes a trade, closing existing positions and creating new ones as necessary.
     * 
     * @param quantity
     *            the quantity of the trade, positive for a buy and negative for a sell
     * @param price
     *            the price of the trade
     */
    private void processTrade(final BigDecimal quantity, final BigDecimal price) {
        mPosition = mPosition.add(quantity);
        // only bother with PNL if the closing price is available
        if (mClosingPriceAvailable) {
            tradingCost.add(quantity, price);
            // determine the sides, +1 for long and -1 for short
            int holdingSide = mUnrealizedCost.signum();
            int tradingSide = quantity.signum();
            BigDecimal remaining = quantity;
            // if sides are different
            if (tradingSide * holdingSide == -1) {
                // close positions
                while (!mPositionElements.isEmpty()) {
                    // get the oldest open position
                    PositionElement toClose = mPositionElements.peek();
                    // add the remaining trade quantity
                    BigDecimal leftover = toClose.quantity.add(remaining);
                    int leftoverSide = leftover.signum();
                    // if there is leftover on the open position
                    if (leftoverSide == holdingSide) {
                        // the trade only partially closed this position
                        processClose(remaining, toClose.price, price);
                        toClose.quantity = leftover;
                        // trade has been completely processed
                        return;
                    } else {
                        // the trade completely closed this position
                        processClose(toClose.quantity.negate(), toClose.price, price);
                        mPositionElements.remove();
                        remaining = leftover;
                        // if leftover is zero
                        if (leftoverSide == 0) {
                            // trade has been completely processed
                            return;
                        }
                    }
                }
            }
            // if non-zero remaining quantity
            if (remaining.signum() != 0) {
                // create new position
                mPositionElements.add(new PositionElement(remaining, price));
                mUnrealizedCost.add(remaining, price);
            }
        }

    }

    /**
     * Processes a position close, updating realized P&L and the unrealized cost
     * 
     * @param quantity
     *            the quantity being closed, negative when closing a long position and positive when
     *            closing a short position
     * @param openPrice
     *            the price at which the position was opened
     * @param closePrice
     *            the price at which the position is closing
     */
    private void processClose(final BigDecimal quantity,
                              final BigDecimal openPrice,
                              final BigDecimal closePrice)
    {
        // subtract closePrice from openPrice since quantity has opposite sign
        // more readable may be:
        // quantity.negate().multiply(closePrice.subtract(openPrice))
        mRealizedPL = mRealizedPL.add(quantity.multiply(openPrice.subtract(closePrice)));
        mUnrealizedCost.add(quantity, openPrice);
    }
    /**
     * Create the position metrics based on the current state.
     *
     * @return a <code>PositionMetrics</code> value
     */
    private PositionMetrics createPositionMetrics()
    {
        BigDecimal unrealizedPL = null;
        BigDecimal realizedPL = null;
        BigDecimal tradingPL = null;
        BigDecimal positionPL = null;
        BigDecimal totalPL = null;
        BigDecimal positionPrice;
        if(quoteChange) {
            if(BigDecimalUtil.isNegative(mPosition)) {
                positionPrice = lastBidPrice;
            } else {
                positionPrice = lastAskPrice;
            }
        } else {
            positionPrice = mLastTradePrice;
        }
        if(positionPrice == null) {
            positionPrice = BigDecimal.ZERO;
        }
        if(mLastTradePrice == null) {
            mLastTradePrice = lastBidPrice == null ? lastAskPrice : lastBidPrice;
        }
        if(mClosingPriceAvailable) {
            realizedPL = mRealizedPL;
            if(mLastTradePrice != null) {
                positionPL = mPositionCost.getPL(positionPrice);
                unrealizedPL = mUnrealizedCost.getPL(positionPrice);
                tradingPL = tradingCost.getPL(positionPrice);
                totalPL = realizedPL.add(unrealizedPL);
            }
        }
        PositionMetricsImpl positionMetrics = new PositionMetricsImpl(mIncomingPosition,
                                                                      mPosition,
                                                                      positionPL,
                                                                      tradingPL,
                                                                      realizedPL,
                                                                      unrealizedPL,
                                                                      totalPL);
        return positionMetrics;
    }

    private static class CostElement {
        private BigDecimal quantity = BigDecimal.ZERO;
        private BigDecimal cost = BigDecimal.ZERO;

        public void add(BigDecimal quantity, BigDecimal price) {
            this.quantity = this.quantity.add(quantity);
            cost = cost.add(quantity.multiply(price));
        }

        public BigDecimal getPL(BigDecimal lastTradePrice) {
            return quantity.multiply(lastTradePrice).subtract(cost);
        }

        public int signum() {
            return quantity.signum();
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("CostElement [quantity=").append(quantity).append(", cost=").append(cost).append("]");
            return builder.toString();
        }
    }

    private class PositionElement {
        public BigDecimal quantity;
        public BigDecimal price;

        public PositionElement(BigDecimal quantity, BigDecimal price) {
            this.quantity = quantity;
            this.price = price;
        }
        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append("PositionElement [quantity=").append(quantity).append(", price=").append(price).append("]");
            return builder.toString();
        }
    }

}