include_class "org.marketcetera.strategy.ruby.Strategy"
require "other1"

###############################
# Sample strategy template    #
###############################
class Require < Strategy
    def initialize
      super
      @startToken = nil
      @stopToken = nil
    end
    ##########################################
    # Executed when the strategy is started. #
    #                                        #
    # Use this method to set up data flows   #
    #  and other initialization tasks.       #
    ##########################################
    def on_start
      set_property "onStart", Other1.action1
    end
    
    ############################################
    # Executed when the strategy is stopped.   #
    ############################################
    def on_stop
      set_property "onStop", Other1.new.action2
    end
end
