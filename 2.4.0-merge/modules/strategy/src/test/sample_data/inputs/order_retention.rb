require 'java'
java_import org.marketcetera.strategy.ruby.Strategy
java_import org.marketcetera.trade.Factory
java_import org.marketcetera.trade.Equity
java_import org.marketcetera.trade.OrderType
java_import org.marketcetera.trade.Side
java_import org.marketcetera.trade.TimeInForce

java_import java.lang.Long
java_import java.math.BigDecimal

###############################
# Sample strategy template    #
###############################
class OrderRetention < Strategy
  
    ##########################################
    # Executed when the strategy is started. #
    #                                        #
    # Use this method to set up data flows   #
    #  and other initialization tasks.       #
    ##########################################
    def on_start
        set_property("executionReportCounter","0")
        @executionReportCounter = 0
        set_property("orderIDs", "")
        1.upto(Long.parseLong(get_parameter("ordersToSubmit"))) { generate_and_submit_order }
    end
    
    ###########################################################
    # Executed when the strategy receives an execution report #
    ###########################################################
    def on_execution_report(executionReport)
        # register this execution report along with its value
        set_property executionReport.getOrderID().getValue(), executionReport.toString()
        # increment the execution report counter
        @executionReportCounter += 1
        set_property "executionReportCounter", @executionReportCounter.to_s
    end
    
    def generate_and_submit_order
        order = Factory.getInstance().createOrderSingle()
        order.setOrderType OrderType::Limit
        order.setPrice BigDecimal::ONE
        order.setQuantity BigDecimal::TEN
        order.setSide Side::Buy
        order.setInstrument Equity.new("METC")
        order.setTimeInForce TimeInForce::GoodTillCancel
        if(send(order))
            record_orderid order.getOrderID
        end
    end
    
    def record_orderid(orderid)
        currentOrderIDList = get_property "orderIDs"
        currentOrderIDList += "," + orderid.getValue().to_s
        set_property "orderIDs", currentOrderIDList
    end
end
