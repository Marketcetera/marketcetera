require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class AccountTest < MarketceteraTestBase
  fixtures :accounts
  
  # Replace this with your real tests.
  def test_sub_account_creation
    a = Account.new(:nickname => "new account", :institution_identifier => "bob")
    assert_equal 0, a.sub_accounts.length
    assert a.save
    assert_equal SubAccountType::DESCRIPTIONS.length, SubAccountType.preloaded.length
    assert_equal SubAccountType::DESCRIPTIONS.length, a.sub_accounts.length
    assert_equal SubAccountType.preloaded.length, a.sub_accounts.length
    assert a.sub_accounts.length > 0
    assert true
  end

  def test_find_by_type
    a = Account.create(:nickname => "find-by-type", :institution_identifier => "bob")
    sa = a.find_sub_account_by_sat(SubAccountType::DESCRIPTIONS[:sti])
    assert_not_nil sa
    assert_equal SubAccountType::DESCRIPTIONS[:sti], sa.description
    
    # now do it for all
    SubAccountType::DESCRIPTIONS.each { |t| 
      sa = a.find_sub_account_by_sat(t[1])
      assert_not_nil sa, t[1]
      assert_equal sa.description, t[1]
    }
  end
  
  def test_find_by_nickname
    beer = Account.find_by_nickname("beer money")
    assert_equal 2, beer.id
    
    assert_nil Account.find_by_nickname("dne")
    
    unassigned = Account.find_by_nickname(Account::UNASSIGNED_NAME)
    assert_not_nil unassigned
    
    assert_not_nil Account.find_by_nickname(nil)
    assert_equal unassigned.id, Account.find_by_nickname(nil).id
    assert_equal unassigned.id, Account.find_by_nickname('').id
  
  end
  
  def test_to_s
    a = Account.new(:nickname => "trali vali", :institution_identifier => "bob")
    assert_equal "[trali vali]", a.to_s
    
    a.nickname = nil
    assert_equal "[]", a.to_s
  end
  
end
