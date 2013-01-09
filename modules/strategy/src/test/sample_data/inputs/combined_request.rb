include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "org.marketcetera.marketdata.MarketDataRequestBuilder"
include_class "java.lang.Integer"

class CombinedRequest < Strategy
  def on_start
      @asks = {}
      @bids = {}
      @totalEventCount = 0
  end
  def on_other data
      do_combined_request
  end
  def on_callback data
      requestIDString = get_property "requestID"
      if(get_property("cancelCep") != nil)
          cancel_data_request Integer.parseInt requestIDString
      else
          cancel_data_request Integer.parseInt requestIDString
      end
  end
  def on_ask ask
      record_symbol ask.getInstrument.to_s, @asks
      transcribe_collection "ask", @asks
  end
  def on_bid bid
      record_symbol bid.getInstrument.to_s, @bids
      transcribe_collection "bid", @bids
  end
  def record_symbol symbol, collection
      if (collection.has_key? symbol)
          collection[symbol] = collection[symbol] + 1
      else
          collection[symbol] = 1
      end
      @totalEventCount += 1
      if(@totalEventCount >= 50)
          set_property "finished", "true"
      end
  end
  def transcribe_collection key, collection
      collection.each {|symbol, value| set_property key + "-" + symbol, (value += 1).to_s }
  end
  def do_combined_request
      symbols = get_property "symbols"
      marketDataSource = get_property "marketDataSource"
      compressedStatements = get_property "statements"
      if(compressedStatements != nil)
        statements = compressedStatements.split("#")
        statementsToUse = statements.to_java(:string)
      else
        statementsToUse = nil
      end 
      cepSource = get_property "cepSource"
      stringAPI = get_property("useStringAPI")
      begin
        if(stringAPI != nil)
          set_property("requestID",
                       Integer.toString(request_processed_market_data(MarketDataRequestBuilder.newRequest().withContent("LATEST_TICK,TOP_OF_BOOK").withSymbols(symbols).
                         withProvider(marketDataSource).create.to_s,
                       statementsToUse,
                       cepSource)))
        else
          set_property("requestID",
                       Integer.toString(request_processed_market_data(MarketDataRequestBuilder.newRequest().withContent("LATEST_TICK,TOP_OF_BOOK").withSymbols(symbols).
                         withProvider(marketDataSource).create,
                       statementsToUse,
                       cepSource)))
        end
        rescue Exception => e
          puts "#{ e } (#{ e.class })"
      end
  end  
end
