include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "java.lang.Long"

class SendEvent < Strategy
    def on_start
        @askCounter = 0
    end
    def on_other (data)
        do_send_event data
    end
    def on_callback(data)
        if(get_property("shouldRequestCEPData") != nil)
            do_cep_request
        else
            cancel_all_data_requests
        end
    end
    def on_ask(ask)
        @askCounter += 1
        set_property "askCount", Long.toString(@askCounter)
        set_property "ask", ask.toString
    end
    def do_send_event (event)
        if(get_property("eventOnlyTest") != nil)
            if(get_property("nilEvent") != nil)
                send_event nil
                return
            end
            send_event event
        else
            source = get_property "source"
            if(get_property("nilSource") != nil)
                send_event_to_cep event, nil  
                return
            end
            if(get_property("nilEvent") != nil)
                send_event_to_cep nil, source
                return
            end
            send_event_to_cep event, source
        end
    end
    def do_cep_request
        cepDataSource = get_property "source"
        if(cepDataSource != nil)
            statementString = get_property "statements"
            if(statementString != nil) 
                statements = statementString.split("#")
            else
                statements = nil
            end
            set_property("requestID", Long.toString(request_cep_data(statements.to_java(:string), cepDataSource)))
        end
    end
end
