#
# author:anshul@marketcetera.com
# since 1.0.0
# version: $Id$
#
#
'require java'
include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "org.marketcetera.marketdata.MarketDataRequest"

##################################################
# Strategy that processes market data via CEP    #
##################################################
class ProcessData < Strategy
    SYMBOLS = "AMZN,JAVA" # Depends on MD - can be other symbols
    CONTENT = "LATEST_TICK"
    MARKET_DATA_PROVIDER = "marketcetera" # Can also be activ, bogus, opentick
    CEP_QUERY = ["select t.symbolAsString as symbol, t.price * t.size as position from trade t"]
    CEP_PROVIDER = "esper"
    

    ##########################################
    # Executed when the strategy is started. #
    #                                        #
    # Use this method to set up data flows   #
    #  and other initialization tasks.       #
    ##########################################
    def on_start
      request = MarketDataRequest.newRequest().withSymbols(SYMBOLS).fromProvider(MARKET_DATA_PROVIDER).withContent(CONTENT)      
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
