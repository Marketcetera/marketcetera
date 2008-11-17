include_class "org.marketcetera.strategy.ruby.Strategy"

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
  end  
  def on_other(data)
        otherParameter = get_parameter("onOther");
        set_property("onOther",
                     otherParameter);
  end  
end
