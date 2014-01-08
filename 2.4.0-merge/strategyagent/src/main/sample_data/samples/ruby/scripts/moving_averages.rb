#
# $License$
#
# author:anshul@marketcetera.com
# since 1.5.0
# version: $Id$
#
#   
require 'java'
java_import org.marketcetera.strategy.ruby.Strategy

#######################################
# Strategy that prints a message      #
# whenever a symbol's moving average  #
# crosses its closing price.       e  #
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
        warn "Close #{close} crossed below average #{avg} on #{date}"
      elsif close > avg && (!@wasAbove)
        warn "Close #{close} crossed above average #{avg} on #{date}"
        @wasAbove = true;
      end
    end
end
