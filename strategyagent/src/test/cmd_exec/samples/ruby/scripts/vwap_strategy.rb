#
# $License$
#
# author:Toli Kuznets
# author:anshul@marketcetera.com
# since 2.0.0
# version: $Id$
#
#
require 'java'
java_import org.marketcetera.strategy.ruby.Strategy
java_import java.lang.System
java_import org.marketcetera.trade.Factory
java_import org.marketcetera.trade.OrderType
java_import org.marketcetera.trade.Side
java_import org.marketcetera.trade.TimeInForce
java_import org.marketcetera.trade.Equity
java_import org.marketcetera.marketdata.MarketDataRequestBuilder
java_import java.math.BigDecimal


###############################
# Strategy implementation
# that calculates the VWAP
# in several names, and
# at some "random" point
# in the future, sends an order
# at that price.
###############################
class VWAPStrategy < Strategy
    SYMBOLS = ["AMZN","GOOG","MSFT"] # Depends on MD - can be other symbols
    MARKET_DATA_PROVIDER = "bogus" # Can be activ, bogus, marketcetera
    CONTENT = "LATEST_TICK"
    CEP_QUERY = ["SELECT t.instrumentAsString AS instrument, sum(cast(t.price, double) * cast(t.size, double))/sum(cast(t.size, double)) AS vwap FROM trade t GROUP BY instrument"]
    CEP_PROVIDER = "esper"

    ##########################################
    # Executed when the strategy is started. #
    #                                        #
    # Use this method to set up data flows   #
    #  and other initialization tasks.       #
    ##########################################
    def on_start
      @vwaps = {}
      request = MarketDataRequestBuilder.newRequest().withSymbols(SYMBOLS.to_java(:string)).withProvider(MARKET_DATA_PROVIDER).withContent(CONTENT).create
      request_processed_market_data(request, CEP_QUERY.to_java(:string), CEP_PROVIDER)
      request_callback_after((1000*10), nil) # register for callback in 10 seconds
    end


    ############################################################
    # Executed when the strategy receives a callback requested #
    #  via request_callback_at or request_callback_after       #
    #  Loops through all interesting symbols, finds the most   #
    # recent VWAP value, and sends an order at that price      #
    ############################################################
    def on_callback(data)
      #send a buy order for each of the symbols
      info "inside callback iterating"
      SYMBOLS.each do |symbol|
        if (!@vwaps[symbol].nil?)
          info "About to send orders for #{symbol} with vwap of #{@vwaps[symbol].to_s}"
          order = Factory.instance.createOrderSingle()
          order.side = Side::Buy
          order.quantity = BigDecimal.new("1000.0")
          order.instrument = Equity.new(symbol)
          order.order_type = OrderType::Limit
          order.time_in_force = TimeInForce::Day
          order.price = BigDecimal.new(@vwaps[symbol])
          info "Sending order #{order}"
          send order      
        else 
          warn "didn't find anything for #{symbol} and checked value was #{@vwaps[symbol]} within #{@vwaps.inspect}"
        end
      end
      request_callback_after((1000*10), nil) # register for callback in 10 seconds
    end

    ############################################################
    # Executed when the strategy receives data of a type other #
    #  than the other callbacks.  Stores the VWAP price        #
    ############################################################
    def on_other(data)
      info "setting vwap for symbol #{data['instrument']} #{data['vwap']}"
      @vwaps[data['instrument']] = data['vwap'].to_s
    end
end
