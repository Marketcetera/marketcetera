import java.math.BigDecimal;

import org.marketcetera.trade.MSymbol;
import org.marketcetera.strategy.java.Strategy;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.trade.OrderType;
import org.marketcetera.trade.Side;
import org.marketcetera.trade.TimeInForce;

/**
 * Test strategy that suggests trades.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 1.0.0
 */
public class SuggestTrades
    extends Strategy
{
    /* (non-Javadoc)
     * @see org.marketcetera.strategy.java.Strategy#onStart()
     */
    @Override
    public void onStart()
    {
        String orderShouldBeNull = getParameter("orderShouldBeNull");
        if(orderShouldBeNull != null) {
            suggestTrade(null,
                         new BigDecimal("100.00"),
                         "suggestion");
        } else {
            OrderSingle suggestedOrder = Factory.getInstance().createOrderSingle();
            suggestedOrder.setAccount(getParameter("account"));
            String orderType = getParameter("orderType");
            if(orderType != null) {
                suggestedOrder.setOrderType(OrderType.valueOf(orderType));
            }
            String price = getParameter("price");
            if(price != null) {
                suggestedOrder.setPrice(new BigDecimal(price));
            }
            String quantity = getParameter("quantity");
            if(quantity != null) {
                suggestedOrder.setQuantity(new BigDecimal(quantity));
            }
            String side = getParameter("side");
            if(side != null) {
                suggestedOrder.setSide(Side.valueOf(side));
            }
            String symbol = getParameter("symbol");
            if(symbol != null) {
                suggestedOrder.setSymbol(new MSymbol(symbol));
            }
            String timeInForce = getParameter("timeInForce");
            if(timeInForce != null) {
                suggestedOrder.setTimeInForce(TimeInForce.valueOf(timeInForce));
            }
            String scoreString = getParameter("score");
            BigDecimal score;
            if(scoreString != null) {
                score = new BigDecimal(scoreString);
            } else {
                score = null;
            }
            if(getParameter("doOrder") != null) {
                if(getParameter("sendOrderShouldBeNull") != null) {
                    sendOrder(null);
                } else {
                    sendOrder(suggestedOrder);
                }
            } else {
                suggestTrade(suggestedOrder,
                             score,
                             getParameter("identifier"));
            }
        }
    }
}