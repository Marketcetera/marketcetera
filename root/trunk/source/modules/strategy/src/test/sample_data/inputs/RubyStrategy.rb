include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "java.lang.System"
include_class "java.lang.Long"

class RubyStrategy < Strategy
  def on_start
      shouldFail = get_parameter("shouldFailOnStart");
      if(shouldFail != nil) 
          10 / 0;
      end
      shouldLoop = get_parameter("shouldLoopOnStart");
      if(shouldLoop != nil)
          while(true)
              shouldStopLoop = get_parameter("shouldStopLoop");
              if(shouldStopLoop != nil)
                break
              end
              puts "sleeping..."
              sleep 0.1
          end
      end
      set_property("onStart",
                   Long.toString(System.currentTimeMillis()));
  end
  def on_stop
      shouldFail = get_parameter("shouldFailOnStop");
      if(shouldFail != nil) 
          10 / 0;
      end
      set_property("onStop",
                   Long.toString(System.currentTimeMillis()));
  end
  def on_ask(ask)
      shouldFail = get_parameter("shouldFailOnAsk");
      if(shouldFail != nil) 
          10 / 0;
      end
      set_property("onAsk",
                   ask.toString());
  end  
  def on_bid(bid)
      shouldFail = get_parameter("shouldFailOnBid");
      if(shouldFail != nil) 
          10 / 0;
      end
      set_property("onBid",
                   bid.toString());
  end  
  def on_callback(data)
      shouldFail = get_parameter("shouldFailOnCallback");
      if(shouldFail != nil) 
          10 / 0;
      end
      set_property("onCallback",
                   data.toString());
  end  
  def on_execution_report(execution_report)
      shouldFail = get_parameter("shouldFailOnExecutionReport");
      if(shouldFail != nil) 
          10 / 0;
      end
      set_property("onExecutionReport",
                   execution_report.toString());
  end  
  def on_trade(trade)
      shouldFail = get_parameter("shouldFailOnTrade");
      if(shouldFail != nil) 
          10 / 0;
      end
      set_property("onTrade",
                   trade.toString());
  end  
  def on_other(data)
      shouldFail = get_parameter("shouldFailOnOther");
      if(shouldFail != nil) 
          10 / 0;
      end
      set_property("onOther",
                   data.toString());
  end  
end
