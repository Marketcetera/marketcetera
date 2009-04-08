#
# author:anshul@marketcetera.com
# since $Release$
# version: $Id$
#
#   
include_class "org.marketcetera.strategy.ruby.Strategy"

#######################################
# Strategy that receives marketdata   #
#######################################
class MovingAverages < Strategy

    ######################################################
    # Executed when the strategy receives any other event#
    ######################################################
    def on_other(event)
      avg = event["average"]
      close = event["close"]
      date = event["date"]
      if close < avg && @wasAbove
        @wasAbove = false
        puts "Close #{close} crossed below average #{avg} on #{date}"
      elsif close > avg && (!@wasAbove)
        puts "Close #{close} crossed above average #{avg} on #{date}"
        @wasAbove = true;
      end
    end
end
