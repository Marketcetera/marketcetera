require File.dirname(__FILE__) + '/../test_helper'

class SubAccountTypeTest < Test::Unit::TestCase
  fixtures :sub_account_types

  # Replace this with your real tests.
  def test_preloaded
    assert_not_nil SubAccountType.preloaded
    assert_equal SubAccountType::DESCRIPTIONS.length, SubAccountType.preloaded.length
  end

  # the queries in profit_and_loss_test depend on this assumption
  def test_cash
    assert_equal 2, SubAccountType.find_by_description("Cash").id
  end
end
