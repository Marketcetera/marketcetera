require File.dirname(__FILE__) + '/../test_helper'
require 'bigdecimal'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class ApplicationHelperTest < MarketceteraTestBase
  include ApplicationHelper
  
  def test_get_date_from_params
    assert_equal Date.civil(2006, 7, 11), 
      get_date_from_params({"position"=>{"as_of(1i)"=>"2006", "as_of(2i)"=>"7", "as_of(3i)"=>"11"}}, 
                            "position", "as_of", "as_of_date")
    assert_equal Date.civil(2006, 7, 11).to_s, 
                 get_date_from_params({ "as_of_date"=>"2006-7-11"}, "position", "as_of", "as_of_date").to_s
      
    assert_nil get_date_from_params({}, "position", "as_of", "as_of_date")
    assert_nil get_date_from_params({"position" => {} }, "position", "as_of", "as_of_date")
  end


end