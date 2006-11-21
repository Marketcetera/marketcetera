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
  end

  # essentially, the same as 'list' if no date is specified
  def test_positions_as_of_no_date_specified
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(400, 400, Side::QF_SIDE_CODE[:sell], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    get :positions_as_of

    assert_response :success
    assert_template 'list'

    assert_not_nil assigns(:positions)
    assert_equal 1, assigns(:positions).length
    assert_equal 1, assigns(:num_positions)
    assert_nums_equal -300, assigns(:positions)[0].position
  end

  # essentially, specify date in future
  def test_positions_as
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(400, 400, Side::QF_SIDE_CODE[:sell], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    get :positions_as_of, { "position"=>{"as_of(1i)"=>"2006", "as_of(2i)"=>"10", "as_of(3i)"=>"30"}}
    assert_response :success
    assert_template 'list'

    assert_not_nil assigns(:positions)
    assert_equal 1, assigns(:positions).length
    assert_nums_equal -300, assigns(:positions)[0].position
  end

  # essentially, specify date in past ie outside of range
  def test_positions_as_date_in_past
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(400, 400, Side::QF_SIDE_CODE[:sell], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    get :positions_as_of, { "position"=>{"as_of(1i)"=>"2005", "as_of(2i)"=>"10", "as_of(3i)"=>"30"}}
    assert_response :success
    assert_template 'list'

    assert_not_nil assigns(:positions)
    assert_equal 0, assigns(:positions).length
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
  
  def test_num_positions
    Array.new(15) { |n| 
      create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "IFLI_"+n.to_s, "4.53", "ZAI")
    }
  
    get :list
    
    assert_equal 15, assigns(:num_positions)
  end
  
end
