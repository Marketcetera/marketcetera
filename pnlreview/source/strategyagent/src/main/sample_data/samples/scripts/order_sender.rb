#
# author:anshul@marketcetera.com
# since 1.0.0
# version: $Id$
#
#
'require java'
include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "org.marketcetera.trade.Factory"
include_class "org.marketcetera.trade.OrderType"
include_class "org.marketcetera.trade.Side"
include_class "org.marketcetera.trade.TimeInForce"
include_class "org.marketcetera.trade.MSymbol"
include_class "java.math.BigDecimal"

###############################
# Order Sender Strategy       #
###############################
class OrderSender < Strategy
    SYMBOLS = "AMZN" # Depends on MD - can be other symbols
    MARKET_DATA_PROVIDER = "marketcetera" # Can also be activ, bogus, opentick
    ACCOUNT = "accountable"

    ##########################################
    # Executed when the strategy is started. #
    #                                        #
    # Use this method to set up data flows   #
    #  and other initialization tasks.       #
    ##########################################
    def on_start
      @requestID = request_market_data SYMBOLS, MARKET_DATA_PROVIDER
      puts "Issued Market Data Request " + @requestID.to_s
      @receivedData = false
    end

    ###################################################
    # Executed when the strategy receives a bid event #
    ###################################################
    def on_bid(bid)
      if @receivedData then
        return
      end
      @receivedData = true

      puts "Bid: " + bid.to_s
      # Send an order to buy and cancel the request
      order = Factory.instance.createOrderSingle()
      order.setAccount ACCOUNT
      order.setOrderType OrderType::Limit
      order.setPrice bid.price
      order.setQuantity bid.size
      order.setSide Side::Buy
      order.setSymbol bid.symbol
      order.setTimeInForce TimeInForce::Day
      puts "Sending Order " + order.to_s

      order_id = send_order order
      puts "Sent Order:"+order_id.to_s

      cancel_data_request @requestID
      puts "Cancelled Market Data Request " + @requestID.to_s
    end

    ####################################################
    # Executed when the strategy receives an ask event #
    ####################################################
    def on_ask(ask)
      if @receivedData then
        return
      end
      @receivedData = true

      puts "Ask: " + ask.to_s
      # Send an order to sell and cancel the request
      order = Factory.instance.createOrderSingle()
      order.setAccount ACCOUNT
      order.setOrderType OrderType::Limit
      order.setPrice ask.price
      order.setQuantity ask.size
      order.setSide Side::Sell
      order.setSymbol ask.symbol
      order.setTimeInForce TimeInForce::Day
      puts "Sending Order " + order.to_s

      order_id = send_order order
      puts "Sent Order:"+order_id.to_s

      cancel_data_request @requestID
      puts "Cancelled Market Data Request " + @requestID.to_s
    end

    ###########################################################
    # Executed when the strategy receives an execution report #
    ###########################################################
    def on_execution_report(executionReport)
      puts "Received Execution Report:" + executionReport.to_s
    end
end
