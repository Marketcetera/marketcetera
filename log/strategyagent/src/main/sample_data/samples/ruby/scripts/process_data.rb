#
# $License$
#
# author:anshul@marketcetera.com
# since 1.0.0
# version: $Id$
#
#
require 'java'
java_import org.marketcetera.strategy.ruby.Strategy
java_import org.marketcetera.marketdata.MarketDataRequestBuilder

##################################################
# Strategy that processes market data via CEP    #
##################################################
class ProcessData < Strategy
    SYMBOLS = "AMZN,JAVA" # Depends on MD - can be other symbols
    CONTENT = "LATEST_TICK"
    MARKET_DATA_PROVIDER = "marketcetera" # Can be activ, bogus, marketcetera
    CEP_QUERY = ["select t.instrumentAsString as symbol, t.price * t.size as position from trade t"]
    CEP_PROVIDER = "esper"
    

    ##########################################
    # Executed when the strategy is started. #
    #                                        #
    # Use this method to set up data flows   #
    #  and other initialization tasks.       #
    ##########################################
    def on_start
      request = MarketDataRequestBuilder.newRequest().withSymbols(SYMBOLS).withProvider(MARKET_DATA_PROVIDER).withContent(CONTENT).create      
      request_processed_market_data(request, CEP_QUERY.to_java(:string), CEP_PROVIDER)
    end


    ############################################################
    # Executed when the strategy receives data of a type other #
    #  than the other callbacks                                #
    ############################################################
    def on_other(data)
      warn "Trade " + data.to_s
    end
end
