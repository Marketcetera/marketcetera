require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class CashFlowTest < MarketceteraTestBase
  
  def setup 
    directory = File.join(File.dirname(__FILE__), "../fixtures/pnl") 
    Fixtures.create_fixtures(directory, [:trades, :m_symbols, :journals, :equities, :accounts, :sub_accounts,
                                         :sub_account_types, :postings, :marks]) 
    @googEq = Equity.get_equity("GOOG")
    @toliAcct = Account.find_by_nickname("TOLI")
  end
  
  def test_to_s
    cf = CashFlow.new(BigDecimal("22.33"), "XYZ", "Goldman", nil)
    assert_equal "22.33 for XYZ in account [Goldman]", cf.to_s
  end
  
  def test_get_synthetic_cashflow_exception_thrown_nonexisting_mark
    assert_exception(Exception, "Please enter a mark for GOOG on 2007-04-20.") do
       CashFlow.get_synthetic_cashflow(Date.new(2007, 4, 20), @toliAcct, @googEq, "GOOG")
    end
  end
  
  def test_get_synthetic_cashflow_no_pos
    assert_equal 0, CashFlow.get_synthetic_cashflow(Date.new(2001, 4, 20), @toliAcct, @googEq, "GOOG")
  end
  
  def test_get_synthetic_cashflow_with_pos
    assert_nums_equal -45600, CashFlow.get_synthetic_cashflow(Date.new(2007, 4, 17), @toliAcct, @googEq, "GOOG")
  end
  
  # verify that 0 p&ls show up
  def _test_get_cashflows_from_to_in_acct_zero_pnl_shows_up
    cfs = CashFlow.get_cashflows_from_to_in_acct(Account.find_by_nickname(''), Date.new(2007, 4, 17), Date.new(2007,4,19))
    assert_equal 1, cfs.length
    cfs = cfs["[UNASSIGNED]"]
    assert_equal ["GOOG", "IBM", "MSFT"], cfs.keys
    assert_nums_equal 0, cfs["GOOG"]
    assert_nums_equal 0, cfs["IBM"]
    assert_nums_equal 0, cfs["MSFT"]
  end

  def _test_get_cashflows_from_to_in_acct
    cfs = CashFlow.get_cashflows_from_to_in_acct(Account.find_by_nickname('TOLI'), Date.new(2005, 1, 1), Date.new(2007,4,19))["TOLI"]
    assert_equal 4, cfs.keys.length
    assert_nums_equal 40818,  cfs["GOOG"]
    assert_nums_equal -17859.50,  cfs["IBM"]
    assert_nums_equal 4562,  cfs["MSFT"]
    assert_nums_equal -1709,  cfs["SUNW"]
  end  

  # should get 3 p&l sets, one for each account
  # should get 0 P&L for sunw/goog/msft in toli/unassigned, and 1 p&l for graham in FRO
  def test_get_cashflows_from_to_in_acct_unspecified_acct
    assert_not_nil SubAccountType.CASH
    cfs = CashFlow.get_cashflows_from_to_in_acct(nil, Date.new(2007, 4, 17), Date.new(2007,4,19))
    assert_not_nil Account.find_by_nickname("GRAHAM")
    assert_equal 3, cfs.length
    assert_equal 4, cfs["TOLI"].length
    assert_equal ["GOOG", "IBM", "MSFT", "SUNW"], cfs["TOLI"].values.collect {|cf| cf.symbol}.sort
    assert_equal 3, cfs["[UNASSIGNED]"].length
    assert_equal ["GOOG", "IBM", "MSFT"], cfs["[UNASSIGNED]"].values.collect {|cf| cf.symbol}.sort
    assert_equal 1, cfs["GRAHAM"].length
    assert_equal ["FRO"], cfs["GRAHAM"].values.collect {|cf| cf.symbol}.sort
  end
end