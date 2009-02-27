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
    get :positions_as_of, { "date"=>{"on(1i)"=>"2007", "on(2i)"=>"10", "on(3i)"=>"30"}}

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
    get :positions_as_of, { "date"=>{"on(1i)"=>"2006", "on(2i)"=>"10", "on(3i)"=>"30"}}
    assert_response :success
    assert_template 'positions_search_output'

    assert_not_nil assigns(:positions)
    assert_equal 1, assigns(:positions).length
    assert_nums_equal -300, assigns(:positions)[0].position
  end

  def test_positions_as_of_invalid_date
    get :positions_as_of, { "position"=>{"as_of(1i)"=>"2006", "as_of(2i)"=>"10", "as_of(3i)"=>"33"}}
    assert_response :success
    assert_template 'positions_queries'
    assert_has_error_box
    assert_not_nil assigns(:report).errors
    assert_not_nil assigns(:report).errors[:on_date]
  end

  # make sure the date is not inclusive
  def test_positions_as_of_inclusivity
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(400, 400, Side::QF_SIDE_CODE[:sell], "pos-acct", Date.civil(2006, 7, 13), "MIFLI", "4.53", "ZAI")
    create_test_trade(400, 400, Side::QF_SIDE_CODE[:sell], "pos-acct", Date.civil(2006, 7, 16), "BIFLI", "4.53", "ZAI")
    get :positions_as_of, { "date"=>{"on(1i)"=>"2006", "on(2i)"=>"7", "on(3i)"=>"16"}}
    assert_response :success
    assert_template 'positions_search_output'

    assert_not_nil assigns(:positions)
    assert_equal 2, assigns(:positions).length
  end

  # essentially, specify date in past ie outside of range
  def test_positions_as_date_in_past
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(400, 400, Side::QF_SIDE_CODE[:sell], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    get :positions_as_of, { "date"=>{"on(1i)"=>"2005", "on(2i)"=>"10", "on(3i)"=>"30"}}
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

  # mixed positions - both forex and equity
  def test_positions_forex_present
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(100, 1.23, Side::QF_SIDE_CODE[:buy], "acct1", Date.civil(2006, 7, 11),
                      "ZAI/USD", "4.53", "ZAI", TradesHelper::SecurityTypeForex)

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

    # now verity that the links are coming back correctly - we should have links to searches by symbola and account
    pos_acct = Account.find_by_nickname("pos-acct")
    acct1 = Account.find_by_nickname("acct1")
    # verify search by symbol link:  <td><a href="/queries/trade_search?m_symbol_root=ZAI%2FUSD&amp;all_dates=yes">ZAI/USD</a></td>
    assert_tag :tag => 'td', :child => { :tag => 'a', :content => "ZAI/USD",
                                            :attributes => {:href => /queries\/trade_search\?.*m_symbol_root=ZAI%2FUSD/} }
    assert_tag :tag => 'td', :child => { :tag => 'a', :content => "IFLI",
                                            :attributes => {:href => /queries\/trade_search\?.*m_symbol_root=IFLI/} }
    # and same for 'see all trades'
    assert_tag :tag => 'td', :child => { :tag => 'a', :content => "See all trades",
                                            :attributes => {:href => /queries\/trade_search\?.*m_symbol_root=ZAI%2FUSD/} }
    assert_tag :tag => 'td', :child => { :tag => 'a', :content => "See all trades",
                                            :attributes => {:href => /queries\/trade_search\?.*m_symbol_root=IFLI/} }
    # verify the all-dates is there too
    assert_tag :tag => 'td', :child => { :tag => 'a', :content => "ZAI/USD",
                                                :attributes => {:href => /queries\/trade_search\?.*all_dates=yes/} }

    # Verify search by account
    assert_tag :tag => 'td', :child => { :tag => 'a', :content => "pos-acct",
                                            :attributes => {:href => /accounts\/show\/#{pos_acct.id}/} }
    assert_tag :tag => 'td', :child => { :tag => 'a', :content => "acct1",
                                            :attributes => {:href => /accounts\/show\/#{acct1.id}/} }
  end

  def test_positions_forex_only
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "pos-acct", Date.civil(2006, 7, 11), "USD/ZAI", "4.53", "ZAI",
                      TradesHelper::SecurityTypeForex)
    create_test_trade(100, 1.23, Side::QF_SIDE_CODE[:buy], "acct1", Date.civil(2006, 7, 11),
                      "ZAI/USD", "4.53", "ZAI", TradesHelper::SecurityTypeForex)

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
