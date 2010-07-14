package org.marketcetera.client.userlimit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.marketcetera.client.Client;
import org.marketcetera.client.ClientInitException;
import org.marketcetera.client.ClientManager;
import org.marketcetera.client.ConnectionException;
import org.marketcetera.core.instruments.InstrumentFromMessage;
import org.marketcetera.event.FutureEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.marketdata.AbstractMarketDataFeed;
import org.marketcetera.marketdata.AbstractMarketDataFeed.Data;
import org.marketcetera.trade.*;
import org.marketcetera.util.log.*;
import org.marketcetera.util.misc.ClassVersion;

import quickfix.FieldNotFound;
import quickfix.field.Price;
import quickfix.field.Quantity;

/* $License$ */

/**
 * Provides tools that guarantee orders are within allowable limits.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@ClassVersion("$Id$")
public enum RiskManager
{
    INSTANCE;
    /**
     * Inspects the given order according to established order limits.
     *
     * @param inOrder an <code>Order</code> value
     * @throws UserLimitViolation if a hard limit is violated
     * @throws UserLimitWarning if a soft limit is violated
     * @throws ClientInitException if the client is not ready to deliver data
     * @throws FieldNotFound if a FIX order is given and a particular field is not available
     * @throws ConnectionException if an error occured connecting to the server
     */
    public void inspect(Order inOrder)
            throws UserLimitViolation,UserLimitWarning, ClientInitException, FieldNotFound, ConnectionException
    {
        if(SLF4JLoggerProxy.isDebugEnabled("no.risk.manager")) {
            SLF4JLoggerProxy.warn(RiskManager.class,
                                  "Warning - risk manager disabled");
            return;
        }
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
        OrderType type;
        if(inOrder instanceof OrderSingle) {
            OrderSingle single = (OrderSingle)inOrder;
            price = single.getPrice();
            instrument = single.getInstrument();
            side = single.getSide();
            quantity = single.getQuantity();
            orderID = single.getOrderID();
            type = single.getOrderType();
        } else if(inOrder instanceof OrderReplace) {
            OrderReplace replace = (OrderReplace)inOrder;
            price = replace.getPrice();
            quantity = replace.getQuantity();
            instrument = replace.getInstrument();
            side = replace.getSide();
            orderID = replace.getOrderID();
            type = replace.getOrderType();
        } else if(inOrder instanceof FIXOrder) {
            FIXOrder order = (FIXOrder)inOrder;
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
            quickfix.field.OrdType typeField = new quickfix.field.OrdType();
            order.getMessage().getField(typeField);
            switch(typeField.getValue()) {
                case quickfix.field.OrdType.LIMIT:
                    type = OrderType.Limit;
                    break;
                case quickfix.field.OrdType.MARKET:
                    type = OrderType.Market;
                    break;
                default :
                    throw new UnsupportedOperationException();
            }
            price = null;
            if(type == OrderType.Limit) {
                Price priceField = new Price();
                price = order.getMessage().getField(priceField).getValue();
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
                                   "Ignoring cancel order {}", //$NON-NLS-1$
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
        SymbolDataCollection allSymbolData = new SymbolDataCollection();
        Data marketdata = null;
        SymbolData symbolData = null;
        // MD will be ENOYR-11, ECF10U, ECF-2010009
        // symbol data will be ENOYR-11 or ECF-201009
        // SYMBOL on order will be ENOYR-11, or ECF (with 201009 available as MMY)
        if(instrument instanceof Future) {
            // first try for MD is SYMBOL-EXPIRATION
            String fullSymbol = ((Future)instrument).getFullSymbol();
            marketdata = AbstractMarketDataFeed.Data.get(fullSymbol);
            symbolData = allSymbolData.getSymbolData(fullSymbol);
        }
        if(marketdata == null) {
            marketdata = AbstractMarketDataFeed.Data.get(instrument.getSymbol());
        }
        if(symbolData == null) {
            symbolData = allSymbolData.getSymbolData(instrument.getSymbol());
        }
        if(marketdata == null ||
           marketdata.getBid() == null ||
           marketdata.getAsk() == null) {
            throw new UserLimitWarning(new I18NBoundMessage1P(Messages.NO_QUOTE_DATA,
                                                              instrument.getSymbol()));
        }
        if(type == OrderType.Market) {
            if(side == Side.Buy) {
                price = marketdata.getAsk().getPrice();
            } else {
                price = marketdata.getBid().getPrice();
            }
        }
        SLF4JLoggerProxy.debug(RiskManager.class,
                               "Beginning risk manager inspection of {} with price={}, quantity={}, adjustedQuantity={}", //$NON-NLS-1$
                               inOrder,
                               price,
                               quantity,
                               adjustedQuantity);
        if(symbolData == null) {
            Messages.NO_SYMBOL_DATA.error(RiskManager.class,
                                          orderID,
                                          instrument.getSymbol());
            throw new UserLimitViolation(new I18NBoundMessage2P(Messages.NO_SYMBOL_DATA,
                                                                orderID,
                                                                instrument.getSymbol()));
        }
        SLF4JLoggerProxy.debug(RiskManager.class,
                               "Using {}", //$NON-NLS-1$
                               symbolData);
        // condition #1 - price cannot be less than 0.01 (for a limit order)
        SLF4JLoggerProxy.debug(RiskManager.class,
                               "** Test #1 - Price cannot be less than 0.01 **"); //$NON-NLS-1$
        if(type == OrderType.Limit &&
           price.compareTo(PENNY) == -1) {
            Messages.LESS_THAN_A_PENNY.error(RiskManager.class,
                                             orderID,
                                             price);
            throw new UserLimitViolation(new I18NBoundMessage2P(Messages.LESS_THAN_A_PENNY,
                                                                orderID,
                                                                price));
        }
        // condition #2 - endless loop - this is harder than it looks, skipping for now
        SLF4JLoggerProxy.debug(RiskManager.class,
                               "** Test #2 - Checking for endless loop **"); //$NON-NLS-1$
        // condition #3 - max position limit
        SLF4JLoggerProxy.debug(RiskManager.class,
                               "** Test #3 - Projected position less than limit **"); //$NON-NLS-1$
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
        SLF4JLoggerProxy.debug(RiskManager.class,
                               "Starting position of {} is {}", //$NON-NLS-1$
                               instrument,
                               position);
        BigDecimal projectedAbsolutePosition = position.add(adjustedQuantity).abs();
        SLF4JLoggerProxy.debug(RiskManager.class,
                               "Projected absolute position is {} max is {}", //$NON-NLS-1$
                               projectedAbsolutePosition,
                               symbolData.getMaximumPosition());
        if(projectedAbsolutePosition.compareTo(symbolData.getMaximumPosition().abs()) == 1) {
            Messages.POSITION_LIMIT_EXCEEDED.error(RiskManager.class,
                                                   orderID,
                                                   projectedAbsolutePosition,
                                                   symbolData.getMaximumPosition());
            throw new UserLimitViolation(new I18NBoundMessage3P(Messages.POSITION_LIMIT_EXCEEDED,
                                                                orderID,
                                                                projectedAbsolutePosition,
                                                                symbolData.getMaximumPosition()));
        }
        SLF4JLoggerProxy.debug(RiskManager.class,
                               "** Test #4 - Total trade value less than maximum **"); //$NON-NLS-1$
        BigDecimal value = quantity.multiply(price);
        if(marketdata.getTrade() instanceof FutureEvent) {
            FutureEvent futureEvent = (FutureEvent)marketdata.getTrade();
            value = value.multiply(new BigDecimal(futureEvent.getContractSize()));
        }
        SLF4JLoggerProxy.debug(RiskManager.class,
                               "Total computed value is {} max is {}", //$NON-NLS-1$
                               value,
                               symbolData.getMaximumTradeValue());
        if(value.compareTo(symbolData.getMaximumTradeValue()) == 1) {
            Messages.VALUE_LIMIT_EXCEEDED.error(RiskManager.class,
                                                orderID,
                                                value,
                                                symbolData.getMaximumTradeValue());
            throw new UserLimitViolation(new I18NBoundMessage3P(Messages.VALUE_LIMIT_EXCEEDED,
                                                                orderID,
                                                                value,
                                                                symbolData.getMaximumTradeValue()));
        }
        TradeEvent lastTrade = marketdata.getTrade();
        SLF4JLoggerProxy.debug(RiskManager.class,
                               "** Test #5 - Maximum deviation from last traded price **"); //$NON-NLS-1$
        BigDecimal absoluteDeviationFromLastTrade = (lastTrade.getPrice().subtract(price).abs()).divide(lastTrade.getPrice(),
                                                                                                        4,
                                                                                                        RoundingMode.HALF_UP);
        SLF4JLoggerProxy.debug(RiskManager.class,
                               "LastPrice: {} CurrentPrice: {} AbsoluteDeviationFromLast: {} Maximum: {}", //$NON-NLS-1$
                               lastTrade.getPrice(),
                               price,
                               absoluteDeviationFromLastTrade,
                               symbolData.getMaximumDeviationFromLast());
        if(absoluteDeviationFromLastTrade.compareTo(symbolData.getMaximumDeviationFromLast()) == 1) {
            Messages.MAX_DEVIATION_FROM_LAST_EXCEEDED.warn(RiskManager.class,
                                                           orderID,
                                                           price,
                                                           lastTrade.getPrice(),
                                                           absoluteDeviationFromLastTrade,
                                                           symbolData.getMaximumDeviationFromLast());
            throw new UserLimitWarning(new I18NBoundMessage5P(Messages.MAX_DEVIATION_FROM_LAST_EXCEEDED,
                                                              orderID,
                                                              price,
                                                              lastTrade.getPrice(),
                                                              absoluteDeviationFromLastTrade,
                                                              symbolData.getMaximumDeviationFromLast()));
        }
        SLF4JLoggerProxy.debug(RiskManager.class,
                               "** Test #6 - Maximum deviation from last mid quote **"); //$NON-NLS-1$
        BigDecimal lastBid = marketdata.getBid().getPrice();
        BigDecimal lastAsk = marketdata.getAsk().getPrice();
        BigDecimal mid = lastAsk.add(lastBid).divide(new BigDecimal(2),
                                                     RoundingMode.HALF_UP);
        BigDecimal absoluteDeviationFromLastMid = (mid.subtract(price).abs()).divide(mid,
                                                                                     4,
                                                                                     RoundingMode.HALF_UP);
        SLF4JLoggerProxy.debug(RiskManager.class,
                               "Last bid is {} Last ask is {} Mid is {} Price is {} AbsoluteDeviation is {} Max is {}", //$NON-NLS-1$
                               lastBid,
                               lastAsk,
                               mid,
                               price,
                               absoluteDeviationFromLastMid,
                               symbolData.getMaximumDeviationFromMid());
        if(absoluteDeviationFromLastMid.compareTo(symbolData.getMaximumDeviationFromMid()) == 1) {
            Messages.MAX_DEVIATION_FROM_MID_EXCEEDED.warn(RiskManager.class,
                                                          orderID,
                                                          price,
                                                          mid,
                                                          absoluteDeviationFromLastMid,
                                                          symbolData.getMaximumDeviationFromMid());
            throw new UserLimitWarning(new I18NBoundMessage5P(Messages.MAX_DEVIATION_FROM_MID_EXCEEDED,
                                                              orderID,
                                                              price,
                                                              mid,
                                                              absoluteDeviationFromLastMid,
                                                              symbolData.getMaximumDeviationFromMid()));
        }
    }
    /**
     * one penny
     */
    private static final BigDecimal PENNY = new BigDecimal("0.01"); //$NON-NLS-1$
    /**
     * client used to retrieve data
     */
    Client client;
}