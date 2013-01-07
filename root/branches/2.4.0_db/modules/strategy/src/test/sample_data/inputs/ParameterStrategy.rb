include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "org.marketcetera.trade.Factory"
include_class "java.math.BigDecimal"
include_class "org.marketcetera.quickfix.FIXVersion"
include_class "org.marketcetera.trade.BrokerID"

class ParameterStrategy < Strategy
  def on_ask(ask)
        askParameter = get_parameter("onAsk");
        set_property("onAsk",
                     askParameter);
  end  
  def on_bid(bid)
        bidParameter = get_parameter("onBid");
        set_property("onBid",
                     bidParameter);
        emitSuggestion = get_parameter("emitSuggestion")
        if(emitSuggestion != nil)
            suggestedOrder = Factory.getInstance().createOrderSingle()
            suggest_trade(suggestedOrder, BigDecimal.new("1.1"), "identifier")
        end
  end  
  def on_callback(data)
        callbackParameter = get_parameter("onCallback");
        set_property("onCallback",
                     callbackParameter);
  end  
  def on_execution_report(execution_report)
        executionReportParameter = get_parameter("onExecutionReport");
        set_property("onExecutionReport",
                     executionReportParameter);
  end  
  def on_trade(trade)
      tradeParameter = get_parameter("onTrade");
      set_property("onTrade",
                   tradeParameter);
      emitMessage = get_parameter("emitMessage")
      if(emitMessage != nil)
          message = FIXVersion.getFIXVersion("FIX.0.0").getMessageFactory().newBasicOrder()
          send_message(message, BrokerID.new("some-broker"))
      end
  end  
  def on_other(data)
        otherParameter = get_parameter("onOther");
        set_property("onOther",
                     otherParameter);
  end  
end
