require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'id_repository_controller'

# Re-raise errors caught by the controller.
class IdRepositoryController; def rescue_action(e) raise e end; end

class IdRepositoryControllerTest < MarketceteraTestBase
include IdRepositoryHelper

  def setup
    @controller = IdRepositoryController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end

  # Replace this with your real tests.
  def test_get_next_batch
    post :get_next_batch
    
    assert_response :success
    assert_template 'id_repository/get_next_batch'
    
    assert_not_nil assigns(:current)
    assert_not_nil assigns(:num_allowed)
    assert_equal NumAllowed, assigns(:num_allowed)
    assert assigns(:current) > 0
    firstRun = assigns(:current)
    
    # get again
    get :get_next_batch
    assert firstRun + NumAllowed, assigns(:current)
    
    assert_tag :tag => "id", :child => { :tag => "next"}
    assert_tag :tag => "id", :child => { :tag => "num"}
    
    assert_tag :tag => "next", :content => "#{assigns(:current)}"
    assert_tag :tag => "num", :content => "#{assigns(:num_allowed)}"
    
  end
end
