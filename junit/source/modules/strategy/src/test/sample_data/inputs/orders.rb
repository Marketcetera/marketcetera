include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "org.marketcetera.trade.Factory"
include_class "org.marketcetera.trade.OrderType"
include_class "org.marketcetera.trade.OrderID"
include_class "org.marketcetera.trade.OrderSingle"
include_class "org.marketcetera.trade.Side"
include_class "org.marketcetera.trade.TimeInForce"
include_class "org.marketcetera.trade.MSymbol"
include_class "java.math.BigDecimal"
include_class "java.util.List"
include_class "java.lang.System"
include_class "java.lang.Long"
include_class "java.lang.Boolean"

class Orders < Strategy
    def on_ask(ask)
      if(get_property("orderShouldBeNull") != nil)
          send_order nil
      else
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
              order.setSymbol(MSymbol.new(symbol))
          end
          timeInForce = get_property("timeInForce")
          if(timeInForce != nil)
              order.setTimeInForce(TimeInForce.valueOf(timeInForce))
          end
          set_property("orderID",
                       order.getOrderID.to_s)
          set_property("transactTime",
                       Long.toString(System.currentTimeMillis))
          send_order order
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
            set_property("orderCanceled",
                         Boolean.toString(cancel_order(event)))
        else
            if event.kind_of? OrderSingle
                do_cancel_replace_test event
            else
                if event.instance_of? String
                    do_cancel_replace_test event
                else 
                    set_property("orderCanceled",
                                 Boolean.toString(cancel_order(nil)))
                end
            end
        end
    end
    def do_cancel_replace_test(event)
        orderIDString = get_property("orderID")
        if orderIDString == nil or orderIDString.empty?
            orderID = nil
        else
            orderID = OrderID.new orderIDString
        end
        if event.kind_of? OrderSingle
            newOrderID = cancel_replace orderID, event
        else  
            newOrderID = cancel_replace orderID, nil
        end
        set_property "newOrderID", (newOrderID == nil ? nil : newOrderID.toString()) 
    end
end