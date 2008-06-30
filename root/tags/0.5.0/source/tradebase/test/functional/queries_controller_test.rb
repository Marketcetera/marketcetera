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

  # should catch MIFLI in acct2 on 2006-7-12
  def test_all_params
      get :trade_search, {:suffix => 'date', "date_date"=>{ "from(1i)"=>"2006", "from(2i)"=>"7", "from(3i)"=>"1",
                          "to(1i)"=>"2006", "to(2i)"=>"11", "to(3i)"=>"30" }, :account => {:nickname => "acct2"},
                          :m_symbol => {:root => "MIFLI"}}

      assert_response :success
      assert_template 'queries_output'

      assert_equal 1, assigns(:trades).length
      assert_equal "MIFLI", assigns(:trades)[0].tradeable_m_symbol_root
      assert_tag :tag => "h1", :content => "List Trades for MIFLI in account [acct2] from 2006-07-01 to 2006-11-30"
  end

  def test_by_symbol_none_specified
    get :trade_search, {:all_dates => "yes"}
    
    assert_response :success
    assert_template 'queries_output'

    assert_not_nil assigns(:trades)
    assert_equal 5, assigns(:trades).length
    # trades should be sorted in reverse by date
    @all_trades.each_index { |i| assert_equal @all_trades[4-i], assigns(:trades)[i],
                                        "trades at index "+i.to_s + " don't match" }
  end
  
  def test_by_symbol_specified
    get :trade_search,{"m_symbol"=>{"root"=>"IFLI"}, :all_dates => "yes" }
    
    assert_response :success
    assert_template 'queries_output'

    assert_not_nil assigns(:trades)
    assert_equal 3, assigns(:trades).length
    assert_equal @all_trades[4], assigns(:trades)[0]
    assert_equal @all_trades[2], assigns(:trades)[1]
    assert_equal @all_trades[0], assigns(:trades)[2]

    assert_tag :tag => "h1", :content => "List Trades for IFLI for all dates"
    # verify IDs are passed through correctly: this can happen if paginate-joins query
    # is not setup to correctly preserve the ID column of right table
    assert_not_equal assigns(:trades)[0].id, assigns(:trades)[1].id    

    # for pagination
    assert_equal "IFLI", assigns(:symbol_str)
  end

  # create a forex trade and make sure it comes up
  def test_by_symbol_forex
    currTrade = create_test_trade(100, 1.23, Side::QF_SIDE_CODE[:buy], "acct1", Date.civil(2006, 7, 11),
            "ZAI/USD", "4.53", "ZAI", TradesHelper::SecurityTypeForex)
    get :trade_search,{"m_symbol"=>{"root"=>"ZAI/USD"}, :all_dates => "yes" }

    assert_response :success
    assert_template 'queries_output'

    assert_not_nil assigns(:trades)
    assert_equal 1, assigns(:trades).length
    assert_equal currTrade, assigns(:trades)[0]

    assert_tag :tag => "h1", :content => "List Trades for ZAI/USD for all dates"

    # for pagination
    assert_equal "ZAI/USD", assigns(:symbol_str)
  end
  
  def test_by_symbol_none_match
    post :trade_search,{"m_symbol"=>{"root"=>"pupkin"}, :all_dates => "yes" }
    
    assert_response :success
    assert_template 'queries_output'

    assert_not_nil assigns(:trades)
    assert_equal 0, assigns(:trades).length
  end  
  
  def test_search_by_account_none
    get :trade_search, {:all_dates => "yes"}
    
    assert_response :success
    assert_template 'queries_output'

    assert_not_nil assigns(:trades)
    assert_equal 5, assigns(:trades).length
    @all_trades.each_index { |i| assert_equal @all_trades[4-i], assigns(:trades)[i],
                                        "trades at index "+i.to_s + " don't match" }
    assert_not_equal assigns(:trades)[0].id, assigns(:trades)[1].id    
  end
  
  def test_by_account_specified
    get :trade_search, {"account"=>{"nickname"=>"acct2"}, :all_dates => "yes" }
    
    assert_response :success
    assert_template 'queries_output'

    assert_not_nil assigns(:trades)
    assert_equal 2, assigns(:trades).length
    assert_equal @all_trades[3], assigns(:trades)[0]
    assert_equal @all_trades[1], assigns(:trades)[1]
    assert_not_equal assigns(:trades)[0].id, assigns(:trades)[1].id

    assert_tag :tag => "h1", :content => "List Trades in account [acct2] for all dates"

    # for pagination
    assert_equal "acct2", assigns(:nickname)
  end
  
  def test_by_account_none_match
    get :trade_search, {"account"=>{"nickname"=>"zanachka"}, :all_dates => "yes" }
    
    assert_response :success
    assert_template 'queries_output'
    
    assert_not_nil assigns(:trades)
    assert_equal 0, assigns(:trades).length
    assert_tag :tag => "h1", :content => "List Trades in account [zanachka] for all dates"
  end

  # should error out b/c dates aren't set and all_dates is not set either'
  def test_by_date_no_params_all_dates_not_specified
    get :trade_search
    
    assert_response :success
    assert_template 'index'
    assert_has_error_box
    assert_not_nil assigns(:report).errors
    assert_not_nil assigns(:report).errors[:from_date]
    assert_not_nil assigns(:report).errors[:to_date]
    assert_nil assigns(:trades)
  end

  # should go through with all_dates
  def test_by_date_no_params
    get :trade_search, {:all_dates => "yes"}

    assert_response :success
    assert_template 'queries_output'
    assert_equal 5, assigns(:trades).length
    assert_tag :tag => "h1", :content => "List Trades for all dates"
  end

  def test_on_date_today
    get :trade_search, {:from_date => Date.today.to_s, :to_date => Date.today.to_s}
    
    assert_response :success
    assert_template 'queries_output'
    assert_equal 0, assigns(:report).errors.length

    assert_not_nil assigns(:trades)
    assert_equal 0, assigns(:trades).length
    assert_tag :tag => "h1", :content => "List Trades from #{Date.today.to_s} to #{Date.today.to_s}"
  end

  # test the case of bug #477 - need to have a trade have a timestamp past midnight on today
  def test_on_date_today_past_midnight
    t1 = create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "acct1", DateTime.now.to_s(:db), "bob", "4.53", "ZAI")
    get :trade_search, {:from_date => Date.today.to_s, :to_date => Date.today.to_s}

    assert_response :success
    assert_template 'queries_output'
    assert_not_nil assigns(:trades)
    assert_equal 1, assigns(:trades).length, "didn't find the trade for today past midnight"
  end


  def test_on_date_invalid
    get :trade_search, {"date_date"=>{"from(1i)"=>"2007", "to(1i)"=>"2007", "from(2i)"=>"4",
                                       "to(2i)"=>"5", "from(3i)"=>"31", "to(3i)"=>"1"},}

    assert_response :success
    assert_template 'index'
    assert_has_error_box
    assert_not_nil assigns(:report).errors
    assert_not_nil assigns(:report).errors[:from_date]

    assert_nil assigns(:trades)
  end

  def test_by_date_invalid
    get :trade_search, {:suffix => 'date', "date"=>{ "from(1i)"=>"2006", "from(2i)"=>"7", "from(3i)"=>"1",
                              "to(1i)"=>"2006", "to(2i)"=>"11", "to(3i)"=>"35" }}

    assert_response :success
    assert_template 'index'
    assert_has_error_box
    assert_not_nil assigns(:report).errors
    assert_not_nil assigns(:report).errors[:to_date]
  end

  def test_by_date_wide_range
    get :trade_search, {:suffix => 'date', "date_date"=>{ "from(1i)"=>"2006", "from(2i)"=>"7", "from(3i)"=>"1",
                              "to(1i)"=>"2006", "to(2i)"=>"11", "to(3i)"=>"30" }}
    
    assert_response :success
    assert_template 'queries_output'

    assert_not_nil assigns(:trades)
    assert_equal 5, assigns(:trades).length
    @all_trades.each_index { |i| assert_equal @all_trades[4-i], assigns(:trades)[i],
                                        "trades at index "+i.to_s + " don't match" }
    assert_not_equal assigns(:trades)[0].id, assigns(:trades)[1].id    
  end
  
  #2006/7/12 - 2006/8/1 includes 1,2,3
  def test_by_date_partial_range
    get :trade_search, {:suffix => 'date',
                    "date_date"=>{ "from(1i)"=>"2006", "from(2i)"=>"7", "from(3i)"=>"12",
                              "to(1i)"=>"2006", "to(2i)"=>"8", "to(3i)"=>"1" }}
    
    assert_response :success
    assert_template 'queries_output'

    assert_not_nil assigns(:trades)
    assert_equal 3, assigns(:trades).length
    assert_equal @all_trades[3], assigns(:trades)[0]
    assert_equal @all_trades[2], assigns(:trades)[1]
    assert_equal @all_trades[1], assigns(:trades)[2]
    assert_not_equal assigns(:trades)[0].id, assigns(:trades)[1].id
  end
  
  def test_by_date_outside_of_range
    get :trade_search, {:suffix => 'date', "date_date"=>{ "from(1i)"=>"2007", "from(2i)"=>"7", "from(3i)"=>"1",
                              "to(1i)"=>"2008", "to(2i)"=>"11", "to(3i)"=>"30" }}
    
    assert_response :success
    assert_template 'queries_output'
    
    assert_not_nil assigns(:trades)
    assert_equal 0, assigns(:trades).length
    assert_has_error_notice

    # for pagination
    assert_equal "2007-07-01", assigns(:from_date).to_s
    assert_equal "2008-11-30", assigns(:to_date).to_s
  end

  # verify the case where we only get either an equity or a currency pair
  # for example, need to have 2 trades where tradeable_id goes has a valid equity and forex trade
  # but only equity trade should show up
  # So manually set the 2 tradeable_ids to be the same
  def test_right_tradeable_type_shows_up_equity
    Trade.delete_all
    t1 = create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "acct1", Date.civil(2006, 7, 11), "bob", "4.53", "ZAI")
    t2 = create_test_trade(100, 1.23, Side::QF_SIDE_CODE[:buy], "acct1", Date.civil(2006, 7, 11),
                        "ZAI/USD", "4.53", "ZAI", TradesHelper::SecurityTypeForex)
    t2.connection.execute("update trades set tradeable_id=#{t1.tradeable_id} where id=#{t2.id}")

    get :trade_search,{"m_symbol"=>{"root"=>"bob"}, :all_dates => "yes" }


    assert_response :success
    assert_template 'queries_output'

    assert_not_nil assigns(:trades)
    assert_equal 1, assigns(:trades).length, "got the forex trade as well"
  end

  def test_right_tradeable_type_shows_up_forex
    Trade.delete_all
    t1 = create_test_trade(100, 400, Side::QF_SIDE_CODE[:buy], "acct1", Date.civil(2006, 7, 11), "bob", "4.53", "ZAI")
    t2 = create_test_trade(100, 1.23, Side::QF_SIDE_CODE[:buy], "acct1", Date.civil(2006, 7, 11),
                        "ZAI/USD", "4.53", "ZAI", TradesHelper::SecurityTypeForex)
    t2.connection.execute("update trades set tradeable_id=#{t2.tradeable_id} where id=#{t1.id}")

    get :trade_search,{"m_symbol"=>{"root"=>"ZAI/USD"}, :all_dates => "yes" }
    assert_response :success
    assert_template 'queries_output'
    assert_not_nil assigns(:trades)
    assert_equal 1, assigns(:trades).length, "got the equity trade as well"
  end
end
