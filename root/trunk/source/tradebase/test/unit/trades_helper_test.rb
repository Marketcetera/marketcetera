require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class TradesHelperTest < MarketceteraTestBase
  fixtures :messages_log, :currencies
  include TradesHelper
  
  def test_get_num_trades_on_day
    assert_equal 0, number_trades_on_day(Date.today)
    
    create_test_trade(100, 20.11, Side::QF_SIDE_CODE[:buy], "TOLI", Date.today, "IFLI", 4.99, "USD")
    assert_equal 1, number_trades_on_day(Date.today)
    
    for i in 1..10
      create_test_trade(100, 20.11, Side::QF_SIDE_CODE[:buy], "TOLI", Date.civil(2006, 7,11), "IFLI-"+i.to_s, 4.99, "USD")
    end
    assert_equal 10, number_trades_on_day(Date.civil(2006, 7, 11))

    assert_equal 0, number_trades_on_day(Date.civil(2005, 7, 11))
  end

  # bug 477
  def test_get_num_trades_on_day_past_midnight
    assert_equal 0, number_trades_on_day(Date.today)

   create_test_trade(100, 20.11, Side::QF_SIDE_CODE[:buy], "TOLI", DateTime.now.to_s(:db), "IFLI", 4.99, "USD")
 
    assert_equal 1, number_trades_on_day(Date.today), "didn't show trade recorded after midnight today"
  end
end
