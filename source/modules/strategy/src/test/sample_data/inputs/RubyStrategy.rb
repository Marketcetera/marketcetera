include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "org.marketcetera.marketdata.MarketDataRequestBuilder"
include_class "org.marketcetera.trade.Factory"
include_class "org.marketcetera.trade.Equity"
include_class 'org.marketcetera.util.test.UnicodeData'
include_class "java.math.BigDecimal"
include_class "java.lang.System"
include_class "java.lang.Long"
include_class "java.util.Date"
include_class 'java.lang.InterruptedException'

class RubyStrategy < Strategy
  def on_start
      set_property "onStartBegins", Long.toString(System.currentTimeMillis())
      @callbackCounter = 0
      shouldFail = get_parameter("shouldFailOnStart")
      if(shouldFail != nil) 
          10 / 0
      end
      shouldLoop = get_parameter("shouldLoopOnStart")
      if(shouldLoop != nil)
          shouldStopLoop = get_property("shouldStopLoop")
          begin
              while(shouldStopLoop == nil)
                  sleep 0.1
                  shouldStopLoop = get_property("shouldStopLoop")
              end
              rescue InterruptedException => e
                  break
              end
          set_property "loopDone", "true"
      end
      marketDataSource = get_parameter("shouldRequestData")
      if(marketDataSource != nil)
        symbols = get_parameter("symbols")
        content = get_parameter("content")
        if(content == nil)
          content = "LATEST_TICK,TOP_OF_BOOK";
        end
          stringAPI = get_parameter "useStringAPI"
          if(stringAPI != nil)
            set_property("requestID",
                         (request_market_data(MarketDataRequestBuilder.newRequest().
                                              withContent(content.to_s).withSymbols(symbols).withProvider(marketDataSource).create.to_s)).to_s)
          else
            set_property("requestID",
                         (request_market_data(MarketDataRequestBuilder.newRequest().
                                              withContent(content.to_s).withSymbols(symbols).withProvider(marketDataSource).create)).to_s)
          end
      end
      if(get_parameter("shouldRequestCEPData") != nil)
          do_cep_request
      end
      do_request_parameter_callbacks
      do_request_properties_callbacks
      if(get_parameter("shouldNotify") != nil) 
          notify_low("low subject", Long.toString(System.nanoTime()))
          notify_medium("medium subject", Long.toString(System.nanoTime()))
          notify_high("high subject", Long.toString(System.nanoTime()))
          debug nil
          debug ""
          debug "Some statement"
          debug UnicodeData::HOUSE_AR
          info nil
          info ""
          info "Some statement"
          info UnicodeData::HOUSE_AR
          warn nil
          warn ""
          warn "Some statement"
          warn UnicodeData::HOUSE_AR
          error nil
          error ""
          error "Some statement"
          error UnicodeData::HOUSE_AR
     end
     if(get_property("askForBrokers") != nil)
        brokers = get_brokers
        counter = 0
        brokers.each { |broker| set_property counter.to_s, broker.toString; counter += 1 }
     end
     userdata = get_user_data
     userdata.setProperty "onStart", Long.toString(System.currentTimeMillis())
     set_user_data userdata
     set_property("onStart",
                   Long.toString(System.currentTimeMillis()))
  end
  def on_stop
      set_property "onStopBegins", Long.toString(System.currentTimeMillis())
      shouldFail = get_property("shouldFailOnStop")
      if(shouldFail != nil) 
          10 / 0
      end
      marketDataSource = get_parameter("shouldRequestDataOnStop")
      if(marketDataSource != nil)
          symbols = get_parameter("symbols")
          set_property("requestID",
                       Long.toString(request_market_data(MarketDataRequestBuilder.newRequest().withContent("LATEST_TICK").withSymbols(symbols).withProvider(marketDataSource).create)))
      end
      shouldLoop = get_parameter("shouldLoopOnStop")
      if(shouldLoop != nil)
          shouldStopLoop = get_property("shouldStopLoop")
          while(shouldStopLoop == nil)
              sleep 0.1
              shouldStopLoop = get_property("shouldStopLoop")
          end
          set_property "loopDone", "true"
      end
      set_property("onStop",
                   Long.toString(System.currentTimeMillis()))
  end
  def on_ask(ask)
      shouldFail = get_parameter("shouldFailOnAsk")
      if(shouldFail != nil) 
          10 / 0
      end
      if(get_parameter("shouldRequestCEPData") != nil)
          suggestion_from_event ask
      end
      set_property("onAsk",
                   ask.toString())
  end  
  def on_bid(bid)
      shouldFail = get_parameter("shouldFailOnBid")
      if(shouldFail != nil) 
          10 / 0
      end
      if(get_parameter("shouldRequestCEPData") != nil)
          suggestion_from_event bid
      end
      set_property("onBid",
                   bid.toString())
  end
  def on_marketstat(statistics)
    shouldFail = get_parameter("shouldFailOnStatistics")
    if(shouldFail != nil) 
        10 / 0
    end
    set_property("onStatistics",
                 statistics.toString())
  end  
  def on_callback(data)
    @callbackCounter += 1
    shouldCancel = get_property("shouldCancel")
    if(shouldCancel != nil)
        requestToCancel = get_property("requestID")
        cancel_data_request Long.parseLong(requestToCancel)
    end
      shouldFail = get_parameter("shouldFailOnCallback")
      if(shouldFail != nil) 
          10 / 0
      end
      if(get_parameter("shouldRequestCEPData") != nil)
          cancel_all_data_requests
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
  def on_cancel_reject(cancel)
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
      if(get_parameter("shouldRequestCEPData") != nil)
          suggestion_from_event trade
      end
      set_property("onTrade",
                   trade.toString())
  end  
  def on_other(data)
      shouldFail = get_parameter("shouldFailOnOther")
      if(shouldFail != nil) 
          10 / 0
      end
      if(get_property("shouldCancelCEPData") != nil)
          cancel_data_request Long.parseLong get_property "requestID"
      end
      set_property("onOther",
                   data.toString())
  end
  def do_request_parameter_callbacks
    do_callbacks(get_parameter("shouldRequestCallbackAfter"),
                 get_parameter("shouldRequestCallbackAt"),
                 get_parameter("shouldRequestCallbackEvery"))
  end
  def do_request_properties_callbacks
    do_callbacks(get_property("shouldRequestCallbackAfter"),
                 get_property("shouldRequestCallbackAt"),
                 get_property("shouldRequestCallbackEvery"))
  end
  def do_callbacks(callbackAfter,
                   callbackAt,
                   callbackEvery)
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
        if(callbackEvery != nil)
            request_callback_every(Long.parseLong(callbackEvery),
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
        if(callbackEvery != nil)
          params = callbackEvery.split(',')
          begin
            request_callback_every(Long.parseLong(params[0]),
                                Long.parseLong(params[1]), self)
          rescue Exception=>e
            print e.backtrace.join("\n"),"\n"
            set_property("callbackEveryException",e.cause.class.name)
          end            
        end        
      end
    end
  end
  def on_dividend(dividend)
      shouldFail = get_parameter("shouldFailOnDividend")
      if(shouldFail != nil) 
          10 / 0
      end
      set_property("onDividend",
                   dividend.toString())
  end  
  def suggestion_from_event(event)
      suggestedOrder = Factory.getInstance().createOrderSingle()
      suggestedOrder.setPrice event.getPrice
      suggestedOrder.setInstrument event.getInstrument
      suggestedOrder.setQuantity event.getSize
      suggest_trade suggestedOrder, BigDecimal.new("1.0"), "CEP Event Received" 
  end
  def do_cep_request
      cepDataSource = get_parameter("source")
      if(cepDataSource != nil)
          statementString = get_parameter "statements"
          if(statementString != nil) 
              statements = statementString.split("#")
          else
              statements = nil
          end
          set_property("requestID", Long.toString(request_cep_data(statements.to_java(:string), cepDataSource)))
      end
  end
end
