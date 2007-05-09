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
    Fixtures.create_fixtures(directory, [:trades, :m_symbols, :journals, :equities, :accounts, :sub_accounts,
                                         :sub_account_types, :postings, :marks]) 
  end

  # no mark for GOOG on 4/11/2007
  def test_by_account_no_mark_present
    get :report, { :account=>{:nickname=>"TOLI"}, :suffix => "acct",
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4", 
                                "from(3i)"=>"11", "to(3i)"=>"11"}}
    assert_response :success
    assert_template 'pnl_by_account'
    assert_has_error_notice                   
    assert_equal 0, assigns(:cashflows).length
  end

  def test_pnl_by_account_no_data_at_all
      Account.delete(Account.find_by_nickname('TOLI'))
      Account.delete(Account.find_by_nickname('GRAHAM'))
      Trade.delete_all
      Equity.delete_all
      MSymbol.delete_all
      Mark.delete_all
      Posting.delete_all
      Journal.delete_all

      get :report, { :account=>{:nickname=>""}, :suffix => "acct",
                         :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4",
                                  "from(3i)"=>"11", "to(3i)"=>"11"}}
      assert_response :success
      assert_template 'pnl_aggregate'
      assert_no_tag :tag => 'div', :attributes => { :id => "error_notice" }
      assert_equal 0, assigns(:cashflows).length
  end

  # should return unassigned account
  def test_unassigned_account
    get :report, { :suffix => "acct", :account => {:nickname => "[UNASSIGNED]"},
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4",
                                "from(3i)"=>"17", "to(3i)"=>"19"}}
    assert_response :success
    assert_template 'pnl_by_account'
    assert_not_nil assigns(:cashflows)
    # should find 3 P&Ls for unassigned
    assert_equal 3, assigns(:cashflows).length
    assert_equal "[UNASSIGNED]", assigns(:nickname)
    cfs = assigns(:cashflows)
    assert_equal [BigDecimal("0").to_s, "GOOG"], [cfs[0].cashflow.to_s, cfs[0].symbol]
    assert_equal [BigDecimal("0").to_s, "IBM"], [cfs[0].cashflow.to_s, cfs[1].symbol]
    assert_equal [BigDecimal("0").to_s, "MSFT"], [cfs[0].cashflow.to_s,  cfs[2].symbol]
  end

  def test_nonexistent_acct
    get :report, { :suffix => "acct", :account => {:nickname => "noSuchAcct"},
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4", 
                                "from(3i)"=>"17", "to(3i)"=>"19"}}
    assert_response :success
    assert_template 'index'
    assert_nil assigns(:cashflows)
    assert_has_error_box
    assert_not_nil assigns(:report).errors[:account]
  end
    
  def test_by_account
    get :report, { :suffix => "acct", :account => {:nickname => "TOLI"},
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4", 
                                "from(3i)"=>"17", "to(3i)"=>"19"}}
    assert_response :success
    assert_template 'pnl_by_account'
    assert_not_nil assigns(:cashflows)
    assert_equal "TOLI", assigns(:nickname)
    # should find 3 P&Ls for TOLI
    assert_equal 4, assigns(:cashflows).length
    cfs = assigns(:cashflows)
    assert_equal [BigDecimal("0").to_s, "GOOG"], [cfs[0].cashflow.to_s, cfs[0].symbol]
    assert_equal [BigDecimal("0").to_s, "IBM"], [cfs[0].cashflow.to_s, cfs[1].symbol]
    assert_equal [BigDecimal("0").to_s, "MSFT"], [cfs[0].cashflow.to_s,  cfs[2].symbol]
    assert_equal [BigDecimal("0").to_s, "SUNW"], [cfs[0].cashflow.to_s,  cfs[3].symbol]
  end
  
  def test_invalid_from_to_by_acct
    get :report, { :suffix => "acct",
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4", 
                                "from(3i)"=>"31", "to(3i)"=>"31"}}
    assert_response :success
    assert_template 'index'
    assert_has_error_box
    assert_equal 2, assigns(:report).errors.length
    assert_not_nil assigns(:report).errors[:from_date]
    assert_not_nil assigns(:report).errors[:to_date]
  end
  
  def test_invalid_from_to_by_dates
    get :report, { :suffix => "acct",
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4", 
                                "from(3i)"=>"31", "to(3i)"=>"31"}}
    assert_response :success
    assert_template 'index'
    assert_has_error_box
    assert_equal 2, assigns(:report).errors.length
    assert_not_nil assigns(:report).errors[:from_date]
    assert_not_nil assigns(:report).errors[:to_date]
  end

  # should find 3 cashflows across all 3 accounts
  def test_aggregate_across_all_accts
    get :report, { :suffix => "acct",
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4", 
                                "from(3i)"=>"17", "to(3i)"=>"19"}}
    assert_response :success
    assert_template 'pnl_aggregate'
    assert_equal 0, assigns(:report).errors.length

    cfs = assigns(:cashflows)
    assert_equal 3, cfs.length
    assert_equal ["GRAHAM", BigDecimal("44485.12")], [cfs[0][:account], cfs[0][:cashflow]]
    assert_equal ["TOLI", BigDecimal("0")], [cfs[1][:account], cfs[1][:cashflow]]
    assert_equal ["[UNASSIGNED]", BigDecimal("0")], [cfs[2][:account], cfs[2][:cashflow]] 
  end

  def test_aggregate_missing_mark
    get :report, { :suffix => "acct",
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4",
                                "from(3i)"=>"17", "to(3i)"=>"20"}}
    assert_response :success
    assert_template 'pnl_aggregate'
    assert_has_error_notice
  end
end
