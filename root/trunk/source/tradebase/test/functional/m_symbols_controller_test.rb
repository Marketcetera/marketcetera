require File.dirname(__FILE__) + '/../test_helper'
require 'm_symbols_controller'

# Re-raise errors caught by the controller.
class MSymbolsController; def rescue_action(e) raise e end; end

class MSymbolsControllerTest < Test::Unit::TestCase
  fixtures :m_symbols

  def setup
    @controller = MSymbolsController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
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

    assert_not_nil assigns(:m_symbols)
  end

  def test_show
    get :show, :id => 1

    assert_response :success
    assert_template 'show'

    assert_not_nil assigns(:m_symbol)
    assert assigns(:m_symbol).valid?
  end

  def test_new
    get :new

    assert_response :success
    assert_template 'new'

    assert_not_nil assigns(:m_symbol)
  end

  def test_create_no_args
    num_m_symbols = MSymbol.count

    post :create, :m_symbol => {}

    assert_template 'new'
    assert_equal 1, assigns(:m_symbol).errors.length, "number of validation errors"
    assert_not_nil assigns(:m_symbol).errors[:root]

    assert_equal num_m_symbols, MSymbol.count
  end

  def test_create_successful
    num_m_symbols = MSymbol.count
    
    flunk("finish implementing me")

    post :create, :m_symbol => {}

    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_equal num_m_symbols + 1, MSymbol.count
  end


  def test_edit
    get :edit, :id => 1

    assert_response :success
    assert_template 'edit'

    assert_not_nil assigns(:m_symbol)
    assert assigns(:m_symbol).valid?
  end

  def test_update
    post :update, :id => 1
    assert_response :redirect
    assert_redirected_to :action => 'show', :id => 1
  end

  def test_destroy
    assert_not_nil MSymbol.find(1)

    post :destroy, :id => 1
    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_raise(ActiveRecord::RecordNotFound) {
      MSymbol.find(1)
    }
  end
end
