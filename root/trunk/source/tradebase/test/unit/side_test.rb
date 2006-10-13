require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

# tests the Side helper class
class SideTest < MarketceteraTestBase
  
  def test_get_human_side
    assert_equal "SELL SHORT", Side.get_human_side(Quickfix::Side_SELL_SHORT())
  end
end