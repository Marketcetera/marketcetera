require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require 'pnl_controller'

# Re-raise errors caught by the controller.
class PnlController; def rescue_action(e) raise e end; end

class PnlControllerTest < MarketceteraTestBase

  def setup
    @controller = PnlController.new
    @request    = ActionController::TestRequest.new
    @response   = ActionController::TestResponse.new
    directory = File.join(File.dirname(__FILE__), "../fixtures/pnl") 
    Fixtures.create_fixtures(directory, [:trades, :m_symbols, :journals, :equities, :accounts, :sub_accounts, :postings, :marks]) 
  end

  # no mark for GOOG on 4/11/2007
  def test_by_account_no_mark_present
    get :by_account, { :account=>{:nickname=>"TOLI"}, 
                       :date=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4", 
                                "from(3i)"=>"11", "to(3i)"=>"11"}}
    assert_response :success
    assert_template 'pnl_output'
    assert_has_error_notice                                
  end
end
