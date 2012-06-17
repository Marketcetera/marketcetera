require 'java'
include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "org.marketcetera.trade.Equity"
include_class "java.lang.Long"
include_class "java.lang.System"
include_class "java.util.Arrays"
include_class "java.math.BigDecimal"

class WidthStrategy < Strategy

  WIDTH_SPREAD = 0.5
  TOTAL_SHARES = 1000
  DELAY_TILL_CANCEL = 5000 # In milliseconds  
  EQUITIES = {'FRO' => QF::Side::SELL, 'SUNW' => QF::Side::SELL, 'GOOG' =>QF::Side::BUY, 'MSFT'=>QF::Side::SELL, 
    'POI'=> QF::Side::BUY, 'QWE'=> QF::Side::SELL, 'XYZ'=> QF::Side::BUY, 'T' => QF::Side::SELL }
  PER_ORDER_LIMIT = BigDecimal.new("20")
  FIX_VERSION = "FIX.4.2"
  ACCOUNT = nil
  
  def initialize
    @waitingForTimeout = {}
    @cancels = {}
  end
  
  def on_bid(bidEvent)
    bid = bidEvent.getPrice().to_s
    
  end
  
  def on_ask(ask)
    
  end
  
  def on_market_data_snapshot(message)
    symbol = message.getString(QF::Symbol::FIELD)
    if(EQUITIES[symbol].nil?)
      return
    end
      
    if(@waitingForTimeout[symbol])
      puts "already waiting for timeout for #{symbol}"
      return
    end
    
    bid = BigDecimal.new(extractMD(message, QF::MDEntryType::BID, QF::MDEntryPx::FIELD).to_s)
    ask = BigDecimal.new(extractMD(message, QF::MDEntryType::OFFER, QF::MDEntryPx::FIELD).to_s)
    if((ask - bid) < WIDTH_SPREAD)
       puts "Market below minimum width #{WIDTH_SPREAD}: #{symbol} [#{bid} - #{ask}] at "+Time.now.to_s
       side = EQUITIES[symbol]
       price = get_limit_price(side, bid, ask)
       order = message_factory.newLimitOrder(getIDFactory().next, side, PER_ORDER_LIMIT, 
          Marketcetera::Equity.new(symbol), java.math.BigDecimal.new(price.to_s), 
          QF::TimeInForce::GOOD_TILL_CANCEL, ACCOUNT)
       @cancels[symbol] = message_factory.newCancelFromMessage(order)
       registerTimedCallback(DELAY_TILL_CANCEL, @cancels[symbol])
       sendFIXMessage(order)
       @waitingForTimeout[symbol] = true
       puts "set waiting: #{@waitingForTimeout[symbol]} on #{symbol}"
    end    
  end
  
  def on_execution_report(execReport)
    symbol = execReport.getString(QF::Symbol::FIELD)
    @waitingForTimeout[symbol] = false
    @cancels[symbol] = nil
    puts "got an exec report for #{symbol}, resetting the waitingforTimeout"
  end
  
  def timeout_callback(client_data)
    symbol = client_data.getString(QF::Symbol::FIELD)
    puts "in callback method for #{symbol} with waiting #{@waitingForTimeout[symbol]}"
    if(@waitingForTimeout[symbol] && !@cancels[symbol].nil?)
    reregister = false
    begin
         theCancel = @cancels[symbol]
         execReport = getLatestExecutionReport(theCancel.getString(QF::ClOrdID::FIELD))
         if(!execReport.nil?)
           orderID = execReport.getString(QF::OrderID::FIELD)
           theCancel.setField(QF::OrderID.new(orderID))
           puts "updated cancel for #{symbol} with orderID: #{orderID}"
         else 
          puts "haven't seen execReport for #{symbol} yet, re-registering callback"
          reregister = true
         end
         sendFIXMessage(theCancel)
         if(reregister)
           puts "sent a cancel for #{symbol} with missing ClOrdID and reregistered callback"
           registerTimedCallback(DELAY_TILL_CANCEL, @cancels[symbol])
         else
           puts "sent a cancel for #{symbol} in timeout callback and reset variables: #{theCancel}"
           @waitingForTimeout[symbol] = false
           @cancels[symbol] = nil
         end
      rescue Exception => ex
        puts "exception while trying to send a cancel for #{symbol}, hitting the cancelAll panic button: #{ex}"
        cancelAllOpenOrders()
        @waitingForTimeout = {}
        @cancels = {}
      end
    else 
      puts "no longer waiting for cancel for #{symbol}"
    end
  end
  
  # Based on the incoming side, figures out which (bid/ask) price to return for a limit order
  def get_limit_price(side, bid, ask)
    return (side == QF::Side::BUY) ? ask : bid
  end
end
