# this is a wrapper around a regular Date that validates it first
# Used in all the forms to make sure we search for regular dates
class VDate
  include Validateable
  
  attr_accessor :year, :month, :day
  
  def initialize(year, month=nil, day=nil)
    if(month.nil?)
      @single_str = year
      begin
        d = Date.parse(@single_str)
        @year, @month, @day = d.year, d.month, d.day
      rescue ArgumentError => ex
        # set to invalid date
        @year, @month, @day = 0, 0, 0
      end
    else
      @single_str = nil
      @year = year
      @month = month
      @day = day
    end
  end

  def validate
    begin
      Date.new(year, month, day) 
    rescue ArgumentError => ex
      errors.add(:date, get_error_msg())
    end
  end
  
  def as_date
    if(valid?) 
      Date.new(year,month,day) 
    else 
      raise Exception.new(get_error_msg())
    end
  end
  
  
  # Designed to create a date from something that may look like this:
  # params={"trade"=>{"journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10","journal_post_date(3i)"=>"11"}}
  # Basically, we have a params[:trade][:journal_post_date(xi)] series of values
  def VDate.parse_date_from_params(params, object_name, tag_name)
    if(params[object_name].blank? ||
       params[object_name][tag_name+"(1i)"].blank? ||
       params[object_name][tag_name+"(2i)"].blank? ||
       params[object_name][tag_name+"(3i)"].blank?) 
      return nil
    end

    return VDate.new(Integer(params[object_name][tag_name+"(1i)"]), 
                      Integer(params[object_name][tag_name+"(2i)"]), 
                      Integer(params[object_name][tag_name+"(3i)"]))
  end
  
  # Take 2 params: either the nested hash or a flat varname stringified date and return whichever is setup, 
  # giving the nested one preference
  # Leverages the above function, but is useful for when you need to specify the date as a param 
  # in the pagination_links function and it currently doesn't accept two-named nested hash
  def VDate.get_date_from_params(params, nested_parent, nested_child, secondary)
    parent = params[nested_parent]
    if(!parent.nil?)
        return parse_date_from_params(params, nested_parent, nested_child)
    end
    secondaryStr = params[secondary]
    return (secondaryStr.blank?) ? nil : VDate.new(secondaryStr, nil, nil)
  end  

  def to_s
    (!@single_str.blank?) ? @single_str : "#{year}-#{month}-#{day}"
  end

  private 
  def get_error_msg
    "Invalid date: " + to_s
  end
end   