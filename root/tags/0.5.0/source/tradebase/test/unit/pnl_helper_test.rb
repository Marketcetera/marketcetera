require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class PnlHelperTest < MarketceteraTestBase
  include PnlHelper, ApplicationHelper
  
  def test_pnl_number_class
    assert_equal ApplicationHelper::RJUST_NUMBER_CLASS_NEG_STR, pnl_number_class(-22)
    assert_equal ApplicationHelper::RJUST_NUMBER_CLASS_STR, pnl_number_class(0)
    assert_equal ApplicationHelper::RJUST_NUMBER_CLASS_STR, pnl_number_class(22)
  end
end
