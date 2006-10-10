require File.dirname(__FILE__) + '/../test_helper'
require 'message_logs_controller'

# Re-raise errors caught by the controller.
class MessagesLogController; def rescue_action(e) raise e end; end

class MessagesLogControllerTest < Test::Unit::TestCase
  fixtures :messages_log

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

    assert_not_nil assigns("exec_report_pages")
    assert_equal 7, assigns("exec_report_pages").length
  end

  def test_show
    get :show, :id => 20

    assert_response :success
    assert_template 'show'

    assert_not_nil assigns(:message_log)
    assert assigns(:message_log).valid?
    assert_not_nil assigns(:qf_message)
  end
  
  def test_conditional_date_list
    flunk "conditional date list test not implemented yet"
  end
end
