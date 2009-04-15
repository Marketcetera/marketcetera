include_class "org.marketcetera.strategy.ruby.Strategy"

###############################
# Sample strategy template    #
###############################
class __TEMPLATE_CLASS_NAME__ < Strategy
  
    ##########################################
    # Executed when the strategy is started. #
    #                                        #
    # Use this method to set up data flows   #
    #  and other initialization tasks.       #
    ##########################################
    def on_start
      
    end
    
    ############################################
    # Executed when the strategy is stopped.   #
    ############################################
    def on_stop
      
    end
    
    ####################################################
    # Executed when the strategy receives an ask event #
    ####################################################
    def on_ask(ask)
      
    end
    
    ###################################################
    # Executed when the strategy receives a bid event #
    ###################################################
    def on_bid(bid)
      
    end
    
    ##########################################################
    # Executed when the strategy receives a statistics event #
    ##########################################################
    def on_marketstat(statistics)
      
    end
    
    #####################################################
    # Executed when the strategy receives a trade event #
    #####################################################
    def on_trade(trade)
      
    end
    
    ###########################################################
    # Executed when the strategy receives an execution report #
    ###########################################################
    def on_execution_report(executionReport)
      
    end
    
    ############################################################
    # Executed when the strategy receives data of a type other #
    #  than the other callbacks                                #
    ############################################################
    def on_other(data)
      
    end

    ############################################################
    # Executed when the strategy receives a callback requested #
    #  via request_callback_at or request_callback_after       #
    ############################################################
    def on_callback(data)
      
    end
    
    ##############################################################
    # Executed when the strategy receives an order cancel reject #
    #  event                                                     #
    ##############################################################
    def on_cancel_reject(reject)
      
    end
end
