include_class "org.marketcetera.strategy.ruby.Strategy"

class StrategyWithHelpers < Strategy
  def on_ask(ask)
        set_property("onAsk",
                     ask.toString());
  end  
  def on_bid(bid)
        set_property("onBid",
                     bid.toString());
  end  
  def on_callback(data)
        set_property("onCallback",
                     data.toString());
  end  
  def on_execution_report(execution_report)
        set_property("onExecutionReport",
                     execution_report.toString());
  end  
  def on_trade(trade)
        set_property("onTrade",
                     trade.toString());
  end  
  def on_cancel_reject(cancel)
      set_property("onCancel",
                   cancel.toString())
  end  
  def on_other(data)
        set_property("onOther",
                     data.toString());
  end  
  def on_dividend(dividend)
        set_property("onDividend",
                     dividend.toString());
  end  
end

class HelperClass
  def does_something(key,
                     value,
                     object)
        object.set_property(key,
                            value.toString());
  end
end
