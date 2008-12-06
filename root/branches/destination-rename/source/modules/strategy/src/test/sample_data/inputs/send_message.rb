include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "org.marketcetera.quickfix.FIXVersion"
include_class "org.marketcetera.trade.DestinationID"
include_class "java.lang.Long"
include_class "java.util.Date"
include_class "quickfix.field.TransactTime"

class SendMessage < Strategy
    def on_start
        messageDate = Long.parseLong(get_parameter("date"))

        nullMessage = get_parameter("nullMessage")
        if(nullMessage == nil)
            message = FIXVersion.getFIXVersion("FIX.0.0").getMessageFactory().newBasicOrder()
            message.setField(TransactTime.new(Date.new(messageDate)))
        else
            message = nil
        end

        nullDestination = get_parameter("nullDestination")
        if(nullDestination == nil)
            destination = DestinationID.new("some-destination")
        else
            destination = nil
        end
        
        send_message(message, destination)
    end  
end
