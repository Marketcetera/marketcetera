require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'queries_controller'

# Re-raise errors caught by the controller.
class QueriesController; def rescue_action(e) raise e end; end

class QueriesControllerTest < MarketceteraTestBase
  fixtures :messages_log, :currencies, :accounts, :sub_account_types, :sub_accounts

  def setup
    @controller = QueriesController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "acct1", Date.civil(2006, 7, 11), "IFLI", "4.53", "ZAI")
    create_test_trade(200, 400, Side::QF_SIDE_CODE[:sell], "acct2", Date.civil(2006, 7, 12), "MIFLI", "4.53", "ZAI")
    create_test_trade(300, 400, Side::QF_SIDE_CODE[:buy], "acct3", Date.civil(2006, 7, 14), "IFLI", "4.53", "ZAI")
    create_test_trade(400, 400, Side::QF_SIDE_CODE[:sell], "acct2", Date.civil(2006, 8, 1), "DRIFLI", "4.53", "ZAI")
    create_test_trade(500, 400, Side::QF_SIDE_CODE[:sellShort], "acct1", Date.civil(2006, 9, 19), "IFLI", "4.53", "ZAI")
    @all_trades = Trade.find(:all)
  end

  def test_by_symbol_none_specified
    post :by_symbol
    
    assert_response :success
    assert_template 'queries_output'

    assert_not_nil assigns(:trades)
    assert_equal 5, assigns(:trades).length
    @all_trades.each_index { |i| assert_equal @all_trades[i], assigns(:trades)[i], 
                                        "trades at index "+i.to_s + " don't match" }
  end
  
  def test_by_symbol_specified
    post :by_symbol,{"m_symbol"=>{"root"=>"IFLI"} }
    
    assert_response :success
    assert_template 'queries_output'

    assert_not_nil assigns(:trades)
    assert_equal 3, assigns(:trades).length
    assert_equal @all_trades[0], assigns(:trades)[0]
    assert_equal @all_trades[2], assigns(:trades)[1]
    assert_equal @all_trades[4], assigns(:trades)[2]
    
    # verify IDs are passed through correctly: this can happen if paginate-joins query
    # is not setup to correctly preserve the ID column of right table
    assert_not_equal assigns(:trades)[0].id, assigns(:trades)[1].id    
  end
  
  def test_by_symbol_none_match
    post :by_symbol,{"m_symbol"=>{"root"=>"pupkin"} }
    
    assert_response :success
    assert_template 'queries_output'

    assert_not_nil assigns(:trades)
    assert_equal 0, assigns(:trades).length
  end  
  
  def test_search_by_account_none
      post :by_account
    
    assert_response :success
    assert_template 'queries_output'

    assert_not_nil assigns(:trades)
    assert_equal 5, assigns(:trades).length
    @all_trades.each_index { |i| assert_equal @all_trades[i], assigns(:trades)[i], 
                                        "trades at index "+i.to_s + " don't match" }
    assert_not_equal assigns(:trades)[0].id, assigns(:trades)[1].id    
  end
  
  def test_by_account_specified
    post :by_account, {"account"=>{"nickname"=>"acct2"} }
    
    assert_response :success
    assert_template 'queries_output'

    assert_not_nil assigns(:trades)
    assert_equal 2, assigns(:trades).length
    assert_equal @all_trades[1], assigns(:trades)[0]
    assert_equal @all_trades[3], assigns(:trades)[1]
    assert_not_equal assigns(:trades)[0].id, assigns(:trades)[1].id    
  end
  
  def test_by_account_none_match
    post :by_account, {"account"=>{"nickname"=>"zanachka"} }
    
    assert_response :success
    assert_template 'queries_output'
    
    assert_not_nil assigns(:trades)
    assert_equal 0, assigns(:trades).length
  end  
  
  # todo: fix when we switch to date validation checking
  def _test_by_date_no_params
    post :by_date
    
    assert_response :success
    assert_template 'by_date'
    
    assert_not_nil assigns(:trades)
    assert_equal 0, assigns(:trades).length
  end

  # todo: fix when we switch to date validation checking
  def _test_on_date_no_params
    post :on_date
    
    assert_response :success
    assert_template 'on_date'
    
    assert_not_nil assigns(:trades)
    assert_equal 0, assigns(:trades).length
  end
  
  
  def test_by_date_wide_range
    post :by_date, {"date"=>{ "from(1i)"=>"2006", "from(2i)"=>"7", "from(3i)"=>"1", 
                              "to(1i)"=>"2006", "to(2i)"=>"11", "to(3i)"=>"30" }}
    
    assert_response :success
    assert_template 'by_date'
    
    assert_not_nil assigns(:trades)
    assert_equal 5, assigns(:trades).length
    @all_trades.each_index { |i| assert_equal @all_trades[i], assigns(:trades)[i], 
                                        "trades at index "+i.to_s + " don't match" }
    assert_not_equal assigns(:trades)[0].id, assigns(:trades)[1].id    
  end
  
  #2006/7/12 - 2006/8/1 includes 1,2,3
  def test_by_date_partial_range
    post :by_date, {"date"=>{ "from(1i)"=>"2006", "from(2i)"=>"7", "from(3i)"=>"12", 
                              "to(1i)"=>"2006", "to(2i)"=>"8", "to(3i)"=>"1" }}
    
    assert_response :success
    assert_template 'by_date'
    
    assert_not_nil assigns(:trades)
    assert_equal 3, assigns(:trades).length
    assert_equal @all_trades[1], assigns(:trades)[0]
    assert_equal @all_trades[2], assigns(:trades)[1]
    assert_equal @all_trades[3], assigns(:trades)[2]
    assert_not_equal assigns(:trades)[0].id, assigns(:trades)[1].id    
  end
  
  def test_by_date_outside_of_range
    post :by_date, {"date"=>{ "from(1i)"=>"2007", "from(2i)"=>"7", "from(3i)"=>"1", 
                              "to(1i)"=>"2008", "to(2i)"=>"11", "to(3i)"=>"30" }}
    
    assert_response :success
    assert_template 'by_date'
    
    assert_not_nil assigns(:trades)
    assert_equal 0, assigns(:trades).length
  end
  
  def test_on_date_has_trade
    create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "acct1", Date.civil(2008, 7, 11), "IFLI", "4.53", "ZAI")
    post :on_date, {"date"=> { "on(1i)"=>"2008", "on(2i)"=>"7", "on(3i)"=>"11" }}
    
    assert_response :success
    assert_template 'on_date'
    
    assert_not_nil assigns(:trades)
    assert_equal 1, assigns(:trades).length
    assert_equal(Date.civil(2008, 7, 11), assigns(:on_date))
  end
  
end
