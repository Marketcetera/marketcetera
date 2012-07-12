require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class JournalTest < MarketceteraTestBase
  fixtures :journals
  include TradesHelper

  # Populate journal with some sub-account types and find them by desc
  def test_find_posting_by_sat
    # create an account
    acct = Account.create(:nickname => "test-acct", :institution_identifier => "bob")
    journal = Journal.create(:description => "random journal")
    
    sti = acct.find_sub_account_by_sat(SubAccountType::DESCRIPTIONS[:sti])
    assert_not_nil sti, "didn't find STI sub_account"
    cash = acct.find_sub_account_by_sat(SubAccountType::DESCRIPTIONS[:cash])
    assert_not_nil cash, "didn't find cash sub_account"
    commissions = acct.find_sub_account_by_sat(SubAccountType::DESCRIPTIONS[:commissions])
    assert_not_nil commissions, "didn't find commission sub_account"
    
    # create some postings: sti, cash, commissions, cash
    journal.postings << Posting.create(:journal=>journal, :currency=>currencies(:usd), 
        :quantity=>100, :sub_account=>sti, :pair_id => 1)
    journal.postings << Posting.create(:journal=>journal, :currency=>currencies(:usd), 
        :quantity=>-100, :sub_account=>cash, :pair_id => 1)
    journal.postings << Posting.create(:journal=>journal, :currency=>currencies(:usd), 
        :quantity=>17, :sub_account=>commissions, :pair_id => 2)
    journal.postings << Posting.create(:journal=>journal, :currency=>currencies(:usd), 
        :quantity=>-17, :sub_account=>cash, :pair_id => 2)
    journal.save
    
    # now start checking that all postings are found
    assert_equal sti, journal.find_posting_by_sat(SubAccountType::DESCRIPTIONS[:sti]).sub_account
    assert_equal cash, journal.find_posting_by_sat(SubAccountType::DESCRIPTIONS[:cash]).sub_account
    assert_equal commissions, journal.find_posting_by_sat(SubAccountType::DESCRIPTIONS[:commissions]).sub_account
    
    # now find the 2nd cash posting with pair-id
    commissionCash = journal.find_posting_by_sat_and_pair_id(SubAccountType::DESCRIPTIONS[:cash], 2)
    assert_not_nil commissionCash
    assert_equal -17, commissionCash.quantity
    
    # and now empty
    assert_nil journal.find_posting_by_sat_and_pair_id(SubAccountType::DESCRIPTIONS[:cash], 37)
    assert_nil journal.find_posting_by_sat(SubAccountType::DESCRIPTIONS[:dividendRevenue])
  end
end
