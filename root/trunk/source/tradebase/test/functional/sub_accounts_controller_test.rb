require File.dirname(__FILE__) + '/../test_helper'
require 'sub_accounts_controller'

# Re-raise errors caught by the controller.
class SubAccountsController; def rescue_action(e) raise e end; end

class SubAccountsControllerTest < Test::Unit::TestCase
  fixtures :sub_accounts

  def setup
    @controller = SubAccountsController.new
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

    assert_not_nil assigns(:sub_accounts)
  end

  def test_show
    get :show, :id => 1

    assert_response :success
    assert_template 'show'

    assert_not_nil assigns(:sub_account)
    assert assigns(:sub_account).valid?
  end

  def test_new
    get :new

    assert_response :success
    assert_template 'new'

    assert_not_nil assigns(:sub_account)
  end

  def test_create
    num_sub_accounts = SubAccount.count

    post :create, :sub_account => {}

    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_equal num_sub_accounts + 1, SubAccount.count
  end

  def test_edit
    get :edit, :id => 1

    assert_response :success
    assert_template 'edit'

    assert_not_nil assigns(:sub_account)
    assert assigns(:sub_account).valid?
  end

  def test_update
    post :update, :id => 1
    assert_response :redirect
    assert_redirected_to :action => 'show', :id => 1
  end

  def test_destroy
    assert_not_nil SubAccount.find(1)

    post :destroy, :id => 1
    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_raise(ActiveRecord::RecordNotFound) {
      SubAccount.find(1)
    }
  end
end
