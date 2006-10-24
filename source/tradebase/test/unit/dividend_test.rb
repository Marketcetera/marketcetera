require File.dirname(__FILE__) + '/../test_helper'

class DividendTest < Test::Unit::TestCase
  fixtures :currencies, :dividends, :equities
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
    assert_equal 2, div.errors.length, "doesn't contain amount and symbol validation"
    assert_not_nil div.errors[:symbol]
    
    div.equity = @ifli
    assert !div.valid?
    assert_equal 1, div.errors.length
    assert_nil div.errors[:symbol]
    
    div.amount = 23
    assert div.valid?
  
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
  
end
