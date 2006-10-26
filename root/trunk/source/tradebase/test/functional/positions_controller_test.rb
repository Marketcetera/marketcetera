require File.dirname(__FILE__) + '/../test_helper'
require 'positions_controller'

# Re-raise errors caught by the controller.
class PositionsController; def rescue_action(e) raise e end; end

class PositionsControllerTest < Test::Unit::TestCase
  def setup
    @controller = PositionsController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end

  # Replace this with your real tests.
  def test_truth
    assert true
  end
end
