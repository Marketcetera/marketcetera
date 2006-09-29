require File.dirname(__FILE__) + '/../test_helper'
require 'message_logs_controller'

# Re-raise errors caught by the controller.
class MessageLogsController; def rescue_action(e) raise e end; end

class MessageLogsControllerTest < Test::Unit::TestCase
  fixtures :message_logs

  def setup
    @controller = MessageLogsController.new
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

    assert_not_nil assigns(:message_logs)
  end

  def test_show
    get :show, :id => 1

    assert_response :success
    assert_template 'show'

    assert_not_nil assigns(:message_log)
    assert assigns(:message_log).valid?
  end

  def test_new
    get :new

    assert_response :success
    assert_template 'new'

    assert_not_nil assigns(:message_log)
  end

  def test_create
    num_message_logs = MessageLog.count

    post :create, :message_log => {}

    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_equal num_message_logs + 1, MessageLog.count
  end

  def test_edit
    get :edit, :id => 1

    assert_response :success
    assert_template 'edit'

    assert_not_nil assigns(:message_log)
    assert assigns(:message_log).valid?
  end

  def test_update
    post :update, :id => 1
    assert_response :redirect
    assert_redirected_to :action => 'show', :id => 1
  end

  def test_destroy
    assert_not_nil MessageLog.find(1)

    post :destroy, :id => 1
    assert_response :redirect
    assert_redirected_to :action => 'list'

    assert_raise(ActiveRecord::RecordNotFound) {
      MessageLog.find(1)
    }
  end
end
