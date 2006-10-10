require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class SubAccountTest < MarketceteraTestBase
  fixtures :sub_accounts, :accounts
  include SubAccountsHelper
  
  def test_num_descriptions_same_as_preloaded
    assert SubAccountType.find_all.length > 0, "doesn't have any sub_account types"
    assert_not_nil SubAccountType::PRELOADED
    assert_equal SubAccountType::DESCRIPTIONS.length, SubAccountType::PRELOADED.length, "PRELOADED array of SATs not populated"
    assert_equal SubAccountType::DESCRIPTIONS.length, SubAccountType.find_all.length
  end
  
  def test_types_not_empty
    assert_equal SubAccountType::TYPES.length, SubAccountType::PRELOADED.length
    assert SubAccountType::TYPES.length > 0
    SubAccountType::TYPES.each { |sa| assert_not_nil sa }
  end
  
end
