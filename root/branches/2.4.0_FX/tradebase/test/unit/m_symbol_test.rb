require File.dirname(__FILE__) + '/../test_helper'

class MSymbolTest < Test::Unit::TestCase
  fixtures :m_symbols

  # Replace this with your real tests.
  def test_validation
    symbol = MSymbol.new()
    assert !symbol.valid?
    
    symbol = MSymbol.new(:isin => "bob")
    assert !symbol.valid?
    
    symbol = MSymbol.new(:root => "")
    assert !symbol.valid?
    
    symbol = MSymbol.new(:root => "bob")
    assert symbol.valid?
  end
end
