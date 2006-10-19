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

  def test_create
    num_trades = Trade.count

    post :create, :trade => {}

    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_equal num_trades + 1, Trade.count
  end
  
  def test_create_no_symbol
    num_trades = Trade.count
    post :create, :trade => {}

    assert_redirected_to :action => 'new'
    assert_equal "Please specify the symbol.", flash[:error]
    assert_equal num_trades, Trade.count
  end
  
  def test_create_no_qty
    num_trades = Trade.count
    post :create, {:m_symbol => {:root => "bob"} }

    assert_redirected_to :action => 'new'
    assert_equal 'Please specify positive quantity.', flash[:error]
    assert_equal num_trades, Trade.count
  end
  

  def test_edit
    get :edit, :id => @allTrades[0].id

    assert_response :success
    assert_template 'edit'

    assert_not_nil assigns(:trade)
    assert assigns(:trade).valid?
  end

  def test_update
    post :update, :id =>  @allTrades[0].id
    assert_response :redirect
    assert_redirected_to :action => 'show', :id =>  @allTrades[0].id
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
  
  ######## testing helper methods 
  
end
