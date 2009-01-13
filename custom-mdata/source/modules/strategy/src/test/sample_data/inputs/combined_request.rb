include_class "org.marketcetera.strategy.ruby.Strategy"
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
      record_symbol ask.getSymbol, @asks
      transcribe_collection "ask", @asks
  end
  def on_bid bid
      record_symbol bid.getSymbol, @bids
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
      cepSource = get_property "cepSource"
      compressedStatements = get_property "statements"
      if(compressedStatements != nil) 
          statements = compressedStatements.split("#")
          set_property("requestID", Integer.toString(request_processed_market_data(symbols, marketDataSource, statements.to_java(:string), cepSource)))
      else
          set_property("requestID", Integer.toString(request_processed_market_data(symbols, marketDataSource, nil, cepSource)))
      end
  end  
end
