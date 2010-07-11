package org.marketcetera.client.userlimit;

import java.math.BigDecimal;
import java.util.Date;

import org.marketcetera.client.Client;
import org.marketcetera.client.ClientManager;
import org.marketcetera.core.instruments.InstrumentFromMessage;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.AbstractMarketDataFeed;
import org.marketcetera.marketdata.AbstractMarketDataFeed.Data;
import org.marketcetera.trade.*;
import org.marketcetera.util.except.I18NException;
import org.marketcetera.util.log.I18NBoundMessage2P;
import org.marketcetera.util.log.I18NBoundMessage3P;
import org.marketcetera.util.log.SLF4JLoggerProxy;

import quickfix.FieldNotFound;
import quickfix.field.Price;
import quickfix.field.Quantity;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum RiskManager
{
    INSTANCE;
    /**
     * 
     *
     *
     * @param inOrder
     * @throws I18NException
     * @throws FieldNotFound 
     */
    public void inspect(Order inOrder)
            throws I18NException, FieldNotFound
    {
        if(inOrder == null) {
            throw new NullPointerException();
        }
        synchronized(this) {
            if(client == null) {
                client = ClientManager.getInstance();
            }
        }
        // order price less than .01 not allowed
        BigDecimal price;
        BigDecimal quantity;
        BigDecimal adjustedQuantity;
        Instrument instrument;
        Side side;
        OrderID orderID;
        if(inOrder instanceof OrderSingle) {
            OrderSingle single = (OrderSingle)inOrder;
            price = single.getPrice();
            instrument = single.getInstrument();
            side = single.getSide();
            quantity = single.getQuantity();
            orderID = single.getOrderID();
        } else if(inOrder instanceof OrderReplace) {
            OrderReplace replace = (OrderReplace)inOrder;
            price = replace.getPrice();
            quantity = replace.getQuantity();
            instrument = replace.getInstrument();
            side = replace.getSide();
            orderID = replace.getOrderID();
        } else if(inOrder instanceof FIXOrder) {
            FIXOrder order = (FIXOrder)inOrder;
            Price priceField = new Price();
            price = order.getMessage().getField(priceField).getValue();
            quickfix.field.Side sideField = new quickfix.field.Side();
            order.getMessage().getField(sideField);
            if(sideField.valueEquals(quickfix.field.Side.BUY)) {
                side = Side.Buy;
            } else if(sideField.valueEquals(quickfix.field.Side.SELL)) {
                side = Side.Sell;
            } else if(sideField.valueEquals(quickfix.field.Side.SELL_SHORT)) {
                side = Side.SellShort;
            } else {
                throw new UnsupportedOperationException();
            }
            instrument = InstrumentFromMessage.SELECTOR.forValue(order.getMessage()).extract(order.getMessage());
            Quantity quantityField = new Quantity();
            quantity = order.getMessage().getField(quantityField).getValue();
            quickfix.field.OrderID orderIDField = new quickfix.field.OrderID();
            order.getMessage().getField(orderIDField);
            orderID = new OrderID(orderIDField.getValue());
        } else if(inOrder instanceof OrderCancel) {
            // limits do not apply
            SLF4JLoggerProxy.debug(RiskManager.class,
                                   "Ignoring cancel order {}",
                                   inOrder);
            return;
        } else {
            throw new UnsupportedOperationException();
        }
        if(side == Side.Sell ||
           side == Side.SellShort) {
            adjustedQuantity = BigDecimal.ZERO.subtract(quantity);
        } else {
            adjustedQuantity = quantity;
        }
        SLF4JLoggerProxy.debug(RiskManager.class,
                               "Beginning risk manager inspection of {} with price={}, quantity={}, adjustedQuantity={}",
                               inOrder,
                               price,
                               quantity,
                               adjustedQuantity);
        // condition #1 - price cannot be less than 0.01
        if(price.compareTo(PENNY) == -1) {
            throw new UserLimitViolation(new I18NBoundMessage2P(Messages.LESS_THAN_A_PENNY,
                                                                orderID,
                                                                price));
        }
        // condition #2 - eternal loop - this is harder than it looks, skipping for now
        // further set of conditions require user limits to continue
        SymbolDataCollection allSymbolData = new SymbolDataCollection();
        // condition #3 - max position limit
        SymbolData symbolData = allSymbolData.getSymbolData(instrument.getSymbol());
        if(symbolData == null) {
            throw new UserLimitViolation(new I18NBoundMessage2P(Messages.NO_SYMBOL_DATA,
                                                                orderID,
                                                                instrument.getSymbol()));
        }
        BigDecimal position;
        if(instrument instanceof Equity) {
            position = client.getEquityPositionAsOf(new Date(),
                                                    (Equity)instrument);
        } else if(instrument instanceof Option)  {
            position = client.getOptionPositionAsOf(new Date(),
                                                    (Option)instrument);
        } else if(instrument instanceof Future) {
            position = client.getFuturePositionAsOf(new Date(),
                                                    (Future)instrument);
        } else {
            throw new UnsupportedOperationException();
        }
        if(position == null) {
            position = BigDecimal.ZERO;
        }
        BigDecimal projectedAbsolutePosition = position.add(adjustedQuantity).abs();
        if(projectedAbsolutePosition.compareTo(symbolData.getMaximumPosition().abs()) == 1) {
            throw new UserLimitViolation(new I18NBoundMessage3P(Messages.POSITION_LIMIT_EXCEEDED,
                                                                orderID,
                                                                projectedAbsolutePosition,
                                                                symbolData.getMaximumPosition()));
        }
        // condition #4 maximum value of trade
        BigDecimal value = quantity.multiply(price); 
        if(value.compareTo(symbolData.getMaximumTradeValue()) == 1) {
            throw new UserLimitViolation(new I18NBoundMessage3P(Messages.VALUE_LIMIT_EXCEEDED,
                                                                orderID,
                                                                value,
                                                                symbolData.getMaximumTradeValue()));
        }
        // condition #5 maximum deviation from last traded price
        Data marketdata = provider.getData(instrument.getSymbol());
        if(marketdata == null ||
           marketdata.getTrade() == null) {
            throw new UserLimitViolation(new I18NBoundMessage2P(Messages.NO_TRADE_DATA,
                                                                orderID,
                                                                instrument.getSymbol()));
        }
        TradeEvent lastTrade = marketdata.getTrade();
        BigDecimal absoluteDeviationFromLastTrade = (lastTrade.getPrice().subtract(price).abs()).divide(lastTrade.getPrice());
        if(absoluteDeviationFromLastTrade.compareTo(symbolData.getMaximumDeviationFromLast()) == 1) {
            throw new UserLimitWarning(new I18NBoundMessage2P(Messages.MAX_DEVIATION_FROM_LAST_EXCEEDED,
                                                              absoluteDeviationFromLastTrade,
                                                              symbolData.getMaximumDeviationFromLast()));
        }
        if(marketdata.getBid() == null ||
           marketdata.getAsk() == null) {
            throw new UserLimitWarning(Messages.NO_QUOTE_DATA);
        }
        BigDecimal lastBid = marketdata.getBid().getPrice();
        BigDecimal lastAsk = marketdata.getAsk().getPrice();
        BigDecimal mid = lastAsk.subtract(lastBid).abs().divide(new BigDecimal(2));
        BigDecimal absoluteDeviationFromLastMid = (mid.subtract(price).abs()).divide(mid);
        if(absoluteDeviationFromLastMid.compareTo(symbolData.getMaximumDeviationFromMid()) == 1) {
            throw new UserLimitWarning(new I18NBoundMessage2P(Messages.MAX_DEVIATION_FROM_MID_EXCEEDED,
                                                              absoluteDeviationFromLastMid,
                                                              symbolData.getMaximumDeviationFromMid()));
        }
    }
    /**
     * one penny
     */
    private static final BigDecimal PENNY = new BigDecimal("0.01");
    /**
     * 
     */
    Client client;
    MarketDataProvider provider = new MarketDataProvider() {
        @Override
        public Data getData(String inSymbol)
        {
            return AbstractMarketDataFeed.Data.get(inSymbol);
        }
    };
    /**
     *
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    interface MarketDataProvider
    {
        Data getData(String inSymbol); 
    }
}