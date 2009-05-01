#
# author:anshul@marketcetera.com
# since 1.0.0
# version: $Id$
#
#   
include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "org.marketcetera.marketdata.MarketDataRequest"

#######################################
# Strategy that receives marketdata   #
#######################################
class MarketData < Strategy
    SYMBOLS = "AMZN,JAVA" # Depends on MD - can be other symbols
    MARKET_DATA_PROVIDER = "marketcetera" # Can also be activ, bogus, opentick

    ##########################################
    # Executed when the strategy is started. #
    #                                        #
    # Use this method to set up data flows   #
    #  and other initialization tasks.       #
    ##########################################
    def on_start
      request = MarketDataRequest.newRequest().withSymbols(SYMBOLS).fromProvider(MARKET_DATA_PROVIDER)
      request_market_data(request)
    end

    ####################################################
    # Executed when the strategy receives an ask event #
    ####################################################
    def on_ask(ask)
      warn "Ask " + ask.to_s
    end

    ###################################################
    # Executed when the strategy receives a bid event #
    ###################################################
    def on_bid(bid)
      warn "Bid " + bid.to_s
    end

    #####################################################
    # Executed when the strategy receives a trade event #
    #####################################################
    def on_trade(trade)
      warn "Trade " + trade.to_s
    end

end
