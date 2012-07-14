# Helper dedicated entirely to making sure that numbers are formatted correctly
# Currently, none of the browsers implement CSS text-align on a character, so 
# in order to make sure numbers aling on a decimal point we have to do it manually.
# Here are the thoughts on how to display in List views (Show should display everything)
# 1. Right-align all numbers
#   Quantity: if it's a while integer, don't display decimal places
#             otherwise, display however many there are
#   Prices: always display 2 decimal places, possibly appending .00 if needed
#           round the number of decimal places if needed
module NumberFormatHelper

  # the approach is as follows:
  # Take the number and multiply it by 10^decimalPlaces
  # Round it
  # divide it by same number
  # extract the integer and decimal parts
  # concact again with decimal point in between, adding 0s if necessary to the end
  # This should all disappear once we have Ruby 1.8.5 and can use the BigDecimal.limit() function
  # Usage: 
  # format_number(BigDecimal.new("23.456"), 2)
  def format_number(number, decimalPlaces, stripDecimalsForIntegers = false)
    if(number.blank?)
      return number
    end
  
    if(number.instance_of?(String))
      number = BigDecimal.new(number)
    end
    if(number.instance_of?(Fixnum) || number.instance_of?(Float))
      number = BigDecimal.new(number.to_s)
    end
    
    if(stripDecimalsForIntegers && (number.frac == 0))
      return number.to_i.to_s
    end
  
    scale = 10**decimalPlaces
    scaled = (number * scale).round()/scale
    integerPart = scaled.fix.to_i.to_s
    decimalPart = (scaled.abs.frac * scale).to_i.to_s
    while decimalPart.length < decimalPlaces
      decimalPart << "0"
    end 

    return integerPart << "." << decimalPart
  end
 
  # shorter-named alias for above function
  def fn(number, decimalPlaces, stripDecimalsForIntegers = false)
    format_number(number, decimalPlaces, stripDecimalsForIntegers)
  end
  
    # Helper function for making sure that if an empty string comes in to display,
  # we alwyas return an &nbsp otherwise in HTML empty divs will end up collapsing
  # and we will be misaligned.
  # see ticket:74 for more info
  def df(str)
    if(str.blank?)
       return "&nbsp;"
    end
    h(str)
  end
  
    # displays the passed in value if it's non-zero
  def display_non_zero_value(value)
    if(value == 0) 
      return ""
    end
    value
  end
end