require File.dirname(__FILE__) + '/../test_helper'
require 'message_display_controller'

# Re-raise errors caught by the controller.
class MessageDisplayController; def rescue_action(e) raise e end; end

class MessageDisplayControllerTest < Test::Unit::TestCase
  fixtures :messages_logs

  def setup
    @controller = MessageDisplayController.new
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

    assert_not_nil assigns(:messages_logs)
  end

  def test_show
    get :show, :id => 1

    assert_response :success
    assert_template 'show'

    assert_not_nil assigns(:messages_log)
    assert assigns(:messages_log).valid?
  end

  def test_new
    get :new

    assert_response :success
    assert_template 'new'

    assert_not_nil assigns(:messages_log)
  end

  def test_create
    num_messages_logs = MessagesLog.count

    post :create, :messages_log => {}

    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_equal num_messages_logs + 1, MessagesLog.count
  end

  def test_edit
    get :edit, :id => 1

    assert_response :success
    assert_template 'edit'

    assert_not_nil assigns(:messages_log)
    assert assigns(:messages_log).valid?
  end

  def test_update
    post :update, :id => 1
    assert_response :redirect
    assert_redirected_to :action => 'show', :id => 1
  end

  def test_destroy
    assert_not_nil MessagesLog.find(1)

    post :destroy, :id => 1
    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_raise(ActiveRecord::RecordNotFound) {
      MessagesLog.find(1)
    }
  end
end
