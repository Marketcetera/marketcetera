require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class ForexMarkTest < MarketceteraTestBase
  fixtures :currency_pairs, :marks

  def setup
  end 
  
  def test_a_mark
	  a_mark = ForexMark.find(:first)
    assert_not_nil a_mark.tradeable
    assert_equal "ZAI", a_mark.tradeable.first_currency.alpha_code
  end
  
  def test_new_mark
    a_mark = ForexMark.new()
    a_mark.mark_date = Date.civil(2007,1,1)
    tradeable = CurrencyPair.get_currency_pair("ZAIUSD")

    # We want to make sure that this works, even if there is no equity
    # with the same ID
    assert_nil Equity.find_by_id(tradeable.id)
    a_mark.tradeable = tradeable
    a_mark.mark_value = 123.4
    a_mark.mark_type = Mark::MarkTypeClose
    assert a_mark.save
    
  end
  
end
