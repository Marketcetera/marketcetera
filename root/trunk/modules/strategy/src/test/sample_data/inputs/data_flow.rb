include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "org.marketcetera.strategy.OutputType"
include_class "org.marketcetera.module.DataFlowID"
include_class "org.marketcetera.module.DataRequest"
include_class "org.marketcetera.module.ModuleURN"

###########################################################
# Test the ability of a strategy to manipulate data flows #
###########################################################
class DataFlow < Strategy
  
    ##########################################
    # Executed when the strategy is started. #
    #                                        #
    # Use this method to set up data flows   #
    #  and other initialization tasks.       #
    ##########################################
    def on_start
        @dataFlowID = do_data_flow
        if !@dataFlowID.nil? 
            set_property "dataFlowID", @dataFlowID.getValue
        end
    end
    
    ############################################
    # Executed when the strategy is stopped.   #
    ############################################
    def on_stop
        if !@dataFlowID.nil? && get_parameter("shouldSkipCancel").nil?
            cancel_data_flow @dataFlowID
            set_property "dataFlowStopped", "true"
        end
        if !get_parameter("shouldMakeNewRequest").nil?
            set_property "newDataFlowAttempt", "false"
            newDataFlowID = do_data_flow
            set_property "newDataFlowAttempt", "true"
            if newDataFlowID.nil?
                set_property "newDataFlowID", "null"
            else
                set_property "newDataFlowID", newDataFlowID.getValue
            end
        end
    end
    
    ####################################################
    # Executed when the strategy receives an ask event #
    ####################################################
    def on_ask(ask)
        send ask      
    end
    
    ###################################################
    # Executed when the strategy receives a bid event #
    ###################################################
    def on_bid(bid)
        send bid      
    end
    
    ##########################################################
    # Executed when the strategy receives a statistics event #
    ##########################################################
    def on_marketstat(statistics)
        send statistics      
    end
    
    #####################################################
    # Executed when the strategy receives a trade event #
    #####################################################
    def on_trade(trade)
        send trade      
    end
    
    ###########################################################
    # Executed when the strategy receives an execution report #
    ###########################################################
    def on_execution_report(executionReport)
        send executionReport      
    end
    
    ############################################################
    # Executed when the strategy receives data of a type other #
    #  than the other callbacks                                #
    ############################################################
    def on_other(data)
        send data      
    end

    ####################################################
    # Executed when the strategy receives a dividend event #
    ####################################################
    def on_dividend(dividend)
        send dividend
    end

    ############################################################
    # Executed when the strategy receives a callback requested #
    #  via request_callback_at or request_callback_after       #
    ############################################################
    def on_callback(data)
        if !get_parameter("shouldCancelDataFlow").nil?
            if data.kind_of? DataFlowID
                cancel_data_flow data
                set_property "localDataFlowStopped", "true"
            end
        end
    end
    
    ##############################################################
    # Executed when the strategy receives an order cancel reject #
    #  event                                                     #
    ##############################################################
    def on_cancel_reject(reject)
        send reject      
    end

    #############################################################
    # Sets up the data flow as dictated by strategy parameters. #
    #############################################################
    def do_data_flow
        baseURNList = get_parameter "urns"
        requests = Array.new
        if !baseURNList.nil?
            urns = baseURNList.split(",")
            urns.each { |urn| requests << DataRequest.new(ModuleURN.new(urn.to_s)) }
            if !get_parameter("useStrategyURN").nil?
                if !get_parameter("routeToSink").nil?
                    requests << DataRequest.new(get_urn, OutputType::ALL)
                else
                    requests << DataRequest.new(get_urn)
                end
            end
        end
        return create_data_flow(!get_parameter("routeToSink").nil?,(baseURNList.nil? ? nil : requests.to_java(DataRequest))) 
    end
end
