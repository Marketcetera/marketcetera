require File.dirname(__FILE__) + '/../test_helper'
require 'trades_controller'

# Re-raise errors caught by the controller.
class TradesController; def rescue_action(e) raise e end; end

class TradesControllerTest < Test::Unit::TestCase
  fixtures :trades, :messages_log

  def setup
    @controller = TradesController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
    
    # create the trades from the messages 
    messageLogController = MessageLogsController.new
#    messageLogController.create
    
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
  end

  def test_show
    get :show, :id => 1

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

  def test_edit
    get :edit, :id => 1

    assert_response :success
    assert_template 'edit'

    assert_not_nil assigns(:trade)
    assert assigns(:trade).valid?
  end

  def test_update
    post :update, :id => 1
    assert_response :redirect
    assert_redirected_to :action => 'show', :id => 1
  end

  def test_destroy
    assert_not_nil Trade.find(1)

    post :destroy, :id => 1
    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_raise(ActiveRecord::RecordNotFound) {
      Trade.find(1)
    }
  end
  
  ######## testing helper methods 
  def test_adjust_quantity_by_side
    assert_equal 100, @controller.adjust_quantity_by_side( Trade.new(:quantity => 100, :side => Side::QF_SIDE_CODE[:buy])).quantity
    assert_equal -100, @controller.adjust_quantity_by_side( Trade.new(:quantity => 100, :side => Side::QF_SIDE_CODE[:sell])).quantity
    assert_equal -100, @controller.adjust_quantity_by_side( Trade.new(:quantity => 100, :side => Side::QF_SIDE_CODE[:sellShort])).quantity
    assert_equal -100, @controller.adjust_quantity_by_side( Trade.new(:quantity => 100, :side => Side::QF_SIDE_CODE[:sellShortExempt])).quantity
  end
  
end
