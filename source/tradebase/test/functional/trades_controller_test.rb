require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'trades_controller'

# Re-raise errors caught by the controller.
class TradesController; def rescue_action(e) raise e end; end

class TradesControllerTest < MarketceteraTestBase
  fixtures :messages_log, :currencies

  def setup
    @controller = TradesController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
    
    # create the trades from the messages 
    creator = CreateTradesController.new
    [20,21].each { |id| creator.create_one_trade(id) }
    assert_equal 2, Trade.count
    @allTrades = Trade.find_all
    
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
  end

  def test_show
    get :show, :id => @allTrades[0].id

    assert_response :success
    assert_template 'show'

    assert_not_nil assigns(:trade)
    assert assigns(:trade).valid?
  end

  def test_new
    get :new

    assert_response :success
    assert_template 'new'

    assert_not_nil assigns(:trade)
  end

  # should generate a bunch of errors
  def test_create_no_args
    num_trades = Trade.count

    post :create, :trade => {}

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
                   :account => {:nickname => "noSuchAccount"}}

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
                   :account => {:nickname => "noSuchAccount"}}

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
    post :create, {:m_symbol => {:root => "bob"}, 
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
    post :create, {:m_symbol => {:root => "bob"}, 
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
    post :create, {:m_symbol => {:root => "bob"}, 
                   :account => {:nickname => "pupkin"},
                    :trade => {:price_per_share => "23", :side => 1, :quantity => "111", :total_commission => "14.99",
                                "journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20"} }

    assert_response :redirect
    assert_redirected_to :action => 'list'
    assert_equal num_trades+1, Trade.count
    
    assert_not_nil assigns(:trade), "didn't createa  trade"
    assert_not_nil Account.find_by_nickname("pupkin"), "didn't create account"
    assert_not_nil Equity.get_equity("bob", false), "didn't create equity"
    
    assert_equal "pupkin", assigns(:trade).account_nickname
    assert_equal "bob", assigns(:trade).tradeable_m_symbol_root
    verify_trade_prices(assigns(:trade), 23 * 111, 14.99)
  end
  
  def test_create_successful_sell
    num_trades = Trade.count
    post :create, {:m_symbol => {:root => "bob"}, 
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
    post :create, {:m_symbol => {:root => "bob"}, 
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
    post :create, {:m_symbol => {:root => "bob"}, 
                   :trade => {:price_per_share => "23", :quantity => "100", :total_commission => "-100", :side => 1,
                                "journal_post_date(1i)"=>"2006", "journal_post_date(2i)"=>"10", "journal_post_date(3i)"=>"20"} }

    assert_template 'new'
    assert_equal 1, assigns(:trade).errors.length, "number of validation errors"
    assert_not_nil assigns(:trade).errors[:total_commission]
    assert_equal num_trades, Trade.count
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
    # create some trade with ZAI currency
    t = Trade.new(:quantity => 11, :price_per_share => 4.99, :side => Side::QF_SIDE_CODE[:buy])
    assert t.create_equity_trade(t.quantity, "TOLI", t.price_per_share, 7.37, "ZAI", "some-account", Date.civil(2006, 10,10))
    assert t.save, "couldn't save the trade"
    get :edit, :id => t.id

    assert_response :success
    assert_template 'edit'

    assert_not_nil assigns(:trade)
    assert assigns(:trade).valid?
    
    # verify the symbol edit box shows up 
    assert_tag :input, :attributes => { :id => 'currency_alpha_code', :value => 'ZAI' }
    assert_tag :input, :attributes => { :id => 'account_nickname', :value => 'some-account' }
    assert_tag :input, :attributes => { :id => 'trade_price_per_share', :value => '4.99' }
    assert_tag :input, :attributes => { :id => 'trade_quantity', :value => '11' }
    assert_tag :input, :attributes => { :id => 'trade_total_commission', :value => '7.37' }
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
  

  def test_destroy
    assert_not_nil Trade.find( @allTrades[0].id)

    post :destroy, :id =>  @allTrades[0].id
    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_raise(ActiveRecord::RecordNotFound) {
      Trade.find( @allTrades[0].id)
    }
  end
end
