import java.math.BigDecimal;

import org.marketcetera.trade.MSymbol;
import org.marketcetera.event.AskEvent;
import org.marketcetera.event.BidEvent;
import org.marketcetera.event.TradeEvent;
import org.marketcetera.strategy.java.Strategy;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;
import org.marketcetera.trade.OrderCancel;
import org.marketcetera.trade.OrderReplace;

/* $License$ */

/**
 * Sample strategy to test the ability to send orders.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public class Orders
        extends Strategy
{
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onAsk(org.marketcetera.event.AskEvent)
     */
    @Override
    public void onAsk(AskEvent inAsk)
    {
        if(getProperty("orderShouldBeNull") != null) {
            doOrderSend(false);
        } else {
            doOrderSend(true);
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onBid(org.marketcetera.event.BidEvent)
     */
    @Override
    public void onBid(BidEvent inBid)
    {
        String orderID = getProperty("orderID");
        if(orderID != null) {
            ExecutionReport[] exeReports = getExecutionReports(new OrderID(orderID));
            setProperty("executionReportCount",
                        Integer.toString(exeReports.length));
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onExecutionReport(org.marketcetera.trade.ExecutionReport)
     */
    @Override
    public void onExecutionReport(ExecutionReport inExecutionReport)
    {
        String executionReportsReceived = getProperty("executionReportsReceived");
        if(executionReportsReceived == null) {
            setProperty("executionReportsReceived",
                        "1");
        } else {
            setProperty("executionReportsReceived",
                        Integer.toString(Integer.parseInt(executionReportsReceived) + 1));
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onTrade(org.marketcetera.event.TradeEvent)
     */
    @Override
    public void onTrade(TradeEvent inTrade)
    {
        if(getProperty("cancelAll") != null) {
            int ordersCanceled = cancelAllOrders();
            setProperty("ordersCanceled",
                        Integer.toString(ordersCanceled));
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onOther(java.lang.Object)
     */
    @Override
    public void onOther(Object inEvent)
    {
        if(inEvent instanceof OrderID) {
            String shouldSkipSend = getProperty("skipSubmitOrders");
            String shouldDelaySend = getProperty("delaySubmitOrders");
            String newAccountName = getProperty("newAccountName");
            OrderCancel orderCancel = cancelOrder((OrderID)inEvent,
                                                  (shouldSkipSend == null ? true : false));
            setProperty("orderCanceled",
                        orderCancel == null ? "false" : "true");
            if(shouldDelaySend != null) {
                orderCancel.setAccount(newAccountName);
                send(orderCancel);
            }
        } else if(inEvent instanceof OrderSingle) {
            doCancelReplaceTest(inEvent);
        } else if(inEvent instanceof String){
            doCancelReplaceTest(inEvent);
        } else {
            OrderCancel orderCancel = cancelOrder(null,
                                                  true);
            setProperty("orderCanceled",
                        orderCancel == null ? "false" : "true");
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onStop()
     */
    @Override
    public void onStop()
    {
        doOrderSend(true);
        String rawOrderID = getProperty("orderID");
        if(rawOrderID != null) {
            OrderID orderID = new OrderID(rawOrderID);
            if(getProperty("shouldReplace") != null) {
                OrderSingle order = Factory.getInstance().createOrderSingle();
                OrderReplace replace = cancelReplace(orderID,
                                                     order,
                                                     false);
                if(replace != null) {
                    setProperty("orderReplaceNull",
                                "false");
                } else {
                    setProperty("orderReplaceNull",
                                "true");
                }
            } else {
                OrderCancel orderCancel = cancelOrder(orderID,
                                                      false);
                if(orderCancel != null) {
                    setProperty("orderCancelNull",
                                "false");
                } else {
                    setProperty("orderCancelNull",
                                "true");
                }
            }
        }
        setProperty("allOrdersCanceled",
                    String.valueOf(cancelAllOrders()));
    }
    /**
     * Executes the tests for <code>cancelReplace</code>.
     *
     * @param inEvent an <code>Object</code> value passed to this strategy
     */
    private void doCancelReplaceTest(Object inEvent)
    {
        String orderIDString = getProperty("orderID");
        String shouldSkipSend = getProperty("skipSubmitOrders");
        String shouldDelaySend = getProperty("delaySubmitOrders");
        OrderID orderID;
        if(orderIDString == null ||
           orderIDString.isEmpty()) {
            orderID = null;
        } else {
            orderID = new OrderID(orderIDString);
        }
        OrderReplace newOrder;
        if(inEvent instanceof OrderSingle) {
            newOrder = cancelReplace(orderID,
                                     (OrderSingle)inEvent,
                                     (shouldSkipSend == null ? true : false));
            if(shouldDelaySend != null) {
                newOrder.setQuantity(newOrder.getQuantity().add(BigDecimal.ONE));
                send(newOrder);
            }
        } else {
            newOrder = cancelReplace(orderID,
                                     null,
                                     true); 
        }
        setProperty("newOrderID",
                    (newOrder == null ? null : newOrder.getOrderID().toString())); 
    }
    /**
     * Sends an order based on test parameters.
     *
     * @param inShouldSendOrder a <code>boolean</code> value indicating whether the order should be
     *  sent or <code>null</code> should be sent instead
     */
    private void doOrderSend(boolean inShouldSendOrder)
    {
        if(inShouldSendOrder) {
            OrderSingle order = Factory.getInstance().createOrderSingle();
            order.setAccount(getProperty("account"));
            String orderType = getProperty("orderType");
            if(orderType != null) {
                order.setOrderType(OrderType.valueOf(orderType));
            }
            String price = getProperty("price");
            if(price != null) {
                order.setPrice(new BigDecimal(price));
            }
            String quantity = getProperty("quantity");
            if(quantity != null) {
                order.setQuantity(new BigDecimal(quantity));
            }
            String side = getProperty("side");
            if(side != null) {
                order.setSide(Side.valueOf(side));
            }
            String symbol = getProperty("symbol");
            if(symbol != null) {
                order.setSymbol(new MSymbol(symbol));
            }
            String timeInForce = getProperty("timeInForce");
            if(timeInForce != null) {
                order.setTimeInForce(TimeInForce.valueOf(timeInForce));
            }
            setProperty("orderID",
                         order.getOrderID().toString());
            setProperty("transactTime",
                         Long.toString(System.currentTimeMillis()));
            if(send(order)) {
                setProperty("sendResult",
                            "true");
            } else {
                setProperty("sendResult",
                            "false");
            }
        } else {
            send(null);
        }
    }
}
