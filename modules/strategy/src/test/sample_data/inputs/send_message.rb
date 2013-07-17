require 'java'
java_import org.marketcetera.strategy.ruby.Strategy
java_import org.marketcetera.quickfix.FIXVersion
java_import org.marketcetera.trade.BrokerID
java_import java.lang.Long
java_import java.util.Date
java_import 'quickfix.field.TransactTime'

class SendMessage < Strategy
    def on_start
        do_send
    end
    def on_stop
        do_send
    end
    def do_send
        messageDate = Long.parseLong(get_parameter("date"))

        nullMessage = get_parameter("nullMessage")
        if(nullMessage == nil)
            message = FIXVersion.getFIXVersion("FIX.0.0").getMessageFactory().newBasicOrder()
            message.setField(TransactTime.new(Date.new(messageDate)))
        else
            message = nil
        end

        nullBroker = get_parameter("nullBroker")
        if(nullBroker == nil)
            broker = BrokerID.new("some-broker")
        else
            broker = nil
        end
        
        send_message(message, broker)
    end  
end
