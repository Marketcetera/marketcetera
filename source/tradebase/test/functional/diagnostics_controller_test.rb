require File.dirname(__FILE__) + '/../test_helper'
require 'diagnostics_controller'

# Re-raise errors caught by the controller.
class DiagnosticsController; def rescue_action(e) raise e end; end

class DiagnosticsControllerTest < Test::Unit::TestCase
  def setup
    @controller = DiagnosticsController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end

  # Replace this with your real tests.
  def test_index
    get :index
    assert :template => 'server_info'
  end

  def test_server_info_nonworking_oms
    get :server_info
    assert :template => 'server_info'
    assert :success

    assert_tag :tag => 'p', :content => /error connecting to the OMS/
  end
end
