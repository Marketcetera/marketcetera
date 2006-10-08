require File.dirname(__FILE__) + '/../test_helper'

class CurrencyTest < Test::Unit::TestCase
  fixtures :currencies

  def test_lookup
    assert_equal 2, Currency.count
    c = Currency.find(1)
    assert c
    assert_equal currencies(:usd).alpha_code, c.alpha_code
  end
end
