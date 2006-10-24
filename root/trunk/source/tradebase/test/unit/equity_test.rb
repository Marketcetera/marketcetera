require File.dirname(__FILE__) + '/../test_helper'

class EquityTest < Test::Unit::TestCase
  fixtures :m_symbols, :equities

  # Replace this with your real tests.
  def test_lookup
    eq = Equity.get_equity("BEER")
    assert_not_nil eq
    assert_equal "BEER", eq.m_symbol.root
  end
  
  # first verify underlying m_symbol does not exist
  # then lookup equity, verify the m_symbol also created
  def test_lookup_and_creation
    assert_nil MSymbol.find_by_root("vodka")
    eq = Equity.get_equity("vodka")
    assert_not_nil eq
    assert_equal "vodka", eq.m_symbol.root
  end
  
  def test_lookup_of_preloaded
    preloaded = Equity.find(2) # goog
    assert_equal "GOOG", preloaded.m_symbol.root
    assert_equal "google", preloaded.description
    eq = Equity.get_equity("GOOG")
    assert_equal "google", eq.description
    assert_equal "GOOG", eq.m_symbol.root    
  end
  
  # Currently, we don't actually save the equity yet 
  # after the method call. should keep this assumption around
  # to make sure it breaks if we change the assumption and 
  # then we need to either delete this test, or rethink the assumption
  def test_assumption_equity_not_saved_after_call
    eq = Equity.get_equity("mifli")
    assert_not_nil eq, "equity is nil"
    assert_nil eq.id, "unsaved equity already has id"
    assert_not_nil eq.m_symbol, "equity doesn't have symbol"
    assert_nil eq.m_symbol.id, "unsaved equity already has symbol id" 
  end
  
  def test_to_s
    preloaded = Equity.find(2) # goog
    assert_equal "[GOOG Equity]", preloaded.to_s   
  end
end
