require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class SubAccountTest < MarketceteraTestBase
  fixtures :sub_accounts, :accounts
    
  def test_num_descriptions_same_as_preloaded
    assert SubAccountType.count > 0, "doesn't have any sub_account types"
    assert_not_nil SubAccountType.preloaded
    assert_equal SubAccountType::DESCRIPTIONS.length, SubAccountType.preloaded.length, "PRELOADED array of SATs not populated"
    assert_equal SubAccountType::DESCRIPTIONS.length, SubAccountType.count
  end
  
  def test_types_not_empty
    assert_equal SubAccountType::DESCRIPTIONS.length, SubAccountType.preloaded.length
    assert SubAccountType::DESCRIPTIONS.length > 0
    SubAccountType::DESCRIPTIONS.each { |sa| assert_not_nil sa }
  end
  
end
