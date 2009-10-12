include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "org.marketcetera.trade.Equity"
include_class "java.math.BigDecimal"
include_class "java.lang.System"
include_class "java.lang.Boolean"

class SendOther < Strategy
    def on_ask(ask)
        if(get_property("sendNull") != nil)
            send nil
            return
        end
        if(get_property("sendString") != nil)
            send "test string"
            return
        end
        if(get_property("sendTwo") != nil)
            send BigDecimal::ONE
            send BigDecimal::TEN
        end
    end
end
