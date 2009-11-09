import java.math.BigDecimal;

import org.marketcetera.strategy.java.Strategy;
import org.marketcetera.trade.ExecutionReport;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.Equity;
import org.marketcetera.trade.OrderID;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;

/* $License$ */

/**
 * Tests the Strategy API open order tracker.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class OrderRetention
        extends Strategy
{
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onStart()
     */
    @Override
    public void onStart()
    {
        setProperty("executionReportCounter",
                    String.valueOf(0));
        setProperty("orderIDs",
                    "");
        int ordersToSubmit = Integer.parseInt(getParameter("ordersToSubmit"));
        for(int i=0;i<ordersToSubmit;i++) {
            generateAndSubmitOrder();
        }
    }
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onExecutionReport(org.marketcetera.trade.ExecutionReport)
     */
    @Override
    public void onExecutionReport(ExecutionReport inExecutionReport)
    {
        // register this execution report along with its value
        setProperty(inExecutionReport.getOrderID().getValue(),
                    inExecutionReport.toString());
        // increment the execution report counter
        setProperty("executionReportCounter",
                    String.valueOf(++executionReportCounter));
    }
    /**
     * Generates and submits an <code>OrderSingle</code>.
     */
    private void generateAndSubmitOrder()
    {
        OrderSingle order = Factory.getInstance().createOrderSingle();
        order.setOrderType(OrderType.Limit);
        order.setPrice(BigDecimal.ONE);
        order.setQuantity(BigDecimal.TEN);
        order.setSide(Side.Buy);
        order.setInstrument(new Equity("METC"));
        order.setTimeInForce(TimeInForce.GoodTillCancel);
        if(send(order)) {
            recordOrderID(order.getOrderID());
        }
    }
    /**
     * Records the given <code>OrderID</code> in a manner
     * expected by the unit tests. 
     *
     * @param inOrderID an <code>OrderID</code> value
     */
    private void recordOrderID(OrderID inOrderID)
    {
        String currentOrderIDList = getProperty("orderIDs");
        currentOrderIDList += "," + inOrderID.getValue();
        setProperty("orderIDs",
                    currentOrderIDList);
    }
    /**
     * records the total number of execution reports received
     */
    private int executionReportCounter = 0;
}
