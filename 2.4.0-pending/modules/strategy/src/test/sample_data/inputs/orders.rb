require 'java'
java_import org.marketcetera.strategy.ruby.Strategy
java_import org.marketcetera.trade.Factory
java_import org.marketcetera.trade.OrderType
java_import org.marketcetera.trade.OrderID
java_import org.marketcetera.trade.OrderSingle
java_import org.marketcetera.trade.OrderReplace
java_import org.marketcetera.trade.Side
java_import org.marketcetera.trade.TimeInForce
java_import org.marketcetera.trade.Equity
java_import java.math.BigDecimal
java_import java.util.List
java_import java.lang.System
java_import java.lang.Long
java_import java.lang.Boolean

class Orders < Strategy
    def on_ask(ask)
      if(get_property("orderShouldBeNull") != nil)
          do_order_send false
      else
          do_order_send true
      end
    end
    def on_bid(bid)
        orderID = get_property("orderID")
        if(orderID != nil)
            exeReports = get_execution_reports(OrderID.new(orderID))
            set_property("executionReportCount",
                         exeReports.size.to_s)
        end      
    end
    def on_trade(trade)
        if(get_property("cancelAll") != nil)
            ordersCanceled = cancel_all_orders
            set_property("ordersCanceled",
                         Long.toString(ordersCanceled))
        end
    end
    def on_execution_report(execution_report)
        executionReportsReceived = get_property("executionReportsReceived")
        if(executionReportsReceived == nil)
            set_property("executionReportsReceived",
                         "1")
        else
            set_property("executionReportsReceived",
                         Long.toString(Long.parseLong(executionReportsReceived) + 1))
        end
    end
    def on_other(event)
        if(event.instance_of? OrderID)
            shouldSkipSend = get_property("skipSubmitOrders")
            shouldDelaySend = get_property("delaySubmitOrders")
            newAccountName = get_property("newAccountName")
            orderCancel = cancel_order event, (shouldSkipSend == nil ? true : false)
            set_property("orderCanceled",
                         orderCancel == nil ? "false" : "true")
            if shouldDelaySend != nil
                orderCancel.setAccount newAccountName
                send orderCancel
            end
        else
            if event.kind_of? OrderSingle
                do_cancel_replace_test event
            else
                if event.instance_of? String
                    do_cancel_replace_test event
                else 
                    orderCancel = cancel_order nil, true
                    set_property("orderCanceled",
                                 orderCancel == nil ? "false" : "true")
                end
            end
        end
    end
    def do_cancel_replace_test(event)
        orderIDString = get_property("orderID")
        shouldSkipSend = get_property("skipSubmitOrders")
        shouldDelaySend = get_property("delaySubmitOrders")
        if orderIDString == nil or orderIDString.empty?
            orderID = nil
        else
            orderID = OrderID.new orderIDString
        end
        if event.kind_of? OrderSingle
            newOrder = cancel_replace orderID, event, (shouldSkipSend == nil ? true : false)
            if shouldDelaySend != nil
                newOrder.setQuantity newOrder.getQuantity.add BigDecimal::ONE
                send newOrder
            end
        else  
            newOrder = cancel_replace orderID, nil, true
        end
        set_property "newOrderID", (newOrder == nil ? nil : newOrder.getOrderID.toString()) 
    end
    def on_stop
        do_order_send true
        rawOrderID = get_property("orderID")
        if(rawOrderID != nil)
            orderID = OrderID.new(rawOrderID)
            if(get_property("shouldReplace") != nil)
                orderSingle = Factory.getInstance().createOrderSingle()
                orderReplace = cancel_replace orderID, orderSingle, false
                if(orderReplace != nil)
                    set_property "orderReplaceNull", "false"
                else
                    set_property "orderReplaceNull", "true"
                end
            else
                orderCancel = cancel_order orderID,false
                if(orderCancel != nil)
                    set_property "orderCancelNull", "false"
                else
                    set_property "orderCancelNull", "true"
                end
            end
        end
        set_property "allOrdersCanceled", cancel_all_orders.to_s
    end
    def do_order_send(sendOrder)
        if(sendOrder)
            order = Factory.getInstance().createOrderSingle()
            order.setAccount(get_property("account"))
            orderType = get_property("orderType")
            if(orderType != nil)
                order.setOrderType(OrderType.valueOf(orderType))
            end
            price = get_property("price")
            if(price != nil)
                order.setPrice(BigDecimal.new(price))
            end
            quantity = get_property("quantity")
            if(quantity != nil)
                order.setQuantity(BigDecimal.new(quantity))
            end
            side = get_property("side")
            if(side != nil)
                order.setSide(Side.valueOf(side))
            end
            symbol = get_property("symbol")
            if(symbol != nil)
                order.setInstrument(Equity.new(symbol))
            end
            timeInForce = get_property("timeInForce")
            if(timeInForce != nil)
                order.setTimeInForce(TimeInForce.valueOf(timeInForce))
            end
            set_property("orderID",
                         order.getOrderID.to_s)
            set_property("transactTime",
                         Long.toString(System.currentTimeMillis))
            if(send order)
                set_property("sendResult",
                             "true")
            else
                set_property("sendResult",
                             "false")
            end
        else
            send nil
        end
    end
end
