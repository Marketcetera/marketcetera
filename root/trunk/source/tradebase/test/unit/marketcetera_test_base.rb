require File.dirname(__FILE__) + '/../test_helper'

class MarketceteraTestBase < Test::Unit::TestCase
  fixtures :currencies, :sub_account_types
  
  def logger
    Logger.new(STDOUT)
  end  
  
  def test_default
    SubAccountType.new
    assert_equal SubAccountType.find_all.length, 7
    assert_equal SubAccountType::DESCRIPTIONS.length, SubAccountType::PRELOADED.length
    assert SubAccountType::PRELOADED.length > 0
  end
  
end