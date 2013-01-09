require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'message_logs_controller'

# Re-raise errors caught by the controller.
class MessagesLogController; def rescue_action(e) raise e end; end

class MessagesLogControllerTest < MarketceteraTestBase
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

  # verify that unparseable messages are handled correctly
  def test_unparseable_msg
      badMsg = "8=FIX.4.2\0019=378\00135=9\00134=18\00149=MRKTC-EXCH\00152=20070404-23:16:21.087\00156=sender-1308-ORS" +
      "\00111=\00137=\00139=8\00141=\00158=Could not find field OrderID in message.: 8=FIX.4.2\0019=190\00135=F\00134=23" +
      "\00149=sender-1308-ORS\00152=20070404-23:16:21.077\00156=MRKTC-EXCH\00111=367792e9-706e-4692-af9e-841be0f1a4eb\00122=1" +
      "\00141=26a0d030-e01a-4493-ac50-7e9da6bb2390\00148=2\00154=2\00155=a\00160=20070404-15:15:51\00110=021\001\001"+
      "60=20070404-23:16:21\001102=1\001434=1\00110=002\001"
    
    m = MessageLog.create(:text => badMsg, :time => Date.today)
    get :list

    assert_response :success
    assert_template 'list'

    assert_not_nil assigns(:exec_report_pages)
    assert_not_nil assigns(:failed_msg)
    assert_equal 3, assigns(:failed_msg).length
    
    # now test that unparseable message shows up
    get :show_unparseable_msg, :id => m.id
    assert_response :success
    assert_template 'show_unparseable_msg'
  end
  
end
