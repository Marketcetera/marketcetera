require File.dirname(__FILE__) + '/../test_helper'

class DividendTest < Test::Unit::TestCase
  fixtures :currencies, :m_symbols, :equities, :dividends
  include DividendsHelper

  def setup
    @USD = currencies(:usd)
    @ifli = Equity.get_equity("ifli")
  end 
  
  def test_modification_existing
    div = Dividend.find(1)
    oldDesc = div.description
    div.description = "new desc"
    assert div.save
    div.reload
    assert_equal "new desc", div.description
    assert_not_equal oldDesc, div.description
  end
  
  def test_negative_amount_validation
    div = Dividend.new
    div.equity = @ifli
    div.amount = -20
    div.description = "negative"
    div.currency = @USD
    
    assert_equal 0, div.errors.length
    div.save  
    assert_equal 1, div.errors.length, "accepted a negative amount"
  end
  
  def test_validation
    div = Dividend.new
    assert !div.valid?
    assert_equal 3, div.errors.length, "doesn't contain amount and symbol and currency validation"
    assert_not_nil div.errors[:symbol]
    assert_not_nil div.errors[:amount]
    assert_not_nil div.errors[:currency]
    
    div.equity = @ifli
    assert !div.valid?
    assert_equal 2, div.errors.length
    assert_nil div.errors[:symbol]
    assert_not_nil div.errors[:amount]
    assert_not_nil div.errors[:currency]
    
    div.amount = 23
    assert !div.valid?
    assert_equal 1, div.errors.length
    assert_not_nil div.errors[:currency]

    div.currency = Currency.find_by_alpha_code("USD")
    assert div.valid?
    assert_nil div.errors[:currency]
  end
  
  def test_symbol_presense_validation
    div = Dividend.new
    div.equity = Equity.new
    assert !div.valid?
    assert_not_nil div.errors[:symbol]
    
    # test validation fails on blank equity root
    blankE = Equity.get_equity('')
    div.equity = blankE
    assert !div.valid?
    assert_not_nil div.errors[:symbol]
  end
  
  def test_numericality_validation
    div = Dividend.new
    div.equity = @ifli
    div.amount = "abcd"
    div.description = "non-numeric"
    div.currency = @USD
    
    div.save
    assert_equal 1, div.errors.length, "accepted non-numeric"
  end
  
  def test_numericality_validation_string_number
    div = Dividend.new
    div.equity = @ifli
    div.amount = "20"
    div.description = "string"
    div.currency = @USD
    
    div.save
    assert_equal 0, div.errors.length, "didn't accept string number"
  end
  
  def test_zero_amount
    div = Dividend.new
    div.equity = @ifli
    div.amount = 0
    div.description = "zero"
    div.currency = @USD
    
    div.save
    assert_equal 0, div.errors.length, "didn't accept zero"
  end
  def test_non_integer
    div = Dividend.new()
    div.equity = @ifli
    div.amount = 23.42
    div.description = "non-integer"
    div.currency = @USD
    
    div.save
    assert_equal 0, div.errors.length, "didn't accept non-integer"
  end
  
  def test_human_dividend_status
    assert_equal "Announced", get_human_dividend_status(DividendStatusAnnounced)
    
    assert_match "Unknown", get_human_dividend_status("bogus")
  end 
  
  def test_equity_m_symbol_root
    div = Dividend.find(:first)
    assert_not_nil div.equity_m_symbol_root
    assert_equal div.equity.m_symbol.root, div.equity_m_symbol_root
    
    # null
    div = Dividend.new
    assert_nil div.equity_m_symbol_root 
    
    div.equity = Equity.new
    assert_nil div.equity_m_symbol_root 
    
    div.equity = @ifli
    assert_not_nil div.equity_m_symbol_root
    
    @ifli.m_symbol = nil
    assert_nil div.equity_m_symbol_root 
  end
  
  def test_currency_alpha_code
    d = Dividend.new
    d.equity = @ifli
    d.currency = @USD
    assert_equal @USD.alpha_code, d.currency_alpha_code
    
    d.currency = currencies(:GPB)
    assert_equal currencies(:GPB).alpha_code, d.currency_alpha_code
  end
  
end
