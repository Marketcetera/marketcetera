require "#{File.dirname(__FILE__)}/../test_helper"
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'

class PnlTest < ActionController::IntegrationTest
  
  def setup 
    directory = File.join(File.dirname(__FILE__), "../fixtures/pnl") 
    Fixtures.create_fixtures(directory, [:trades, :m_symbols, :journals, :equities, :accounts, :sub_accounts, :postings, :marks]) 
  end
  
  def test_get_cashflows
    cashflows = CashFlow.get_cashflows_from_to_in_acct(Account.find_by_nickname("TOLI"), 
                                                       Date.new(2005, 1,1), Date.new(2007, 4,19))
    assert_equal 4, cashflows.length
    assert_equal ["GOOG", BigDecimal.new("-4782").to_s], [cashflows[0].symbol, cashflows[0].cashflow.to_s]
    assert_equal ["IBM", BigDecimal.new("1920.5").to_s], [cashflows[1].symbol, cashflows[1].cashflow.to_s]
    assert_equal ["MSFT", BigDecimal.new("-38").to_s], [cashflows[2].symbol, cashflows[2].cashflow.to_s]  
    assert_equal ["SUNW", BigDecimal.new("-674").to_s], [cashflows[3].symbol, cashflows[3].cashflow.to_s]  
  end
    

end