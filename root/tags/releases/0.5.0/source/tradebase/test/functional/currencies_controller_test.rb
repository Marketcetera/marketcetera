require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'currencies_controller'

# Re-raise errors caught by the controller.
class CurrenciesController; def rescue_action(e) raise e end; end

class CurrenciesControllerTest < MarketceteraTestBase
  fixtures :currencies

  def setup
    @controller = CurrenciesController.new
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

    assert_not_nil assigns(:currencies)
    assert_has_show_edit_delete_links(true, false, false)
  end

  def test_show
    usd = Currency.find_by_alpha_code("USD")
    get :show, :id => usd.id

    assert_response :success
    assert_template 'show'

    assert_not_nil assigns(:currency)
    assert assigns(:currency).valid?
  end

  def test_verify_edit_create_update_dne
    assert_no_controller_action(false, :new)
    assert_no_controller_action(true, :create, :currency => {})
    assert_no_controller_action(false, :edit, :id => 1)
    assert_no_controller_action(true, :update, :id => 1)
    assert_no_controller_action(true, :destroy, :id => 1)
  end
end
