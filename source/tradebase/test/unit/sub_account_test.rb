require File.dirname(__FILE__) + '/../test_helper'

class SubAccountTest < Test::Unit::TestCase
  fixtures :sub_accounts, :sub_account_types, :accounts
  include SubAccountsHelper
  
  def setup
    @num_subs = SubAccountType.find_all.length
    assert_equal 2, @num_subs, "doesn't have any sub_account types"
  end
  
  # Create an account, and then make sure all sub-accounts are filled on it.
  def test_fill_subaccounts
    toliAcct = accounts(:toli_account)
    assert_equal 0, toliAcct.sub_accounts.length
    fill_in_sub_accounts(toliAcct)
    assert_equal @num_subs, toliAcct.sub_accounts.length
    
    # access a random one
    assert_not_nil toliAcct.sub_accounts[1]
    assert_equal "fake cash", toliAcct.sub_accounts[1].description
    
    # reload
    readAcct = Account.find(toliAcct.id)
    assert_equal @num_subs, readAcct.sub_accounts.length
  end
end
