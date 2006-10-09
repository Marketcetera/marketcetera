require File.dirname(__FILE__) + '/../test_helper'

class ApplicationHelperTest < Test::Unit::TestCase
  fixtures :currencies
  include ApplicationHelper

  # Replace this with your real tests.
  def test_get_currency
    assert_equal currencies(:usd).alpha_code, get_currency(nil).alpha_code
    assert_equal currencies(:usd).alpha_code, get_currency('').alpha_code
    assert_equal currencies(:usd).alpha_code, get_currency("USD").alpha_code
    assert_equal currencies(:usd).alpha_code, get_currency('usd').alpha_code
    
    assert_not_nil get_currency("GBP")
    assert_nil get_currency("DNE")
  end
end
