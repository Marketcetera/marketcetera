# Methods added to this helper will be available to all templates in the application.
require 'tzinfo'

arb = ActiveRecord::Base
def arb.sanitize_sql_accessor(*args)
  sanitize_sql(*args)
end

module ApplicationHelper
  include NumberFormatHelper
  
  RJUST_NUMBER_CLASS_STR = " class='number'"
  RJUST_NUMBER_CLASS_NEG_STR = " class='negative'"
  LOCAL_TZ = TZInfo::Timezone.get(TZ_ID)


  def auto_complete_for_currency_alpha_code
    auto_complete_responder_for_currency_alpha_code params[:currency][:alpha_code]
  end
  
  def get_debit_or_credit(cash_flow, accounting_type)
    case (accounting_type) 
    when 'A'
      return cash_flow < 0 ? :credit : :debit
    when 'L'
      return cash_flow < 0 ? :debit : :credit 
    when 'R'
      return cash_flow < 0 ? :debit : :credit
    when 'E'
      return cash_flow < 0 ? :credit : :debit
    else
      return nil
    end
  
  end

  # Displays all the table columns except for the created_on/updated_on columns
  # ex:   <%= show_relevant_table_columns(Currency.new, true) %>
  def show_relevant_table_columns(inObject, isHeader)
    outStr = ""
    for column in inObject.class.content_columns
      if(column.name != "created_on" && column.name != "updated_on")
          if(isHeader) 
            outStr += "<th>" + column.human_name+"</th>"
          else 
            if(column.number? || column.instance_of?(BigDecimal)) 
              tdStr = "<td"+RJUST_NUMBER_CLASS_STR+">"
              value = fn(inObject.send(column.name), 2)
            else 
              tdStr = "<td>"
              value = inObject.send(column.name).to_s
            end
            outStr += tdStr + value + "</td>"
          end
      end
    end
    return outStr
  end
  
  # Returns the first string if it's non-empty, otherwise returns the 2nd string (which can be empty/nil)
  # This is a workaround for rails not handling nested hashes in URL links yet
  # The params can contain either a nested parent[nested child] hash, or it may have a 
  # secondary (hack) variable that has a "flat" name but points to same information.
  # This is for helping with pagination.
  def get_non_empty_string_from_two(params, nested_parent, nested_child, secondary)
    parent = params[nested_parent]
    if(!parent.nil?)
      child = parent[nested_child]
      if(!child.blank?)
        return child
      end
    end
    return params[secondary]
  end
  
  # Collects all the errors (in AR object) into 1 string for printing
  def collect_errors_into_string(errors)
    (errors.collect { |n, v| n.to_s + ": " + v.to_s + "\n"}).to_s
  end
  
  # If a string is longer than (2x+1), contracts it by grabbing the first and last X chars
  # for example, for maxLen 10, does 4...4
  # for example:
  # contract_string("zaporozhets", 10) ==> zapo...hets
  # NOTE: for odd length, it's actually possible that we'll get an outgoing string that's longer than 
  # the incoming. oh well.
  def contract_string(theString, maxLen)
    if(theString.blank?) 
      return theString
    end
    
    if(theString.length > maxLen)
      halfLen = (maxLen/2.0).ceil - 1 
      theString = theString[0, halfLen]+"..."+theString[-halfLen, halfLen]
    end
    
    theString
  end
  
  # similar to contract_string, but just chops off the end and leaves the ellipsis
  TRADE_COMMENT_LENGTH = 25
  ACCOUNT_LENGTH = 12
  def shorten_string(theString, maxLen = TRADE_COMMENT_LENGTH)
    if(theString.blank?)
      return theString
    end
    
    if(theString.length > (maxLen-3))
      # remember the off-by-one since we start at 0
      return theString[0..(maxLen-3-1)]+"..." 
    end
    theString
  end
  
  # A wrapper around the regular error_messages_for, except for it substitutes the error 
  # messages to say "prohibeted from ... being executed" instead of '... from being saved"
  def error_messages_for_report(*params)
    (error_messages_for(params)).gsub("being saved", "being executed")
  end
  
  # Replaces spaces with &nbsp; - to be used for Account and Symbol names
  # this effectively makes spaces "hard" so they won't wrap.
  # Keep in mind that we need to call h() on the incoming string before this function is called 
  def make_spaces_hard(str)
    if(str.blank?) 
      return str 
    end
    str.gsub(" ", "&nbsp;")
  end

  # Helps determine if a checkbox is checked
  # Lifted from http://snippets.dzone.com/posts/show/2559
  def checked?( *args )
  if args.length == 3
    object, method, value = args
    if params[object] && params[object][method] && params[object][method] == value
      'checked'
    end
  elsif args.length == 2
    name, value = args
    if params[name] && params[name] == value
      true
    end
  end
end

  private
  def auto_complete_responder_for_currency_alpha_code(value)
    @currencies = Currency.find(:all, 
      :conditions => [ 'LOWER(alpha_code) LIKE ? AND obsolete=\'N\'',
      '%' + value.downcase + '%'], 
      :order => 'alpha_code ASC',
      :limit => 10)
    render :partial => 'shared/currencies_auto_complete'
  end

end
