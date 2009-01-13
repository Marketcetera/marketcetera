#
# author:anshul@marketcetera.com
# since 1.0.0
# version: $Id$
#
#   
include_class "org.marketcetera.strategy.ruby.Strategy"

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
      request_market_data(SYMBOLS,MARKET_DATA_PROVIDER)
    end

    ####################################################
    # Executed when the strategy receives an ask event #
    ####################################################
    def on_ask(ask)
      puts "Ask " + ask.to_s
    end

    ###################################################
    # Executed when the strategy receives a bid event #
    ###################################################
    def on_bid(bid)
      puts "Bid " + bid.to_s
    end

    #####################################################
    # Executed when the strategy receives a trade event #
    #####################################################
    def on_trade(trade)
      puts "Trade " + trade.to_s
    end

end
