require 'java'
include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "org.marketcetera.core.MSymbol"
include_class "java.lang.Long"
include_class "java.lang.System"
include_class "java.util.Arrays"

class RubyStrategy < Strategy
  def on_ask(ask)
        set_common_property("onAsk",
                            Long.toString(System.nanoTime()));
  end  
  def on_bid(bid)
        set_common_property("onBid",
                            Long.toString(System.nanoTime()));
  end  
  def on_callback(data)
        set_common_property("onCallback",
                            Long.toString(System.nanoTime()));
        # execute all services
        set_common_property("getCurrentTime",
                            get_current_time().toString());
        set_common_property("getExecutionReport",
                            Arrays.toString(get_execution_report(nil).toArray()));
        set_common_property("getCurrentPositionAtOpen",
                            get_current_position_at_open(MSymbol.new("symbol")).toString());
        set_common_property("getCurrentPosition",
                            get_current_position(MSymbol.new("symbol")).toString());
        set_common_property("getGoal",
                            get_goal(MSymbol.new("symbol")).toString());
  end  
  def on_cancel(cancel)
        set_common_property("onCancel",
                            Long.toString(System.nanoTime()));
  end  
  def on_execution_report(execution_report)
        set_common_property("onExecutionReport",
                            Long.toString(System.nanoTime()));
  end  
  def on_news(symbol,
              news)
        set_common_property("onNews",
                            Long.toString(System.nanoTime()));
  end  
  def on_trade(trade)
        set_common_property("onTrade",
                            Long.toString(System.nanoTime()));
  end  
end
