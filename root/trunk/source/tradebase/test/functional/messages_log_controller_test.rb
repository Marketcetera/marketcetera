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
    assert_equal 7, assigns("exec_report_pages").length
    assert_has_show_edit_delete_links(true, false, false)
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
end
