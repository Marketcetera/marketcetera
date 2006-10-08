require File.dirname(__FILE__) + '/../test_helper'

class DividendTest < Test::Unit::TestCase
  fixtures :currencies, :dividends

  def setup
    @USD = currencies(:usd)
  end 
  
  def test_modification_existing
    div = Dividend.find(1)
    oldDesc = div.description
    div.description = "new desc"
    div.save
    div.reload
    assert_not_equal oldDesc, div.description
    assert_equal "new desc", div.description
  end
  
  def test_negative_amount_validation
    div = Dividend.new
    div.amount = -20
    div.description = "negative"
    div.currency = @USD
    
    assert_equal 0, div.errors.length
    div.save  
    assert_equal 1, div.errors.length, "accepted a negative amount"
  end
  
  def test_numericality_validation
    div = Dividend.new
    div.amount = "abcd"
    div.description = "non-numeric"
    div.currency = @USD
    
    div.save
    assert_equal 1, div.errors.length, "accepted non-numeric"
  end
  
  def test_numericality_validation_string_number
    div = Dividend.new
    div.amount = "20"
    div.description = "string"
    div.currency = @USD
    
    div.save
    assert_equal 0, div.errors.length, "didn't accepted string number"
  end
  
  def test_zero_amount
    div = Dividend.new
    div.amount = 0
    div.description = "zero"
    div.currency = @USD
    
    div.save
    assert_equal 0, div.errors.length, "didn't accepted zero"
  end
  def test_non_integer
    div = Dividend.new
    div.amount = 23.42
    div.description = "non-integer"
    div.currency = @USD
    
    div.save
    assert_equal 0, div.errors.length, "didn't accepted non-integer"
  end
end
