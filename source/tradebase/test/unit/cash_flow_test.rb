require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class CashFlowTest < MarketceteraTestBase
  
  def setup 
    directory = File.join(File.dirname(__FILE__), "../fixtures/pnl") 
    Fixtures.create_fixtures(directory, [:trades, :m_symbols, :journals, :equities, :accounts, :sub_accounts, :postings, :marks]) 
    @googEq = Equity.get_equity("GOOG")
    @toliAcct = Account.find_by_nickname("TOLI")
  end
  
  def test_to_s
    cf = CashFlow.new(BigDecimal("22.33"), "XYZ", "Goldman", nil)
    assert_equal "22.33 for XYZ in Goldman", cf.to_s
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
  
  
end