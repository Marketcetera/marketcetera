require 'java'
java_import org.marketcetera.strategy.ruby.Strategy
java_import org.marketcetera.trade.Equity
java_import java.math.BigDecimal
java_import java.lang.System
java_import java.lang.Boolean

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
