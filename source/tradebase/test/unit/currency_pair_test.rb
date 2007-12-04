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

  def test_get_currency_pair
    assert_not_nil Currency.get_currency("USD")
    assert_not_nil Currency.get_currency("ZAI")
    assert_not_nil CurrencyPair.get_currency_pair("ZAIUSD", false)
    assert_not_nil CurrencyPair.get_currency_pair("ZAI/USD", false)

    assert_raise(UnknownCurrencyPairException) { CurrencyPair.get_currency_pair("unparseable")}

    assert_nil CurrencyPair.get_currency_pair("XYZ/BSD", false)
    assert_raise(UnknownCurrencyPairException) { CurrencyPair.get_currency_pair("XYZ/BSD", true)}

    assert_nil CurrencyPair.get_currency_pair("USD/ZAI", false)
    usd_zai = CurrencyPair.get_currency_pair("USD/ZAI", true)
    assert_not_nil usd_zai
    assert_equal "USD", usd_zai.first_currency.alpha_code
    assert_equal "ZAI", usd_zai.second_currency.alpha_code
  end

  def test_get_currency_pair_nil
    assert_raises (UnknownCurrencyPairException) { assert_nil CurrencyPair.get_currency_pair(nil) }  
  end

  def test_get_currency_pair_lowercase
    assert_not_nil CurrencyPair.get_currency_pair("zaiusd", false)
    assert_not_nil CurrencyPair.get_currency_pair("zaIUsd", false)
    assert_not_nil CurrencyPair.get_currency_pair("zai/USD", false)
    assert_not_nil CurrencyPair.get_currency_pair("ZAI/usd", false)
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
