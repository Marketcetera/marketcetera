# Methods added to this helper will be available to all templates in the application.
module ApplicationHelper

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

  # displays the passed in value if it's non-zero
  def display_non_zero_value(value)
    if(value == 0) 
      return ""
    end
    value
  end
  
  # Designed to create a date from something that may look like this:
  # params={"trade"=>{"journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10","journal_post_date(3i)"=>"11"}}
  # Basically, we have a params[:trade][:journal_post_date(xi)] series of values
  def parse_date_from_params(params, object_name, tag_name)
    if(params[object_name].blank?) 
      return nil
    end
    
    return Date.new(Integer(params[object_name][tag_name+"(1i)"]), 
                      Integer(params[object_name][tag_name+"(2i)"]), 
                      Integer(params[object_name][tag_name+"(3i)"]))
  end
  
  # Take 2 params: either the nested hash or a flat varname stringified date and return whichever is setup, 
  # giving the nested one preference
  # Leverages the above function, but is useful for when you need to specify the date as a param 
  # in the pagination_links function and it currently doesn't accept two-named nested hash
  def get_date_from_params(params, nested_parent, nested_child, secondary)
    parent = params[nested_parent]
    if(!parent.nil?)
        return parse_date_from_params(params, nested_parent, nested_child)
    end
    secondaryStr = params[secondary]
    return (secondaryStr.blank?) ? nil : Date.parse(secondaryStr)
  end  
  
  
  # Displays all the table columns except for the created_on/updated_on columns
  # ex:   <%= show_relevant_table_columns(Currency.new, true) %>
  def show_relevant_table_columns(inObject, isHeader)
    outStr = ""
    for column in inObject.class.content_columns
      if(column.name != "created_on" && column.name != "updated_on")
          if(isHeader) 
            outStr += "<th>" + column.human_name+"</th>"
          else outStr += "<td>" + inObject.send(column.name).to_s + "</td>"
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
      if(!child.nil? && !child.empty?)
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
  # for example, for maxLen 10, does 4..4
  # for example:
  # contract_string("zaporozhets", 10) ==> zapo..hets
  # NOTE: for odd length, it's actually possible that we'll get an outgoing string that's longer than 
  # the incoming. oh well.
  def contract_string(theString, maxLen)
    if(theString.blank?) 
      return theString
    end
    
    if(theString.length > maxLen)
      halfLen = (maxLen / 2.0).ceil - 1 
      theString = theString[0, halfLen]+".."+theString[-halfLen, halfLen]
    end
    
    theString
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
