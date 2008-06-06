require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'positions_controller'

# Re-raise errors caught by the controller.
class PositionsController; def rescue_action(e) raise e end; end

# Similar to the positions-controller test, but with many more complex trades
class ComplexPositionsTest < MarketceteraTestBase
  fixtures :currencies, :accounts, :sub_accounts, :sub_account_types

  def setup
    @controller = PositionsQueriesController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end

  # Use the pre-canned trade, should see 1 position
  def test_different_accounts
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 13), "IFLI", "4.53", "USD")
    create_test_trade(400, 400, Side::QF_SIDE_CODE[:sell], "acct-2", Date.civil(2006, 7, 14), "IFLI", "4.53", "ZAI")
    verify_position(2, 0, 200, "pos-acct", Date.civil(2006, 7, 30))
    verify_position(2, 1, -400, "acct-2", Date.civil(2006, 7, 30))
  end
  
  # setup 5 trades over period of days and verify position at each day
  def test_trades_over_date_ranges
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 13), "IFLI", "4.53", "USD")
    create_test_trade(400, 400, Side::QF_SIDE_CODE[:sell], "acct-2", Date.civil(2006, 7, 15), "IFLI", "4.53", "ZAI")
    
    verify_position(0, 0, 0, "pos-acct", Date.civil(2006, 7,11))
    verify_position(1, 0, 100, "pos-acct", Date.civil(2006, 7, 12))
    verify_position(1, 0, 200, "pos-acct", Date.civil(2006, 7, 14))
    verify_position(2, 0, 200, "pos-acct", Date.civil(2006, 7, 16))
    verify_position(2, 1, -400, "acct-2", Date.civil(2006, 7, 16))
  end
  
  def verify_position(numPos, posIndex, qty, account, date)
    get :positions_as_of, { "date"=>{"on(1i)"=>date.year, "on(2i)"=>date.month, "on(3i)"=>date.day}}

    assert_response :success
    assert_template 'positions_search_output'

    assert_not_nil assigns(:positions)
    assert_equal numPos, assigns(:positions).length
    if(numPos > 0)
      assert_nums_equal qty, assigns(:positions)[posIndex].position
      assert_equal account, assigns(:positions)[posIndex].account.nickname  
    end
  end
end
