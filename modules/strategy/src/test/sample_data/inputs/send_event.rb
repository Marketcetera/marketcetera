require 'java'
java_import org.marketcetera.strategy.ruby.Strategy
java_import org.marketcetera.strategy.OutputType
java_import org.marketcetera.event.impl.TradeEventBuilder
java_import org.marketcetera.trade.Equity
java_import org.marketcetera.module.DataRequest
java_import java.lang.Long
java_import java.util.Date
java_import java.math.BigDecimal

class SendEvent < Strategy
    def on_start
        @askCounter = 0
        dataRequests = Array.new
        dataRequests << DataRequest.new(get_urn, OutputType::ALL)
        @dataFlowID = create_data_flow true, dataRequests.to_java(DataRequest)
    end
    def on_stop
        tradeBuilder = TradeEventBuilder.equityTradeEvent
        tradeBuilder.withInstrument(Equity.new("METC")).withExchange("exchange")
        tradeBuilder.withPrice(BigDecimal::ONE).withSize(BigDecimal::TEN)
        tradeBuilder.withTradeDate(Date.new)
        do_send_event tradeBuilder.create
        cancel_data_flow @dataFlowID
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
