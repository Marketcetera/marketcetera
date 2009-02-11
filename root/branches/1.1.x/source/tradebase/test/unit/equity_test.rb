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
  
  def test_get_equity
    assert_nil MSymbol.find_by_root("dne_1")
    assert_not_nil Equity.get_equity("dne-1")
    
    assert_nil MSymbol.find_by_root("dne-2")
    assert_nil Equity.get_equity("dne-2", false), "equity/msymbol still created even though told not to"
  end
  
  def test_to_s
    preloaded = Equity.find(2) # goog
    assert_equal "[GOOG Equity]", preloaded.to_s   
  end
  
  def test_m_symbol_root
    e = Equity.new
    assert_nil e.m_symbol_root
    
    e = equities(:SUNW)
    assert_equal "SUNW", e.m_symbol_root
  end
  
  def test_validate
    assert !Equity.new.valid?
    
    assert equities(:SUNW).valid?
    
    eq = Equity.new
    eq.m_symbol = MSymbol.new
    assert !eq.valid?
    assert_not_nil eq.errors[:m_symbol_id], "accepted equity with m_symbol with nonexisting root"
  end
  
  def test_validate_duplicates
    ifli = Equity.get_equity("ifli")
    assert ifli.save
    
    dupe = Equity.new(:description => "dupe")
    dupe.m_symbol = ifli.m_symbol
    assert !dupe.valid?
    assert !dupe.save
    assert_not_nil dupe.errors[:m_symbol_id], "should flag as duplicate"
  end
  
  # Use case: the mSymbol exists, but the equity does not. should create a new equity pointing to msymbol
  def test_get_equity_dangling_symbol
    assert_nil MSymbol.find_by_root("IFLI")
    ifli = MSymbol.create(:root => "IFLI")
    
    eq = Equity.get_equity("IFLI")
    assert_not_nil eq
    assert_equal ifli, eq.m_symbol
  end
  
end
