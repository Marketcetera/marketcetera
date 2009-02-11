require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'accounts_controller'

# Re-raise errors caught by the controller.
class AccountsController; def rescue_action(e) raise e end; end

class AccountsControllerTest < MarketceteraTestBase
  fixtures :accounts

  def setup
    @controller = AccountsController.new
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

#    assert_response :success
#    assert_template 'list'
#
#    assert_not_nil assigns(:accounts)
#    assert_equal 3, assigns(:accounts).length
#    assert_has_show_edit_delete_links(true, true, true)
  end

  def test_show
    get :show, :id => 1

    assert_response :success
    assert_template 'show'

    assert_not_nil assigns(:account)
    assert assigns(:account).valid?
  end

  def test_new
    get :new

    assert_response :success
    assert_template 'new'

    assert_not_nil assigns(:account)
  end

  def test_create
    num_accounts = Account.count

    post :create, :account => { :nickname=>"vasya", :description=>"pupkin", :institution_identifier=>"vp"}

    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_equal num_accounts + 1, Account.count
    
    assert_not_nil Account.find_by_nickname('vasya')
  end

  def test_edit
    get :edit, :id => 1

    assert_response :success
    assert_template 'edit'

    assert_not_nil assigns(:account)
    assert assigns(:account).valid?
  end

  def test_update
    initial = Account.find(1)
    assert_not_equal "new nick", initial.nickname
    
    post :update, {:id => 1, :account => {:nickname => "new nick" }}
    assert_response :redirect
    assert_redirected_to :action => 'show', :id => 1
    assert_equal "Account was successfully updated.", flash[:notice]
    assert assigns(:account).valid?
    assert_equal "new nick", assigns(:account).nickname
    assert_equal "new nick", Account.find(1).nickname
  end

  def test_reject_empty_nickname
    initial = Account.find(2)
    assert_not_nil initial.nickname
    
    post :update, {:id => 2, :account => {:nickname => nil } }
    assert_response :success
    assert_errors
    assert_not_nil assigns(:account).errors[:nickname]
    
    
    post :update, {:id => 2, :account => {:nickname => '' } }
    assert_response :success
    assert_errors
    assert_not_nil assigns(:account).errors[:nickname]
      
    post :update, {:id => 2, :account => {:nickname => "bob", :institution_identifier=>nil } }
    assert_response :success
    assert_errors
    assert_not_nil assigns(:account).errors[:institution_identifier]
      
    post :update, {:id => 2, :account => {:nickname => "bob", :institution_identifier=>'' } }
    assert_response :success
    assert_errors
    assert_not_nil assigns(:account).errors[:institution_identifier]    
  end

  def test_destroy
    # create a new account
    acct = Account.create(:nickname=>"test acct", :institution_identifier=>"test")
    assert_not_nil acct
    assert acct.sub_accounts.length > 0
    assert_not_nil SubAccount.find_by_account_id(acct.id)
    
    post :destroy, :id => acct.id
    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_raise(ActiveRecord::RecordNotFound) {
      Account.find(acct.id)
    }
    
    # verify subaccounts are gone
    assert_nil SubAccount.find_by_account_id(acct.id)
  end
end
