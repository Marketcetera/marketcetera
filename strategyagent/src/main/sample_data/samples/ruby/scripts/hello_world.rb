#
# $License$
#
# author:anshul@marketcetera.com
# since 1.0.0
# version: $Id: hello_world.rb 16633 2013-07-17 01:26:26Z colin $
#
#
require 'java'
java_import org.marketcetera.strategy.ruby.Strategy

###############################
# Hello World Strategy        #
###############################
class HelloWorld < Strategy

    ##########################################
    # Executed when the strategy is started. #
    #                                        #
    # Use this method to set up data flows   #
    #  and other initialization tasks.       #
    ##########################################
    def on_start
      warn "Hello World!"
    end

    ############################################
    # Executed when the strategy is stopped.   #
    ############################################
    def on_stop
      warn "Good Bye!"
    end
end
