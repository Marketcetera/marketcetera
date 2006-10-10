require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class AccountTest < MarketceteraTestBase
  fixtures :accounts
  
  # Replace this with your real tests.
  def test_sub_account_creation
    a = Account.new(:nickname => "new account")
    assert_equal 0, a.sub_accounts.length
    a.save
    assert_equal SubAccountType::DESCRIPTIONS.length, SubAccountType.preloaded.length
    assert_equal SubAccountType::DESCRIPTIONS.length, a.sub_accounts.length
    assert_equal SubAccountType.preloaded.length, a.sub_accounts.length
    assert a.sub_accounts.length > 0
    assert true
  end

  def test_find_by_type
    a = Account.create(:nickname => "find-by-type")
    sa = a.find_sub_account_by_sat(SubAccountType::DESCRIPTIONS[:sti])
    assert_not_nil sa
    assert_equal SubAccountType::DESCRIPTIONS[:sti], sa.description
    
    # now do it for all
    SubAccountType::DESCRIPTIONS.each { |t| 
      logger.debug "looking up " + t[0].to_s
      sa = a.find_sub_account_by_sat(t[1])
      assert_not_nil sa, t[1]
      assert_equal sa.description, t[1]
    }
  end
  
end
