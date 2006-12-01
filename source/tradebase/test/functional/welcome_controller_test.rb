require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'welcome_controller'

# Re-raise errors caught by the controller.
class WelcomeController; def rescue_action(e) raise e end; end

class WelcomeControllerTest < MarketceteraTestBase
  def setup
    @controller = WelcomeController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
  end

  def test_index
    get :index
    assert_response :success
    assert_template 'welcome'
  end

  def test_welcome
    get :welcome
    assert_response :success
    assert_template 'welcome'
    assert_equal 0, assigns(:num_trades_today)
    assert_equal 0, assigns(:num_positions)
    assert_equal 0, assigns(:top_positioned_accounts).length
  end
  
  def test_values_assigned
    for i in 1..10
      create_test_trade(100, 20.11, Side::QF_SIDE_CODE[:buy], "TOLI", Date.civil(2006, 7,11), "IFLI-"+i.to_s, 4.99, "USD")
    end
    create_test_trade(100, 20.11, Side::QF_SIDE_CODE[:buy], "bob", Date.today, "GOOG", 4.99, "USD")
    
    get :index
    assert_response :success
    assert_equal 10, assigns(:num_positions)
    assert_equal 1, assigns(:num_trades_today)
    
    # verify top accounts
    assert_equal 2, assigns(:top_positioned_accounts).length
    assert_equal ["TOLI", 10], [assigns(:top_positioned_accounts)[0][0].nickname, assigns(:top_positioned_accounts)[0][1]]
    assert_equal ["bob", 1], [assigns(:top_positioned_accounts)[1][0].nickname, assigns(:top_positioned_accounts)[1][1]]
  end
end
