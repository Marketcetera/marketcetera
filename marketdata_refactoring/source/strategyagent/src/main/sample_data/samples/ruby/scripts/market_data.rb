#
# $License$
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
    SYMBOLS = "AMZN,GOOG" # Depends on MD - can be other symbols
    OPTION_OSI_SYMBOL = "AAPL  091121C00123450" # AAPL, Nov'09 $123.45 Call
    MARKET_DATA_PROVIDER = "bogus" # Can be activ, bogus, marketcetera

    ##########################################
    # Executed when the strategy is started. #
    #                                        #
    # Use this method to set up data flows   #
    #  and other initialization tasks.       #
    ##########################################
    def on_start
      # equity
      request_market_data(MarketDataRequest.newRequest().
          withSymbols(SYMBOLS).
          fromProvider(MARKET_DATA_PROVIDER).
          withContent("TOP_OF_BOOK"))
      # option
      request_market_data(MarketDataRequest.newRequest().
          withSymbols(OPTION_OSI_SYMBOL).
          ofAssetClass("OPTION").
          fromProvider(MARKET_DATA_PROVIDER).
          withContent("LATEST_TICK"))
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
