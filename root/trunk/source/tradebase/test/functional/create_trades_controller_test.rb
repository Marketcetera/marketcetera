require File.dirname(__FILE__) + '/../test_helper'
require 'create_trades_controller'

# Re-raise errors caught by the controller.
class CreateTradesController; def rescue_action(e) raise e end; end

class CreateTradesControllerTest < Test::Unit::TestCase
  def setup
    @controller = CreateTradesController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end

  # Replace this with your real tests.
  def test_truth
    assert true
  end
end
