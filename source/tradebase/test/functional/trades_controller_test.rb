require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'trades_controller'

# Re-raise errors caught by the controller.
class TradesController; def rescue_action(e) raise e end; end

class TradesControllerTest < MarketceteraTestBase
  fixtures :trades, :messages_log

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
    assert_equal 3, assigns(:trade).errors.length, "number of validation errors"
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
    assert_nil Equity.find_by_m_symbol_id("bob")
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
