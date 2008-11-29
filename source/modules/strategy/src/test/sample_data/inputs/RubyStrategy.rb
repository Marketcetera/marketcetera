include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "org.marketcetera.marketdata.DataRequest"
include_class "java.lang.System"
include_class "java.lang.Long"
include_class "java.util.Date"

class RubyStrategy < Strategy
  def on_start
      @callbackCounter = 0
      shouldFail = get_parameter("shouldFailOnStart")
      if(shouldFail != nil) 
          10 / 0
      end
      shouldLoop = get_parameter("shouldLoopOnStart")
      if(shouldLoop != nil)
          while(true)
              shouldStopLoop = get_parameter("shouldStopLoop")
              if(shouldStopLoop != nil)
                break
              end
              puts "sleeping..."
              sleep 0.1
          end
      end
      marketDataSource = get_parameter("shouldRequestData")
      if(marketDataSource != nil)
          symbols = get_parameter("symbols")
          set_property("requestID",
                       Long.toString(request_market_data(symbols,
                                                         marketDataSource)))
      end
      do_request_parameter_callbacks
      do_request_properties_callbacks
      if(get_parameter("shouldNotify") != nil) 
          notify_low("low subject", Long.toString(System.nanoTime()))
          notify_medium("medium subject", Long.toString(System.nanoTime()))
          notify_high("high subject", Long.toString(System.nanoTime()))
     end
     if(get_property("askForPosition") != nil)
         symbol = get_property "symbol"
         dateString = get_property "date"
         if(dateString == nil)
             date = nil
         else
             date = Date.new Long.parseLong dateString
         end
         result = get_position_as_of date, symbol
         if(result == nil)
            resultString = nil
         else
            resultString = result.to_s
         end
         set_property "position", resultString
     end
     if(get_property("askForDestinations") != nil)
        destinations = get_destinations
        iterator = destinations.iterator
        counter = 0
        while iterator.hasNext
            destination = iterator.next
            set_property counter.to_s, destination.toString
            counter += 1
        end
     end
     set_property("onStart",
                   Long.toString(System.currentTimeMillis()))
  end
  def on_stop
      shouldFail = get_parameter("shouldFailOnStop")
      if(shouldFail != nil) 
          10 / 0
      end
      set_property("onStop",
                   Long.toString(System.currentTimeMillis()))
  end
  def on_ask(ask)
      shouldFail = get_parameter("shouldFailOnAsk")
      if(shouldFail != nil) 
          10 / 0
      end
      set_property("onAsk",
                   ask.toString())
  end  
  def on_bid(bid)
      shouldFail = get_parameter("shouldFailOnBid")
      if(shouldFail != nil) 
          10 / 0
      end
      set_property("onBid",
                   bid.toString())
  end  
  def on_callback(data)
    @callbackCounter += 1
    shouldCancel = get_property("shouldCancel")
    if(shouldCancel != nil)
        requestToCancel = get_property("requestID")
        cancel_market_data_request(Long.parseLong(requestToCancel))
    end
      shouldFail = get_parameter("shouldFailOnCallback")
      if(shouldFail != nil) 
          10 / 0
      end
      set_property("onCallback",
                   @callbackCounter.to_s)
  end  
  def on_execution_report(execution_report)
      shouldFail = get_parameter("shouldFailOnExecutionReport")
      if(shouldFail != nil) 
          10 / 0
      end
      set_property("onExecutionReport",
                   execution_report.toString())
  end  
  def on_cancel(cancel)
      shouldFail = get_parameter("shouldFailOnCancel")
      if(shouldFail != nil) 
          10 / 0
      end
      set_property("onCancel",
                   cancel.toString())
  end  
  def on_trade(trade)
      shouldFail = get_parameter("shouldFailOnTrade")
      if(shouldFail != nil) 
          10 / 0
      end
      set_property("onTrade",
                   trade.toString())
  end  
  def on_other(data)
      shouldFail = get_parameter("shouldFailOnOther")
      if(shouldFail != nil) 
          10 / 0
      end
      set_property("onOther",
                   data.toString())
  end
  def do_request_parameter_callbacks
    do_callbacks(get_parameter("shouldRequestCallbackAfter"),
                 get_parameter("shouldRequestCallbackAt"))
  end
  def do_request_properties_callbacks
    do_callbacks(get_property("shouldRequestCallbackAfter"),
                 get_property("shouldRequestCallbackAt"))
  end
  def do_callbacks(callbackAfter,
                   callbackAt)
    shouldDoubleCallbacks = get_parameter("shouldDoubleCallbacks")
    if(shouldDoubleCallbacks != nil)
      multiplier = 2
    else  
      multiplier = 1
    end
    callbackDataIsNull = get_parameter("callbackDataIsNull")
    for i in (1..multiplier)
      if(callbackDataIsNull != nil)
        if(callbackAfter != nil)
          request_callback_after(Long.parseLong(callbackAfter),
                                 nil)
        end
        if(callbackAt != nil)
            request_callback_at(Date.new(Long.parseLong(callbackAt)),
                                nil)
        end
      else
        if(callbackAfter != nil)
          request_callback_after(Long.parseLong(callbackAfter),
                                 self)
        end
        if(callbackAt != nil)
          request_callback_at(Date.new(Long.parseLong(callbackAt)),
                              self)
        end
      end
    end
  end
end
