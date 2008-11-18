include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "java.math.BigDecimal"
include_class "org.marketcetera.trade.Factory"
include_class "org.marketcetera.trade.OrderType"
include_class "org.marketcetera.trade.Side"
include_class "org.marketcetera.trade.TimeInForce"
include_class "org.marketcetera.core.MSymbol"

class SuggestTrades < Strategy
    def on_start
        orderShouldBeNull = get_parameter("orderShouldBeNull")
        if(orderShouldBeNull != nil)
            suggest_trade(nil, BigDecimal.new("100.00"), "suggestion")
        else
            suggestion = Factory.getInstance().createOrderSingleSuggestion()
            suggestion.setAccount(get_parameter("account"))
            orderType = get_parameter("orderType")
            if(orderType != nil)
                suggestion.setOrderType(OrderType.valueOf(orderType))
            end
            price = get_parameter("price")
            if(price != nil)
                suggestion.setPrice(BigDecimal.new(price))
            end
            quantity = get_parameter("quantity")
            if(quantity != nil)
                suggestion.setQuantity(BigDecimal.new(quantity))
            end
            side = get_parameter("side")
            if(side != nil)
                suggestion.setSide(Side.valueOf(side))
            end
            symbol = get_parameter("symbol")
            if(symbol != nil)
                suggestion.setSymbol(MSymbol.new(symbol))
            end
            timeInForce = get_parameter("timeInForce")
            if(timeInForce != nil)
                suggestion.setTimeInForce(TimeInForce.valueOf(timeInForce))
            end
            scoreString = get_parameter("score")
            if(scoreString != nil)
                score = BigDecimal.new(scoreString)
            else
                score = nil
            end
            suggest_trade(suggestion, score, get_parameter("identifier"))
        end
    end
end