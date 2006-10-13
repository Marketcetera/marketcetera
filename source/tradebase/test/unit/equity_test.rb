require File.dirname(__FILE__) + '/../test_helper'

class EquityTest < Test::Unit::TestCase
  fixtures :equities

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
end
