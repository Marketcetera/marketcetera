require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'sub_accounts_controller'

# Re-raise errors caught by the controller.
class SubAccountsController; def rescue_action(e) raise e end; end

class SubAccountsControllerTest < MarketceteraTestBase
  fixtures :sub_accounts, :accounts
  include ApplicationHelper
  
  def setup
    @controller = SubAccountsController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end

  def test_index
    get :index
    assert_response :success
    assert_template 'list'
    
    assert_not_nil assigns(:sub_accounts)
    assert_equal 7, assigns(:sub_accounts).length
  end

  def test_list
    get :list

    assert_response :success
    assert_template 'list'

    assert_not_nil assigns(:sub_accounts)
    assert_equal 7, assigns(:sub_accounts).length
    assert_has_show_edit_delete_links(true, false, false)
  end

  def test_show
    get :show, :id => 1

    assert_response :success
    assert_template 'show'

    assert_not_nil assigns(:sub_account)
    assert assigns(:sub_account).valid?
  end

  def test_verify_edit_create_update_dne
    assert_no_controller_action(false, :new)
    assert_no_controller_action(true, :create, :sub_account => {})
    assert_no_controller_action(false, :edit, :id => 1)
    assert_no_controller_action(true, :update, :id => 1)
    assert_no_controller_action(true, :destroy, :id => 1)
  end
end
