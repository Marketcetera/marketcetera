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

  def test_contract_string
    assert_nil contract_string(nil, 5)
    assert_equal "", contract_string("", 5)
    assert_equal "tolik", contract_string("tolik", 10)
    assert_equal "tolik", contract_string("tolik", 5)
    assert_equal "t..k", contract_string("tolik", 4)
    assert_equal "al..ba", contract_string("alibaba", 5)
    assert_equal "zapo..hets", contract_string("zaporozhets", 10)  
  end
  
  def test_display_non_zero_value
    assert_equal("", display_non_zero_value(0))
    assert_equal("", display_non_zero_value(0.0))
    assert_equal("bob", display_non_zero_value("bob"))
    assert_equal(23.45, display_non_zero_value(23.45))
  end
end