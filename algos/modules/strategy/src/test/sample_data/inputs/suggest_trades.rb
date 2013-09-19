require 'java'
java_import org.marketcetera.strategy.ruby.Strategy
java_import java.math.BigDecimal
java_import org.marketcetera.trade.Factory
java_import org.marketcetera.trade.OrderType
java_import org.marketcetera.trade.Side
java_import org.marketcetera.trade.TimeInForce
java_import org.marketcetera.trade.Equity

class SuggestTrades < Strategy
    def on_start
        do_suggest
    end
    def on_stop
        do_suggest
    end
    def do_suggest
        orderShouldBeNull = get_parameter("orderShouldBeNull")
        if(orderShouldBeNull != nil)
            suggest_trade(nil, BigDecimal.new("100.00"), "suggestion")
        else
            suggestedOrder = Factory.getInstance().createOrderSingle()
            suggestedOrder.setAccount(get_parameter("account"))
            orderType = get_parameter("orderType")
            if(orderType != nil)
                suggestedOrder.setOrderType(OrderType.valueOf(orderType))
            end
            price = get_parameter("price")
            if(price != nil)
                suggestedOrder.setPrice(BigDecimal.new(price))
            end
            quantity = get_parameter("quantity")
            if(quantity != nil)
                suggestedOrder.setQuantity(BigDecimal.new(quantity))
            end
            side = get_parameter("side")
            if(side != nil)
                suggestedOrder.setSide(Side.valueOf(side))
            end
            symbol = get_parameter("symbol")
            if(symbol != nil)
                suggestedOrder.setInstrument(Equity.new(symbol))
            end
            timeInForce = get_parameter("timeInForce")
            if(timeInForce != nil)
                suggestedOrder.setTimeInForce(TimeInForce.valueOf(timeInForce))
            end
            scoreString = get_parameter("score")
            if(scoreString != nil)
                score = BigDecimal.new(scoreString)
            else
                score = nil
            end
            suggest_trade(suggestedOrder, score, get_parameter("identifier"))
        end
    end
end
