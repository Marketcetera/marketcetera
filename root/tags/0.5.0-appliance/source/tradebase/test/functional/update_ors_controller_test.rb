require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'update_ors_controller'

# Re-raise errors caught by the controller.
class UpdateOrsController; def rescue_action(e) raise e end; end

class UpdateOrsControllerTest < MarketceteraTestBase
  def setup
    @controller = UpdateOrsController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end

  # Replace this with your real tests.
  def test_empty_sender
    post :update_sender, {}

    assert_template 'index'
    assert_not_nil assigns(:report)
    assert_not_nil assigns(:report).errors[:sender_id]
  end
end
