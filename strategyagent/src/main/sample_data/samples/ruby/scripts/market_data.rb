#
# $License$
#
# author:anshul@marketcetera.com
# since 1.0.0
# version: $Id: market_data.rb 16633 2013-07-17 01:26:26Z colin $
#
#   
require 'java'
java_import org.marketcetera.strategy.ruby.Strategy
java_import org.marketcetera.marketdata.MarketDataRequestBuilder
java_import org.marketcetera.marketdata.AssetClass

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
      request_market_data(MarketDataRequestBuilder.newRequest().
          withSymbols(SYMBOLS).
          withProvider(MARKET_DATA_PROVIDER).
          withContent("TOP_OF_BOOK").create)
      # option
      request_market_data(MarketDataRequestBuilder.newRequest().
          withSymbols(OPTION_OSI_SYMBOL).
          withAssetClass(AssetClass::OPTION).
          withProvider(MARKET_DATA_PROVIDER).
          withContent("LATEST_TICK").create)
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
