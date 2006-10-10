require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class AccountTest < MarketceteraTestBase
  fixtures :accounts
  
  # Replace this with your real tests.
  def test_sub_account_creation
    a = Account.new(:nickname => "new account")
    assert_equal 0, a.sub_accounts.length
    a.save
    assert_equal SubAccountType::DESCRIPTIONS.length, SubAccountType::PRELOADED.length
    assert_equal SubAccountType::DESCRIPTIONS.length, a.sub_accounts.length
    assert_equal SubAccountType::PRELOADED.length, a.sub_accounts.length
    assert a.sub_accounts.length > 0
    assert true
  end

  def test_find_by_type
    a = Account.create(:nickname => "find-by-type")
    sa = a.find_sub_account_by_sat(SubAccountType::TYPES[:sti])
    assert_not_nil sa
    assert_equal SubAccountType::DESCRIPTIONS[:ShortTermInvestmentDescription], sa.description
    
    # now do it for all
    SubAccountType::TYPES.each { |t| 
      p "looking up " + t[0].to_s
      assert_not_nil a.find_sub_account_by_sat(t[1]), t[1].description
    }
  end
  
end
