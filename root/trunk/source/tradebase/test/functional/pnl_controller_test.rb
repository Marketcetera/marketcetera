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

  # Marks on 4/25/2006 and 4/26/2006
  # Txn date: B 100 IBM 70.73 on 5/8/2006
  # Do P&L from 1/1/2006 to 5/8/2006
  # Should get an error calculating P&L since don't have a mark on 5/8/2006
  # this is bug #231
  def test_pnl_by_account_mark_missing_on_txn_date
    e = Equity.get_equity("IBM", true)
    m1 = Mark.new(:mark_value => BigDecimal.new("100"), :mark_date => Date.civil(2006,4,25))
    m2 = Mark.new(:mark_value => BigDecimal.new("101"), :mark_date => Date.civil(2006,4,26))
    t = create_test_trade(100, 77.30, Side::QF_SIDE_CODE[:buy], "BOB", Date.civil(2006, 5, 8), "IBM", "4.53", "ZAI")
    a = Account.new(:nickname => "BOB")

    get :report, { :account=>{:nickname=>"BOB"}, :suffix => "acct",
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"5", "from(2i)"=>"1",
                                "from(3i)"=>"1", "to(3i)"=>"8"}}
    assert_response :success
    assert_template 'pnl_by_account'
    assert_has_error_notice
    assert_equal 0, assigns(:cashflows).length
    assert_has_error_notice("Error generating cashflow for BOB: Please enter a mark for IBM on 2007-05-08.")
  end

  # same as above, but we are already missing some marks for FRO for example
  def test_pnl_by_account_mark_missing_on_txn_date_aggregate
    get :report, { :suffix => "acct",
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"5", "from(2i)"=>"1",
                                "from(3i)"=>"1", "to(3i)"=>"8"}}
    assert_response :success
    assert_template 'missing_marks'
    assert_has_error_notice
    assert_equal 5, assigns(:missing_marks).length
    assert_has_error_notice("Unable to calculate P&L because some marks are missing.")
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
      assert_equal 0, assigns(:pnls).length
  end

  # should return unassigned account
  def test_unassigned_account
    get :report, { :suffix => "acct", :account => {:nickname => "[UNASSIGNED]"},
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4",
                                "from(3i)"=>"17", "to(3i)"=>"19"}}
    assert_response :success
    assert_template 'pnl_by_account'
    assert_not_nil assigns(:pnls)
    # should find 3 P&Ls for unassigned
    assert_equal 3, assigns(:pnls).length
    assert_equal "[UNASSIGNED]", assigns(:nickname)
    cfs = assigns(:pnls)
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

    pnls = assigns(:pnls)
    assert_equal 3, pnls.length
    assert_equal "[UNASSIGNED]",pnls[0].account.nickname 
    assert_nums_equal 0, pnls[0].profit_and_loss.to_s
    assert_equal "TOLI",pnls[1].account.nickname 
    assert_nums_equal -669.0, pnls[1].profit_and_loss.to_s
    assert_equal "GRAHAM",pnls[2].account.nickname 
    assert_nums_equal 285.12, pnls[2].profit_and_loss.to_s
  end

  def test_aggregate_missing_mark
    get :report, { :suffix => "acct",
                       :date_acct=>{"to(1i)"=>"2007", "from(1i)"=>"2007", "to(2i)"=>"4", "from(2i)"=>"4",
                                "from(3i)"=>"17", "to(3i)"=>"20"}}
    assert_response :success
    assert_template 'missing_marks'
    assert_has_error_notice
  end
end
