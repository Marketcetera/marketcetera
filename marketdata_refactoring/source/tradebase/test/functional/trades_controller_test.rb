require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'trades_controller'

# Re-raise errors caught by the controller.
class TradesController; def rescue_action(e) raise e end; end

# Override the timezones to be Europe/Minsk (EET)
module ApplicationHelper
  TZ_ID = 'Europe/Minsk'
  LOCAL_TZ = TZInfo::Timezone.get(TZ_ID)
  ENV['TZ'] = 'EET'
end

class TradesControllerTest < MarketceteraTestBase
  fixtures :messages_log, :currencies

  def setup
    @controller = TradesController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
    
    # create the trades from the messages 
    # trade20: B 100 GOOG 408.18
    # trade21: SS GOOG 4000 408.18
    creator = CreateTradesController.new
    [20,21].each { |id| creator.create_one_trade(id) }
    assert_equal 2, Trade.count
    @allTrades = Trade.find(:all)
    
    assert_nil Account.find_by_nickname("noSuchAccount"), "[noSuchAccount] exists before tests started"
  end

  def test_index
    get :index
    assert_response :success
    assert_template 'list'
  end

  def test_list
    get :list

    assert_response :success
    assert_template 'list'

    assert_not_nil assigns(:trades)
    assert_equal @allTrades.length, assigns(:trades).length
    assert_has_show_edit_delete_links(true, true, true)

    assert_not_equal assigns(:trades)[0].id, assigns(:trades)[1].id 
    
    assert_tag :tag => "td", :content => "SS"
  end

  # should be sorted: c, b, a, d, e
  def test_list_sorted_correctly
    Trade.delete_all
    assert_equal 0, Trade.count
    # create a few trades
    create_test_trade(44, 100.10, Quickfix::Side_BUY(), "TOLI", Date.new(2007, 4, 1), "b", 3, "USD")
    create_test_trade(44, 100.10, Quickfix::Side_BUY(), "TOLI", Date.new(2007, 4, 2), "a", 3, "USD")
    create_test_trade(44, 100.10, Quickfix::Side_BUY(), "BOB", Date.new(2007, 4, 1), "c", 3, "USD")
    create_test_trade(44, 100.10, Quickfix::Side_BUY(), "TOLI", Date.new(2007, 4, 3), "d", 3, "USD")
    create_test_trade(44, 100.10, Quickfix::Side_BUY(), "TOLI", Date.new(2007, 4, 4), "e", 3, "USD")

      get :list
      assert_response :success
      trades = assigns(:trades)
      assert_equal 5, trades.length
      assert_equal "c", trades[0].tradeable_m_symbol_root
      assert_equal "b", trades[1].tradeable_m_symbol_root
      assert_equal "a", trades[2].tradeable_m_symbol_root
      assert_equal "d", trades[3].tradeable_m_symbol_root
      assert_equal "e", trades[4].tradeable_m_symbol_root
  end

  # verify that the formatting for long strings in Symbol/Account/Comments is handled
  def test_list_account_symbol_comment_formatting
    Trade.delete_all
    t = Trade.new(:quantity => 10, :comment => "very long comment with spaces",
                   :trade_type => "T", :side => Side::QF_SIDE_CODE[:buy], 
                       :price_per_share => "50.12")
    t.create_trade(t.quantity, "SYMBOL WITH SPACES", t.price_per_share, 19.99,  "USD",
		                             "A COUNT WITH SPACES", DateTime.civil(2006, 10, 20, 12, 23, 0).to_s(:db))
														    
    t.save
    
    get :list
    assert_response :success
    assert_template 'list'

    assert_not_nil assigns(:trades)
    assert_equal 1, assigns(:trades).length
    
    # now verify the symbol is contracted
    assert_tag :tag => "td", :content => "SYMBOL&nbsp;WITH&nbsp;SPACES"
    assert_tag :tag => "td", :content => "very long comment with..."
    assert_tag :tag => "td", :content => "A&nbsp;COU...PACES"

    # verify the post-date is displayed according to format and translated from UTC to EST: time should be October 20, 2006 at 3:23:00 PM EEST
    assert_tag :tag => 'td', :content => /20-Oct-06 15:23:00 EEST/
  end

  # bug #123
  def test_show
    @allTrades[0].imported_fix_msg = nil
    @allTrades[0].save
    get :show, :id => @allTrades[0].id

    assert_response :success
    assert_template 'show'

    assert_not_nil assigns(:trade)
    assert assigns(:trade).valid?
    
    # also verify the transaction display
    assert_tag :tag => "td", :attributes => {:class => "number"}, :content => "&nbsp; "

    # verify there's no imported FIX message text
    assert_no_tag :tag => "div", :attributes => {:id => "view_imported_fix_message"}
  end

  def test_show_imported_trade
      @allTrades[0].imported_fix_msg = "abcdefg"
      @allTrades[0].save

      get :show, :id => @allTrades[0].id

      assert_response :success
      assert_template 'show'

      # verify there's no imported FIX message text
      assert_tag :tag => "div", :attributes => {:id => "view_imported_fix_message"}
  end

  def test_new
    get :new

    assert_response :success
    assert_template 'new'

    assert_not_nil assigns(:trade)

    # verify we have the security type raio buttons
    assert_tag :tag => 'label', :attributes => {:for => 'trade_trade_type'}, :content => "Security type"
    assert_tag :tag => 'input', :attributes => {:class => 'radio'}
  end

  # should generate a bunch of errors
  def test_create_no_args
    num_trades = Trade.count

    # todo: remove date arg once we switch to date validation
    post :create, {:security_type => TradesHelper::SecurityTypeEquity,
                   :trade => {"journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20"}}
    
    assert_template 'new'
    assert_equal 4, assigns(:trade).errors.length, "number of validation errors"
    assert_not_nil assigns(:trade).errors[:symbol]
    assert_not_nil assigns(:trade).errors[:quantity]
    assert_not_nil assigns(:trade).errors[:price_per_share]
    assert_equal num_trades, Trade.count
  end
  
  def test_create_no_symbol
    num_trades = Trade.count

    post :create, {:m_symbol=>{:root=>""}, 
                   :trade=>{:side=>"1", "journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20", 
                            :quantity=>"23", :price_per_share=>"23", :comment=>"", :total_commission=>"", :trade_type=>"T"}, 
                   :currency=>{:alpha_code=>"USD"}, 
                   :account => {:nickname => "noSuchAccount"}, :security_type => TradesHelper::SecurityTypeEquity}

    assert_template 'new'
    assert_equal 1, assigns(:trade).errors.length, "number of validation errors"
    assert_not_nil assigns(:trade).errors[:symbol]
    assert_equal num_trades, Trade.count
    
    # verify the account is not created
    assert !Account.find_by_nickname("noSuchAccount")
  end
  
  def test_create_no_price
    num_trades = Trade.count

    post :create, {:m_symbol=>{:root=>"abc"}, 
                   :trade=>{:side=>"1", "journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20", 
                            :quantity=>"23", :comment=>"", :total_commission=>"", :trade_type=>"T"}, 
                   :currency=>{:alpha_code=>"USD"}, 
                   :account => {:nickname => "noSuchAccount"}, :security_type => TradesHelper::SecurityTypeEquity}

    assert_template 'new'
    assert_equal 1, assigns(:trade).errors.length, "number of validation errors"
    assert_not_nil assigns(:trade).errors[:price_per_share]
    assert_equal num_trades, Trade.count
    
    # verify the account is not created
    assert_nil Account.find_by_nickname("noSuchAccount")
    assert_nil Equity.find_by_m_symbol_id("abc")
  end
  
  def test_create_no_qty
    num_trades = Trade.count
    post :create, {:m_symbol => {:root => "bob"}, :security_type => TradesHelper::SecurityTypeEquity,
                   :account => {:nickname => "noSuchAccount"},
                    :trade => {:price_per_share => "23", :side => 1,
                                "journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20"} }

    assert_template 'new'
    assert_equal 1, assigns(:trade).errors.length, "number of validation errors"
    assert_not_nil assigns(:trade).errors[:quantity]
    assert_equal num_trades, Trade.count

    # verify the account is not created
    assert_nil Account.find_by_nickname("noSuchAccount")
  end
  
  # not specifying an account should get the default UNASSIGNED account
  def test_create_no_account_should_pickup_default
    num_trades = Trade.count
    post :create, {:m_symbol => {:root => "bob"}, :security_type => TradesHelper::SecurityTypeEquity,
                   :account => {:nickname => ""},
                    :trade => {:price_per_share => "23", :side => 1, :quantity=> "111",
                                "journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20"} }

    assert_response :redirect
    assert_redirected_to :action => 'list'
    assert_equal num_trades+1, Trade.count
    assert_not_nil assigns(:trade).account
    assert_equal Account.find_by_nickname(nil), assigns(:trade).account
    assert_equal Account::UNASSIGNED_NAME, assigns(:trade).account.nickname
    assert_not_nil Equity.get_equity("bob", false), "didn't create equity"
    assert_equal currencies(:usd).alpha_code, assigns(:trade).currency_alpha_code
  end
  
  def test_create_successful
    num_trades = Trade.count
    post :create, {:m_symbol => {:root => "bob"}, :security_type => TradesHelper::SecurityTypeEquity,
                   :account => {:nickname => "pupkin"},
                    :trade => {:price_per_share => "23", :side => 1, :quantity => "111", :total_commission => "14.99",
                                "journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20"} }

    assert_response :redirect
    assert_redirected_to :action => 'list'
    assert_equal num_trades+1, Trade.count
    
    assert_not_nil assigns(:trade), "didn't create a trade"
    assert_not_nil Account.find_by_nickname("pupkin"), "didn't create account"
    assert_not_nil Equity.get_equity("bob", false), "didn't create equity"
    
    assert_equal "pupkin", assigns(:trade).account_nickname
    assert_equal "bob", assigns(:trade).tradeable_m_symbol_root
    verify_trade_prices(assigns(:trade), 23 * 111, 14.99)
  end
  
  def test_create_successful_sell
    num_trades = Trade.count
    post :create, {:m_symbol => {:root => "bob"}, :security_type => TradesHelper::SecurityTypeEquity,
                   :account => {:nickname => "pupkin"},
                    :trade => {:price_per_share => "23", :side => Side::QF_SIDE_CODE[:sell].to_s, 
                               :quantity => "111", :total_commission => "14.99",
                               "journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20"} }

    assert_response :redirect
    assert_redirected_to :action => 'list'
    assert_equal num_trades+1, Trade.count
    
    assert_not_nil assigns(:trade), "didn't createa  trade"
    assert_not_nil Account.find_by_nickname("pupkin"), "didn't create account"
    assert_not_nil Equity.get_equity("bob", false), "didn't create equity"
    
    assert_equal "pupkin", assigns(:trade).account_nickname
    assert_equal "bob", assigns(:trade).tradeable_m_symbol_root
    assert_nums_equal Side::QF_SIDE_CODE[:sell], assigns(:trade).side
    verify_trade_prices(assigns(:trade), -23 * 111, 14.99)
  end
  
  def test_create_successful_set_currency
    num_trades = Trade.count
    post :create, {:m_symbol => {:root => "bob"}, :security_type => TradesHelper::SecurityTypeEquity,
                   :account => {:nickname => "pupkin"},
                   :currency => { :alpha_code => 'ZAI'}, 
                    :trade => {:price_per_share => "23", :side => 1, :quantity => "111", :total_commission => "14.99",
                                "journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20"} }

    assert_response :redirect
    assert_redirected_to :action => 'list'
    assert_equal num_trades+1, Trade.count
    
    assert_not_nil assigns(:trade), "didn't create a  trade"
    assert_equal currencies(:ZAI).alpha_code, assigns(:trade).currency_alpha_code
  end
  
  def test_create_neg_commission
    num_trades = Trade.count
    post :create, {:m_symbol => {:root => "bob"}, :security_type => TradesHelper::SecurityTypeEquity, 
                   :trade => {:price_per_share => "23", :quantity => "100", :total_commission => "-100", :side => 1,
                                "journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20"} }

    assert_template 'new'
    assert_equal 1, assigns(:trade).errors.length, "number of validation errors"
    assert_not_nil assigns(:trade).errors[:total_commission]
    assert_equal num_trades, Trade.count
  end
  

  def test_create_no_security_type
    num_trades = Trade.count
    post :create, {:m_symbol => {:root => "bob"}, :security_type => 'bogus',
                   :trade => {:price_per_share => "23", :quantity => "100", :total_commission => "100", :side => 1,
                                "journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20"} }

    assert_template 'new'
    assert_equal 1, assigns(:trade).errors.length, "number of validation errors"
    assert_not_nil assigns(:trade).errors[:security_type]
    assert_equal num_trades, Trade.count
  end

  def test_create_forex_trade_successful
    num_trades = Trade.count
    post :create, {:m_symbol => {:root => "ZAI/USD"}, :security_type => TradesHelper::SecurityTypeForex,
                   :account => {:nickname => "FOREX"},
                    :trade => {:price_per_share => "1.4298", :side => 1, :quantity => "1000000", :total_commission => "14.99",
                                "journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20"} }

    assert_response :redirect
    assert_redirected_to :action => 'list'
    assert_equal num_trades+1, Trade.count

    assert_not_nil assigns(:trade), "didn't create a trade"
    assert_not_nil Account.find_by_nickname("FOREX"), "didn't create account"
    assert_not_nil CurrencyPair.get_currency_pair("ZAIUSD", false), "didn't create equity"

    assert_equal "FOREX", assigns(:trade).account_nickname
    assert_equal "ZAI/USD", assigns(:trade).tradeable_m_symbol_root
  end

  # USD/ZAI doesn't exist, should be created
  def test_create_forex_trade_successful_new_cur_pair
    num_trades = Trade.count
    post :create, {:m_symbol => {:root => "USD/ZAI"}, :security_type => TradesHelper::SecurityTypeForex,
                   :account => {:nickname => "FOREX"},
                    :trade => {:price_per_share => "1.4298", :side => 1, :quantity => "1000000", :total_commission => "14.99",
                                "journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20"} }

    assert_response :redirect
    assert_redirected_to :action => 'list'
    assert_equal num_trades+1, Trade.count

    assert_not_nil assigns(:trade), "didn't create a trade"
    assert_not_nil Account.find_by_nickname("FOREX"), "didn't create account"
    assert_not_nil CurrencyPair.get_currency_pair("USDZAI", false), "didn't create currency pair"

    assert_equal "FOREX", assigns(:trade).account_nickname
    assert_equal "USD/ZAI", assigns(:trade).tradeable_m_symbol_root
  end

  # test creation of forex trade with currency pair that doesn't exist - should fail
  def test_create_forex_currency_pair_dne
    def test_create_forex_trade_successful_new_cur_pair
      num_trades = Trade.count
      post :create, {:m_symbol => {:root => "ABC/XYZ"}, :security_type => TradesHelper::SecurityTypeForex,
                     :account => {:nickname => "FOREX"},
                      :trade => {:price_per_share => "1.4298", :side => 1, :quantity => "1000000", :total_commission => "14.99",
                                  "journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20"} }

      assert_template 'new'
      assert_equal num_trades, Trade.count

      assert_not_nil assigns(:trade).errors[:symbol]
      assert_not_nil assigns(:trade).errors[:symbol].match("Unknown currency")
      assert_nil CurrencyPair.get_currency_pair("ABCXYZ", false), "created currency pair when it shouldn't have"
    end
  end

  def test_edit
    get :edit, :id => @allTrades[0].id

    assert_response :success
    assert_template 'edit'

    assert_not_nil assigns(:trade)
    assert assigns(:trade).valid?
    
    # verify the symbol edit box shows up 
    assert_tag  :input, :attributes => { :id => 'currency_alpha_code', :value => 'USD' }
  end

  def test_edit_verify_fields_prefilled
    prefilled_test_helper( Side::QF_SIDE_CODE[:sellShort], "TOLI", "4.99000", "11.00000", "some-account", "ZAI", 7.37, Date.civil(2006, 10,10))
    prefilled_test_helper( Side::QF_SIDE_CODE[:sell], "TOLI", "4.99000", "11.00000", '', "ZAI", 7.37, Date.civil(2006, 10,10))
    prefilled_test_helper( Side::QF_SIDE_CODE[:sell], "TOLI", "4.99000", "11.00000", '', '', 7.37, Date.civil(2006, 10,10))
    prefilled_test_helper( Side::QF_SIDE_CODE[:sell], "TOLI", "4.99000", "11.00000", '', nil, 7.37, Date.civil(2006, 10,10))
  end
  
  def test_edit_verify_side_selected_correctly
    prefilled_test_helper( Side::QF_SIDE_CODE[:buy], "TOLI", "4.99000", "11.00000", "some-account", "ZAI", 7.37, Date.civil(2006, 10,10))
    prefilled_test_helper( Side::QF_SIDE_CODE[:sellShort], "TOLI", "4.99000", "11.00000", "some-account", "ZAI", 7.37, Date.civil(2006, 10,10))
    prefilled_test_helper( Side::QF_SIDE_CODE[:sell], "TOLI", "4.99000", "11.00000", nil, "ZAI", 7.37, Date.civil(2006, 10,10))
  end

  def test_edit_page_security_type_disabled
    get :edit, :id => @allTrades[0]
    assert_response :success
    assert_template 'edit'
    # shouldn't have any radio buttons
    assert_no_tag :tag => 'input', :attributes => {:class => 'radio', :id => "security_type_f"}
    assert_tag :tag => 'div', :attributes => {:id => "asset_type"}, :content => "Equity"

    # create currency trade
    currTrade = create_test_trade(10000, 1.11, Quickfix::Side_BUY(), "forex", Date.today, "ZAI/USD", 3.33, "USD", TradesHelper::SecurityTypeForex)
    get :edit, :id => currTrade
    assert_response :success
    assert_template 'edit'
    # shouldn't have any radio buttons
    assert_no_tag :tag => 'input', :attributes => {:class => 'radio', :id => "security_type_f"}
    assert_tag :tag => 'div', :attributes => {:id => 'asset_type'}, :content => 'CurrencyPair'
  end

  def test_update_no_actual_edits
    tradeCopy = @allTrades[0].clone
    post :update, { :id =>  @allTrades[0].id,
                    :trade => @allTrades[0].attributes.merge(
                     {"journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20"}), 
                    :account => {:nickname => @allTrades[0].account_nickname}, 
                    :m_symbol => {:root => @allTrades[0].tradeable_m_symbol_root}}
    assert_response :redirect
    assert_redirected_to :action => 'show', :id =>  @allTrades[0].id
    assert "Trade was successfully updated", flash[:notice]
    
    # verify numbers/accounts still same
    verify_trade_prices(Trade.find(@allTrades[0].id), tradeCopy.quantity * tradeCopy.price_per_share, tradeCopy.total_commission)
  end

  def test_update_price
    tradeCopy = @allTrades[0].clone
        
    post :update, { :id =>  @allTrades[0].id,
                    :trade => @allTrades[0].attributes.merge(
                     {"journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20", 
                     "price_per_share" => 125}), 
                    :account => {:nickname => @allTrades[0].account_nickname}, 
                    :m_symbol => {:root => @allTrades[0].tradeable_m_symbol_root}}
    assert_response :redirect
    assert_redirected_to :action => 'show', :id =>  @allTrades[0].id
    assert "Trade was successfully updated", flash[:notice]
    
    assert_nums_equal 125, assigns(:trade).price_per_share
    # verify numbers/accounts still same
    verify_trade_prices(Trade.find(@allTrades[0].id), tradeCopy.quantity * 125, tradeCopy.total_commission)
  end

  def test_update_qty
    tradeCopy = @allTrades[0].clone
        
    post :update, { :id =>  @allTrades[0].id,
                    :trade => @allTrades[0].attributes.merge(
                     {"journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20", 
                     "quantity" => 350}), 
                    :account => {:nickname => @allTrades[0].account_nickname}, 
                    :m_symbol => {:root => @allTrades[0].tradeable_m_symbol_root}}
    assert_response :redirect
    assert_redirected_to :action => 'show', :id =>  @allTrades[0].id
    assert "Trade was successfully updated", flash[:notice]
    
    assert_nums_equal 350, assigns(:trade).quantity
    # verify numbers/accounts still same
    verify_trade_prices(Trade.find(@allTrades[0].id), tradeCopy.price_per_share * 350, tradeCopy.total_commission)
  end

  def test_update_commission
    tradeCopy = @allTrades[0].clone
        
    post :update, { :id =>  @allTrades[0].id,
                    :trade => @allTrades[0].attributes.merge(
                     {"journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20", 
                     "total_commission" => 12.34}), 
                    :account => {:nickname => @allTrades[0].account_nickname}, 
                    :m_symbol => {:root => @allTrades[0].tradeable_m_symbol_root}}
    assert_response :redirect
    assert_redirected_to :action => 'show', :id =>  @allTrades[0].id
    assert "Trade was successfully updated", flash[:notice]
    
    assert_nums_equal 12.34, assigns(:trade).total_commission
    # verify numbers/accounts still same
    verify_trade_prices(Trade.find(@allTrades[0].id), tradeCopy.price_per_share * tradeCopy.quantity, 12.34)
  end

  def test_update_buy_to_sell
    tradeCopy = @allTrades[0].clone
        
    post :update, { :id =>  @allTrades[0].id,
                    :trade => @allTrades[0].attributes.merge(
                     {"journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20", 
                     "side" => Side::QF_SIDE_CODE[:sell]}), 
                    :account => {:nickname => @allTrades[0].account_nickname}, 
                    :m_symbol => {:root => @allTrades[0].tradeable_m_symbol_root}}
    assert_response :redirect
    assert_redirected_to :action => 'show', :id =>  @allTrades[0].id
    assert "Trade was successfully updated", flash[:notice]
    
    assert_nums_equal -tradeCopy.quantity, assigns(:trade).quantity
    # verify numbers/accounts still same
    verify_trade_prices(Trade.find(@allTrades[0].id), -tradeCopy.price_per_share * tradeCopy.quantity, tradeCopy.total_commission)
  end

  # allTrades[1] is a SSE that we just change to Sell but all #s shoudl stay same
  def test_update_sse_to_sell_short
    tradeCopy = @allTrades[1].clone
        
    post :update, { :id =>  @allTrades[1].id,
                    :trade => @allTrades[1].attributes.merge(
                     {"journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20", 
                     "side" => Side::QF_SIDE_CODE[:sell]}), 
                    :account => {:nickname => @allTrades[1].account_nickname}, 
                    :m_symbol => {:root => @allTrades[1].tradeable_m_symbol_root}}
    assert_response :redirect
    assert_redirected_to :action => 'show', :id =>  @allTrades[1].id
    assert "Trade was successfully updated", flash[:notice]
    
    assert_nums_equal tradeCopy.quantity, assigns(:trade).quantity
    # verify numbers/accounts still same
    verify_trade_prices(Trade.find(@allTrades[1].id), tradeCopy.price_per_share * tradeCopy.quantity, tradeCopy.total_commission)
  end

  def test_update_currency
    tradeCopy = @allTrades[1].clone
        
    post :update, { :id =>  @allTrades[1].id,
                    :trade => @allTrades[1].attributes.merge(
                     {"journal_post_date(1i)"=>"2005", "journal_post_date(2i)"=>"9", "journal_post_date(3i)"=>"11"}), 
                    :account => {:nickname => @allTrades[1].account_nickname}, 
                    :currency => { :alpha_code => 'ZAI'}, 
                    :m_symbol => {:root => @allTrades[1].tradeable_m_symbol_root}}
    assert_response :redirect
    assert_redirected_to :action => 'show', :id =>  @allTrades[1].id
    assert "Trade was successfully updated", flash[:notice]
    assert_equal currencies(:ZAI).alpha_code, assigns(:trade).currency_alpha_code
  end

  # new date: 2005-9-11
  def test_update_date
    tradeCopy = @allTrades[1].clone
        
    post :update, { :id =>  @allTrades[1].id,
                    :trade => @allTrades[1].attributes.merge(
                     {"journal_post_date(1i)"=>"2005", "journal_post_date(2i)"=>"9", "journal_post_date(3i)"=>"11"}), 
                    :account => {:nickname => @allTrades[1].account_nickname}, 
                    :m_symbol => {:root => @allTrades[1].tradeable_m_symbol_root}}
    assert_response :redirect
    assert_redirected_to :action => 'show', :id =>  @allTrades[1].id
    assert "Trade was successfully updated", flash[:notice]
    
    assert_equal Date.civil(2005, 9, 11), assigns(:trade).journal_post_date
    # verify numbers/accounts still same
    verify_trade_prices(Trade.find(@allTrades[1].id), tradeCopy.price_per_share * tradeCopy.quantity, tradeCopy.total_commission)
  end

  # Switch to a different account
  # Need to verify the old postings are now referring to different sub-account ids
  def test_update_switch_accounts
    tradeCopy = @allTrades[1].clone
    newAcct = Account.create(:nickname => 'new-account-'+Date.new.to_s, :institution_identifier => "12345")    
    post :update, { :id =>  @allTrades[1].id,
                    :trade => @allTrades[1].attributes.merge(
                     {"journal_post_date(1i)"=>"2005", "journal_post_date(2i)"=>"9", "journal_post_date(3i)"=>"11"}), 
                    :account => {:nickname => newAcct.nickname }, 
                    :m_symbol => {:root => @allTrades[1].tradeable_m_symbol_root}}
    assert_response :redirect
    assert_redirected_to :action => 'show', :id =>  @allTrades[1].id
    assert "Trade was successfully updated", flash[:notice]
    
    assert_equal newAcct.nickname, assigns(:trade).account_nickname
    assert_not_equal  newAcct.nickname, tradeCopy.account_nickname
    # verify that all postings point to new account
    assigns(:trade).journal.postings.each { |p| assert_equal newAcct, p.sub_account.account, "posting not referencing right account"  }
  end
  
  # Switch to a different equity
  # Verify that old equity still exists, the m_symbol still exists
  # and that we have a new equity pointing to a new m_symbol
  def test_update_equity
    tradeCopy = @allTrades[1].clone
    newSymbol = "IFLI"
    oldSymbol = @allTrades[1].tradeable_m_symbol_root
    oldEquityId = @allTrades[1].tradeable.id
    
    post :update, { :id =>  @allTrades[1].id,
                    :trade => @allTrades[1].attributes.merge(
                     {"journal_post_date(1i)"=>"2005", "journal_post_date(2i)"=>"9", "journal_post_date(3i)"=>"11"}), 
                    :m_symbol => {:root => newSymbol}}
    assert_response :redirect
    assert_redirected_to :action => 'show', :id =>  @allTrades[1].id
    assert "Trade was successfully updated", flash[:notice]
    
    assert_equal newSymbol, assigns(:trade).tradeable_m_symbol_root
    assert_not_equal oldEquityId, assigns(:trade).tradeable.id, "equity IDs are same"
    assert_not_equal tradeCopy.tradeable.m_symbol.id, assigns(:trade).tradeable.m_symbol.id, "m_symbol ids are same"
  end
  
  def test_update_comment
    tradeCopy = @allTrades[1].clone
    newComment = "your momma so fat.."
    post :update, { :id =>  @allTrades[1].id,
                    :trade => @allTrades[1].attributes.merge(
                     {"journal_post_date(1i)"=>"2005", "journal_post_date(2i)"=>"9", "journal_post_date(3i)"=>"11", 
                     "comment" => newComment}), 
                    :m_symbol => {:root => @allTrades[1].tradeable_m_symbol_root}}
    assert_response :redirect
    assert_redirected_to :action => 'show', :id =>  @allTrades[1].id
    assert "Trade was successfully updated", flash[:notice]
    
    assert_equal newComment, assigns(:trade).comment
    assert_not_equal tradeCopy.comment, assigns(:trade).comment, "comments are same"
  end

  def test_update_forex
    currTrade = create_test_trade(10000, 1.11, Quickfix::Side_BUY(), "forex", Date.today, "ZAI/USD", 3.33, "USD", TradesHelper::SecurityTypeForex)
    # update comment field
    post :update, {:id => currTrade.id,  :m_symbol => {:root=>"ZAI/USD"}, 
                   :trade => currTrade.attributes.merge({"comment" => 'new comment',  "journal_post_date(1i)"=>"2005",
                                                         "journal_post_date(2i)"=>"9", "journal_post_date(3i)"=>"11"})}
    assert_response :redirect
    assert_redirected_to :action => 'show', :id =>  currTrade.id
    assert "Trade was successfully updated", flash[:notice]
    assert_equal "new comment", assigns(:trade).comment
  end
  
  def test_destroy
    t = Trade.find( @allTrades[0].id)
    assert_not_nil t
    journal = t.journal
    assert_not_nil Journal.find(journal.id)
    
    post :destroy, :id =>  @allTrades[0].id
    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_raise(ActiveRecord::RecordNotFound) {
      Trade.find( @allTrades[0].id)
    }
    assert_raise(ActiveRecord::RecordNotFound) {
      Journal.find(journal.id)
    }
  end
  
  def test_destroy_newly_created
    count = Trade.count
    post :create, {:m_symbol => {:root => "bob"}, :security_type => TradesHelper::SecurityTypeEquity,
                   :account => {:nickname => "pupkin"},
                    :trade => {:price_per_share => "23", :side => 1, :quantity => "111", :total_commission => "14.99",
                                "journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20"} }
    t = assigns(:trade)
    assert_equal count+1, Trade.count

    assert_not_nil t
    journal = t.journal
    postings = journal.postings
    assert_not_nil Journal.find(journal.id)

    post :destroy, :id =>  t.id
    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_raise(ActiveRecord::RecordNotFound, "didn't delete trade") {
      Trade.find(t)
    }
    assert_raise(ActiveRecord::RecordNotFound, "didn't delete underlying journal") {
      Journal.find(journal.id)
    }
    postings.each {|p| assert_raise(ActiveRecord::RecordNotFound, "didn't delete underlying postings") {Posting.find(p.id) } }
  end

  ### Helpers ####
  # this tests internal ticket:66
  # Need to verify that when we click on Edit the right option is selected in the side drop-down
  def prefilled_test_helper(side, symbol, price, qty, account, cur, commission, date)
    # create some trade with ZAI currency
    t = Trade.new(:quantity => qty, :price_per_share => price, :side => side)
    assert t.create_trade(t.quantity, symbol, t.price_per_share, commission, cur, account, date)
    assert t.save, "couldn't save the trade"
    get :edit, :id => t.id

    assert_response :success
    assert_template 'edit'

    assert_not_nil assigns(:trade)
    assert assigns(:trade).valid?
    
    # verify the symbol edit box shows up 
    cur = cur.blank? ? "USD" : cur
    account = account.blank? ? Account::UNASSIGNED_NAME : account
    
    assert_tag :input, :attributes => { :id => 'currency_alpha_code', :value => cur }
    assert_tag :input, :attributes => { :id => 'account_nickname', :value => account }
    assert_tag :input, :attributes => { :id => 'trade_price_per_share', :value => price }
    assert_tag :input, :attributes => { :id => 'trade_quantity', :value => qty }
    assert_tag :input, :attributes => { :id => 'trade_total_commission', :value => commission }
    assert_tag :tag => 'option', :attributes => { :value => side, :selected => "selected"}, 
               :parent => { :tag => 'select', :attributes => {:id => 'trade_side' } }
  end
end
