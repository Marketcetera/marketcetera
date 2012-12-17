include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "org.marketcetera.trade.OptionType"
include_class "java.lang.Long"
include_class "java.lang.String"
include_class "java.math.BigDecimal"
include_class "java.util.Date"

########################################
# Tests Strategy API positions methods #
########################################
class Positions < Strategy
  
    ##########################################
    # Executed when the strategy is started. #
    #                                        #
    # Use this method to set up data flows   #
    #  and other initialization tasks.       #
    ##########################################
    def on_start
        do_get_position_as_of true
        do_get_all_positions_as_of true
        do_get_option_position_as_of true
        do_get_all_option_positions_as_of true
        do_get_option_positions_as_of true
        do_get_underlying true
        do_get_option_roots true
    end
    
    ############################################
    # Executed when the strategy is stopped.   #
    ############################################
    def on_stop
        do_get_position_as_of false
        do_get_all_positions_as_of false
        do_get_option_position_as_of false
        do_get_all_option_positions_as_of false
        do_get_option_positions_as_of false
        do_get_underlying false
        do_get_option_roots false
    end
    ###############################
    # Executes get_position_as_of #
    ###############################
    def do_get_position_as_of(duringStart)
      symbol = get_property "symbol"
      dateString = get_property "date"
      if dateString.nil?
          date = nil
      else
          date = Date.new Long.parseLong dateString
      end
      result = get_position_as_of date, symbol
      if result.nil?
          resultString = nil
      else
          resultString = result.to_s
      end
      if duringStart
          set_property "positionAsOf", resultString
      else
          set_property "positionAsOfDuringStop", resultString
      end
    end
    ####################################
    # Executes get_all_positions_as_of #
    ####################################
    def do_get_all_positions_as_of(duringStart)
        dateString = get_property "date"
        if dateString.nil?
            date = nil
        else
            date = Date.new Long.parseLong dateString
        end
        result = get_all_positions_as_of date
        if result.nil?
            resultString = nil
        else
            resultString = result.to_s
        end
        if duringStart
            set_property "allPositionsAsOf", resultString
        else
            set_property "allPositionsAsOfDuringStop", resultString
        end
    end
    ######################################
    # Executes get_option_position_as_of #
    ######################################
    def do_get_option_position_as_of(duringStart)
        optionRoot = get_property "optionRoot"
        expiry = get_property "expiry"
        strikePriceString = get_property "strikePrice"
        strikePrice = nil
        if !strikePriceString.nil?
            strikePrice = BigDecimal.new strikePriceString
        end
        optionTypeString = get_property "optionType"
        optionType = nil
        if !optionTypeString.nil?
            optionType = OptionType.valueOf optionTypeString
        end
        dateString = get_property "date"
        date = nil
        if !dateString.nil?
            date = Date.new Long.parseLong dateString
        end
        result = get_option_position_as_of date,optionRoot,expiry,strikePrice,optionType
        if result.nil?
            resultString = nil
        else
            resultString = result.to_s
        end
        if duringStart
            set_property "optionPositionAsOf",resultString
        else
            set_property "optionPositionAsOfDuringStop",resultString
        end
    end
    ###########################################
    # Executes get_all_option_positions_as_of #
    ###########################################
    def do_get_all_option_positions_as_of(duringStart)
        dateString = get_property "date"
        date = nil
        if !dateString.nil?
            date = Date.new Long.parseLong dateString
        end
        result = get_all_option_positions_as_of date
        if result.nil?
            resultString = nil
        else
            resultString = result.to_s
        end
        if duringStart
            set_property "allOptionPositionsAsOf",resultString
        else
            set_property "allOptionPositionsAsOfDuringStop",resultString
        end
    end
    #######################################
    # Executes get_option_positions_as_of #
    #######################################
    def do_get_option_positions_as_of(duringStart)
        optionRootsString = get_property "optionRoots"
        optionRoots = nil
        if !optionRootsString.nil? && !optionRootsString.empty?
            optionRoots = optionRootsString.split ","
        end
        dateString = get_property "date"
        date = nil
        if !dateString.nil?
            date = Date.new Long.parseLong dateString
        end
        if !get_parameter("nullOptionRoot").nil?
            result = get_option_positions_as_of date, (Array.new << optionRootsString << nil).to_java(String)
        else
            if !get_parameter("emptyOptionRoot").nil?
                result = get_option_positions_as_of date, (Array.new << optionRootsString << "").to_java(String)
            else
                result = get_option_positions_as_of date, (optionRoots.nil? ? nil : optionRoots.to_java(String))
            end
        end
        if result.nil?
            resultString = nil
        else
           resultString = result.to_s
        end
        if duringStart
            set_property "optionPositionsAsOf",resultString
        else
            set_property "optionPositionsAsOfDuringStop",resultString
        end
    end
    ###########################
    # Executes get_underlying #
    ###########################
    def do_get_underlying(duringStart)
        optionRoot = get_property "optionRoot"
        result = get_underlying optionRoot
        if result.nil?
            resultString = nil
        else
           resultString = result.to_s
        end
        if duringStart
            set_property "underlying",resultString
        else
            set_property "underlyingDuringStop",resultString
        end
    end
    #############################
    # Executes get_option_roots #
    #############################
    def do_get_option_roots(duringStart)
        underlyingSymbol = get_property "underlyingSymbol"
        result = get_option_roots underlyingSymbol
        if result.nil?
            resultString = nil
        else
            resultString = result.to_s
        end
        if duringStart
            set_property "optionRoots",resultString
        else
            set_property "optionRootsDuringStop",resultString
        end
    end
end
