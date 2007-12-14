require File.dirname(__FILE__) + '/../test_helper'

class CurrencyTest < Test::Unit::TestCase
  fixtures :currencies

  def test_lookup
    assert_equal 4, Currency.count
    c = Currency.find(154)
    assert c
    assert_equal currencies(:usd).alpha_code, c.alpha_code
  end
  
  def test_get_currency
    assert_equal currencies(:usd).alpha_code, Currency.get_currency(nil).alpha_code
    assert_equal currencies(:usd).alpha_code, Currency.get_currency('').alpha_code
    assert_equal currencies(:usd).alpha_code, Currency.get_currency("USD").alpha_code
    assert_equal currencies(:usd).alpha_code, Currency.get_currency('usd').alpha_code
    
    assert_not_nil Currency.get_currency("GBP")
    assert_nil Currency.get_currency("DNE")
  end

  def test_to_s
    assert_equal "USD", currencies(:usd).to_s
  end
end
