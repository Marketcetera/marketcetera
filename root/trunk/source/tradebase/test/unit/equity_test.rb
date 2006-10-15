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
end
