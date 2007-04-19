class Journal < ActiveRecord::Base
  has_many :postings
  has_many :trades
  
  # returns the first posting that matches the specified sub-account-type description
  def find_posting_by_sat(desc)
    if(!self.postings.nil?)
      return self.postings.select { |p| p.sub_account.sub_account_type.description == desc}[0]
    else return nil
    end
  end
  
    # find subaccount type by a sub-account-type.description and pair_id
  def find_posting_by_sat_and_pair_id(desc, pair_id)
    if(self.postings.nil?) 
      return nil
    else return self.postings.select {|p| (p.sub_account.sub_account_type.description == desc) && (p.pair_id == pair_id) }[0]
    end
  end
  
  # returns an array of cashflows for the specified account 
  # Returns an arra of [cashflow, tradeable_id] pairs
  # Incoming acct is an Account object
  def Journal.get_cashflows_from_to_in_acct(acct, from_date, to_date)
    params = [SubAccountType::CASH, from_date, to_date]
    acctQuery = ''
    if(!acct.blank?)
      acctQuery = 'AND t.account_id = ?'
      params << acct
    end

    cashflow = Journal.find_by_sql(['SELECT sum(p.quantity) as cashflow, t.tradeable_id, s.root as symbol, a.nickname as account ' +
              'FROM trades AS t, postings p, journals j, m_symbols s, sub_accounts sa, equities e, accounts a ' +
              'WHERE t.journal_id = j.id AND p.journal_id = j.id AND t.tradeable_id = e.id AND e.m_symbol_id = s.id AND '+
              't.account_id = a.id AND p.sub_account_id = sa.id AND sa.sub_account_type_id = ? AND '+
              'j.post_date >= ? AND j.post_date <= ? '+acctQuery + 
              ' GROUP BY t.tradeable_id, t.account_id '+ 
              ' HAVING cashflow != 0', params].flatten)
     cashflow.each { |cf| { :cashflow => cf.cashflow, :tradeable_id => cf.tradeable_id, :symbol => cf.symbol, :account => cf.account} }
  end
  
  
end
