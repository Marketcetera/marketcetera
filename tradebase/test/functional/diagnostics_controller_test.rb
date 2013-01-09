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
    assert :template => 'index'
    assert :success
  end

  # this test is brittle - it requires the outside xmlrpc server to be up
  # which may not always be the case
#  def test_server_info_nonworking_ors
#    get :server_info
#    assert :template => 'server_info'
#    assert :success
#
#    assert_tag :tag => 'p', :content => /error connecting to the ORS/
#  end
#
  def test_ors_log_display
    get :ors_log_display
    assert :template => 'ors_log_display'
    assert :success
  end

  def test_tradebase_log_display
    get :tradebase_log_display
    assert :template => 'tradebase_log_display'
    assert :success
  end

end
