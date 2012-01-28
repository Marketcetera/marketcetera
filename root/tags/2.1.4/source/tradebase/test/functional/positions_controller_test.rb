require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'positions_controller'

# Re-raise errors caught by the controller.
class PositionsController; def rescue_action(e) raise e end; end

class PositionsControllerTest < MarketceteraTestBase
  fixtures :currencies, :accounts, :sub_accounts, :sub_account_types

  def setup
    @controller = PositionsController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end

  # Use the pre-canned trade, should see 1 position
  def test_one_position
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(400, 400, Side::QF_SIDE_CODE[:sell], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    get :list

    assert_response :success
    assert_template 'list'

    assert_not_nil assigns(:positions)
    assert_equal 1, assigns(:positions).length
    assert_nums_equal -300, assigns(:positions)[0].position
    
    # verify the visible position number doesn't have many trailing zeros
    assert_select "table tr td", "-300"
  end


  def test_num_positions
    Array.new(15) { |n| 
      create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "IFLI_"+n.to_s, "4.53", "ZAI")
    }
  
    get :list
    
    assert_equal 15, assigns(:num_positions)
    assert_has_show_edit_delete_links(false, false, false)
  end

  def test_list_positions_inclusivity
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 13), "MIFLI", "4.53", "ZAI")
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.today, "BIFLI", "4.53", "ZAI")

    get :list
    # should get today's position'
    assert_equal 3, assigns(:num_positions)
  end

  def test_zero_position
    create_test_trade(130, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(130, 400, Side::QF_SIDE_CODE[:sell], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")

    get :list
    assert_response :success
    assert_template 'list'

    assert_not_nil assigns(:positions)
    assert_equal 0, assigns(:positions).length
    assert_equal 0, assigns(:num_positions)
  end

end
