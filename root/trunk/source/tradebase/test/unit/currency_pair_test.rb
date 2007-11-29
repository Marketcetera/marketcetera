require File.dirname(__FILE__) + '/../test_helper'

class CurrencyPairTest < Test::Unit::TestCase
  fixtures :currency_pairs
  fixtures :currencies

  # Replace this with your real tests.
  def test_to_s
    expected = "ZAI/USD"
    cp = CurrencyPair.get_currency_pair(expected, false)
    assert_not_nil cp
    assert_equal expected, cp.to_s
  end

  def test_validate
    usd = Currency.find_by_alpha_code("USD")

    cp = CurrencyPair.new(:first_currency => nil, :second_currency => usd)
    assert !cp.valid?

    cp = CurrencyPair.new(:first_currency => usd, :second_currency => nil)
    assert !cp.valid?

    cp = CurrencyPair.new()
    assert !cp.valid?

    cp = CurrencyPair.new(:first_currency => usd, :second_currency => usd)
    assert cp.valid?

  end

end
