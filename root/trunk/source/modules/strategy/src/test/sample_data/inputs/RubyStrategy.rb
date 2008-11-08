require 'java'
include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "org.marketcetera.core.MSymbol"
include_class "java.lang.Long"
include_class "java.lang.System"
include_class "java.util.Arrays"

class RubyStrategy < Strategy
  def on_ask(ask)
        set_property("onAsk",
                     Long.toString(System.nanoTime()));
  end  
  def on_bid(bid)
        set_property("onBid",
                     Long.toString(System.nanoTime()));
  end  
  def on_callback(data)
        set_property("onCallback",
                     Long.toString(System.nanoTime()));
        # execute all services
        set_property("getCurrentTime",
                     get_current_time().toString());
        set_property("getExecutionReport",
                     Arrays.toString(get_execution_report(nil).toArray()));
        set_property("getCurrentPositionAtOpen",
                     get_current_position_at_open(MSymbol.new("symbol")).toString());
        set_property("getCurrentPosition",
                     get_current_position(MSymbol.new("symbol")).toString());
  end  
  def on_cancel(cancel)
        set_property("onCancel",
                     Long.toString(System.nanoTime()));
  end  
  def on_execution_report(execution_report)
        set_property("onExecutionReport",
                     Long.toString(System.nanoTime()));
  end  
  def on_news(symbol,
              news)
        set_property("onNews",
                     Long.toString(System.nanoTime()));
  end  
  def on_trade(trade)
        set_property("onTrade",
                     Long.toString(System.nanoTime()));
  end  
end
