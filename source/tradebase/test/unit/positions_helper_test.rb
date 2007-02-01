require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class PositionsHelperTest < MarketceteraTestBase
  fixtures :currencies
  include PositionsHelper

  def test_get_top_positioned_accounts
    create_trades_in_account(3, "TOLI")
    create_trades_in_account(5, "RAMA")
    create_trades_in_account(2, "LENA")
    create_trades_in_account(3, "MOLI")

    accounts = get_top_positioned_accounts(3)
    assert_equal 3, accounts.length
    assert_equal ["RAMA", 5], [accounts[0][0].nickname, accounts[0][1]]
    assert_equal ["TOLI", 3], [accounts[1][0].nickname, accounts[1][1]]
    assert_equal ["MOLI", 3], [accounts[2][0].nickname, accounts[2][1]]
  end
  
  private
  def create_trades_in_account(num_trades, account)
    for i in 1..num_trades
      create_test_trade(100, 20.11, Side::QF_SIDE_CODE[:buy], account, Date.today, "IFLI-"+i.to_s, 4.99, "USD")
    end
  end  
end
