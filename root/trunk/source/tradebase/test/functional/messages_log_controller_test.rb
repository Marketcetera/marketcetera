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

  def test_list
    get :list

    assert_response :success
    assert_template 'list'

    assert_not_nil assigns("exec_report_pages")
    assert_equal 8, assigns("exec_report_pages").length
    assert_has_show_edit_delete_links(true, false, false)
    assert_nil flash[:error]
  end

  # make sure the symbol/account columns have spaces substituted for &nbsp;
  def test_list_formatting
    get :list

    assert_response :success
    assert_template 'list'

    # verify the formatting for the 'goog_long_22' message
    assert_tag :tag => "td", :content => "GOOG&nbsp;Long&nbsp;Equity"   # symbol
    assert_tag :tag => "td", :content => "TOLI&nbsp;...paces"
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
    # there are 2 messages that  are done on Sept 23-24, shoudl find just these 2
    get :list, {:dates => { "start_date(1i)"=>"2006", "start_date(2i)"=>"9", "start_date(3i)"=>"16", 
                            "end_date(1i)"=>"2006", "end_date(2i)"=>"9", "end_date(3i)"=>"27"}, "search_type"=>"s"}
    assert_response :success
    assert_template 'list'
    assert_equal 2, assigns(:exec_report_pages).length
  end

  # test if the account is empty we still display an &nbsp
  # this is essentially a test for the display_heper::df function, but since
  # the h() function is not loaded in unit test we can't test it directly.'
  def test_empty_account_name_results_in_space
      # get goog_20 message
      get :show, :id => 20

      assert_response :success
      assert_template 'show'

      assert_tag :tag => "div", :attributes => { :id => "account", :class => "data view_data" }, :content => "&nbsp;"
  end
  
  # verify that unparseable messages are handled correctly 
  def test_unparseable_msg
      badMsg = "8=FIX.4.2\0019=378\00135=9\00134=18\00149=MRKTC-EXCH\00152=20070404-23:16:21.087\00156=sender-1308-OMS" +
      "\00111=\00137=\00139=8\00141=\00158=Could not find field OrderID in message.: 8=FIX.4.2\0019=190\00135=F\00134=23" +
      "\00149=sender-1308-OMS\00152=20070404-23:16:21.077\00156=MRKTC-EXCH\00111=367792e9-706e-4692-af9e-841be0f1a4eb\00122=1" +
      "\00141=26a0d030-e01a-4493-ac50-7e9da6bb2390\00148=2\00154=2\00155=a\00160=20070404-15:15:51\00110=021\001\001"+
      "60=20070404-23:16:21\001102=1\001434=1\00110=002\001"
    
    m = MessageLog.create(:text => badMsg, :time => Date.today)
    get :list

    assert_response :success
    assert_template 'list'

    assert_not_nil assigns("exec_report_pages")
    assert_equal 8, assigns("exec_report_pages").length
    assert_not_nil assigns("failed_msg")
    assert_equal 1, assigns("failed_msg").length
    assert flash[:error] = "Failed to parse 1 message(s)."
    
    # verify the row with unparseable message shows up
    assert_tag :tag => "td", :attributes => { :colspan => "12"}
    
    # now test that unparseable message shows up
    get :show_unparseable_msg, :id => m.id
    assert_response :success
    assert_template 'show_unparseable_msg'
  end
  
end
