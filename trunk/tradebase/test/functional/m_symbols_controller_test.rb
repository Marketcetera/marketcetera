require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'm_symbols_controller'

# Re-raise errors caught by the controller.
class MSymbolsController; def rescue_action(e) raise e end; end

class MSymbolsControllerTest < MarketceteraTestBase
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
    assert_has_show_edit_delete_links(true, true, true)
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
    assert_has_error_box

    assert_equal num_m_symbols, MSymbol.count
  end

  def test_create_successful
    num_m_symbols = MSymbol.count
    
    post :create, :m_symbol => { :root => "ifli", :reuters => "IFLI", :isin => "8765", :bloomberg => "IFLI.IM"}

    assert_response :redirect
    assert_redirected_to :action => 'list'
    
    assert_equal num_m_symbols + 1, MSymbol.count
    assert_not_nil MSymbol.find_by_root("ifli")
    ifli = MSymbol.find_by_root("ifli")
    assert_equal "IFLI", ifli.reuters
    assert_equal "8765", ifli.isin
    assert_equal "IFLI.IM", ifli.bloomberg
  end


  def test_edit
    get :edit, :id => 1

    assert_response :success
    assert_template 'edit'

    assert_not_nil assigns(:m_symbol)
    assert assigns(:m_symbol).valid?
  end

  def test_update
    post :update, { :id => 1, :m_symbol => { :isin => "435" } }
    assert_response :redirect
    assert_redirected_to :action => 'show', :id => 1
    assert_equal "435", MSymbol.find(1).isin
  end

  def test_destroy
    assert_not_nil MSymbol.find(4)

    post :destroy, :id => 1
    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_raise(ActiveRecord::RecordNotFound) {
      MSymbol.find(1)
    }
  end
end
