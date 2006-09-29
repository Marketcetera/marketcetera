require File.dirname(__FILE__) + '/../test_helper'
require 'messages_log_controller'

# Re-raise errors caught by the controller.
class MessagesLogController; def rescue_action(e) raise e end; end

class MessagesLogControllerTest < Test::Unit::TestCase
  def setup
    @controller = MessagesLogController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end

  # Replace this with your real tests.
  def test_truth
    assert true
  end
end
