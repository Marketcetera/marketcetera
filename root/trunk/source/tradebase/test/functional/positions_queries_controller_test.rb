require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'positions_queries_controller'

# Re-raise errors caught by the controller.
class PositionsQueriesController; def rescue_action(e) raise e end; end

class PositionsQueriesControllerTest < MarketceteraTestBase
  fixtures :messages_log, :currencies, :accounts, :sub_account_types, :sub_accounts

  def setup
    @controller = PositionsQueriesController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end

  # essentially, the same as 'list' if no date is specified
  def test_positions_as_of_specified
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(400, 400, Side::QF_SIDE_CODE[:sell], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    get :positions_as_of, { "position"=>{"as_of(1i)"=>"2007", "as_of(2i)"=>"10", "as_of(3i)"=>"30"}}

    assert_response :success
    assert_template 'positions_search_output'

    assert_not_nil assigns(:positions)
    assert_equal 1, assigns(:positions).length
    assert_equal 1, assigns(:num_positions)
    assert_nums_equal -300, assigns(:positions)[0].position
  end

  # essentially, specify date in future
  def test_positions_as_of_future
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(400, 400, Side::QF_SIDE_CODE[:sell], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    get :positions_as_of, { "position"=>{"as_of(1i)"=>"2006", "as_of(2i)"=>"10", "as_of(3i)"=>"30"}}
    assert_response :success
    assert_template 'positions_search_output'

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
    assert_template 'positions_search_output'

    assert_not_nil assigns(:positions)
    assert_equal 0, assigns(:positions).length
  end
  
  def test_positions_by_account_none_specified
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(400, 400, Side::QF_SIDE_CODE[:sell], "acct2", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    get :positions_by_account, { "account"=>{ "nickname" => ""}}
    assert_response :success
    assert_template 'positions_search_output'

    assert_not_nil assigns(:positions)
    assert_equal 2, assigns(:positions).length

    # now w/out specifying arg
    get :positions_by_account
    assert_response :success
    assert_template 'positions_search_output'

    assert_not_nil assigns(:positions)
    assert_equal 2, assigns(:positions).length
  end
  
  def test_positions_by_account
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(400, 400, Side::QF_SIDE_CODE[:sell], "acct2", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    get :positions_by_account, { "account"=>{ "nickname" => "acct2"}}
    assert_response :success
    assert_template 'positions_search_output'

    assert_not_nil assigns(:positions)
    assert_equal 1, assigns(:positions).length
  end
  
  # use the param_name instead of nested accounts[nickname]
  def test_positions_by_account_use_param_name
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(400, 400, Side::QF_SIDE_CODE[:sell], "acct2", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    get :positions_by_account, { "nickname"=> "acct2"}
    assert_response :success
    assert_template 'positions_search_output'

    assert_not_nil assigns(:positions)
    assert_equal 1, assigns(:positions).length
  end
  
  def test_positions_by_account_substring
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(400, 400, Side::QF_SIDE_CODE[:sell], "acct2", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(400, 400, Side::QF_SIDE_CODE[:sell], "acct2bob", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(400, 400, Side::QF_SIDE_CODE[:buy], "bobacct2asdf", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    get :positions_by_account, { "account"=>{ "nickname" => "acct2"}}
    assert_response :success
    assert_template 'positions_search_output'

    assert_not_nil assigns(:positions)
    assert_equal 3, assigns(:positions).length
  end
  
end
