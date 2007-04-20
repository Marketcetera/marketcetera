# Cashflow is a regular Ruby class, it doesn't ahve a corresponding database table 
# and is intended to be a helper class (a struct) holding relevant cash flow-related information.
class CashFlow 
  attr_reader :cashflow, :account, :symbol, :tradeable_id

  # Cashflow is a BigDecimal, account and symbols are strings
  def initialize(cashflow, symbol, account, tradeable_id)
    @cashflow = cashflow
    @account  = account
    @symbol   = symbol
    @tradeable_id   = tradeable_id
  end
  
  def to_s
    @cashflow.to_s + " for #{@symbol} in #{account}"
  end
  
    # returns an array of cashflows for the specified account 
  # Returns an arra of [cashflow, tradeable_id] pairs
  # Incoming acct is an Account object
  def CashFlow.get_cashflows_from_to_in_acct(acct, from_date, to_date)
    params = [SubAccountType::CASH, from_date, to_date]
    acctQuery = ''
    if(!acct.blank?)
      acctQuery = 'AND t.account_id = ?'
      params << acct
    end

    results = Journal.find_by_sql(['SELECT sum(p.quantity) as cashflow, t.tradeable_id, '+
                                          's.root as symbol, a.nickname as account ' +
              'FROM trades AS t, postings p, journals j, m_symbols s, sub_accounts sa, equities e, accounts a ' +
              'WHERE t.journal_id = j.id AND p.journal_id = j.id AND t.tradeable_id = e.id AND e.m_symbol_id = s.id AND '+
              't.account_id = a.id AND p.sub_account_id = sa.id AND sa.sub_account_type_id = ? AND '+
              'j.post_date > ? AND j.post_date <= ? '+acctQuery + 
              ' GROUP BY t.tradeable_id, t.account_id '+ 
              ' HAVING cashflow != 0 '+
              ' ORDER BY symbol ', params].flatten)
    cashflows = {}
    results.each { |cf| 
      openSyntheticCashflow = get_synthetic_cashflow(from_date, acct, cf.tradeable_id, cf.symbol)
      closeSyntheticCashflow = get_synthetic_cashflow(to_date, acct, cf.tradeable_id, cf.symbol)
      cashflows[cf.symbol] = CashFlow.new(BigDecimal.new(cf.cashflow)+closeSyntheticCashflow - openSyntheticCashflow, 
                                cf.symbol, cf.account, cf.tradeable_id) 
    }
    
    # now look at all positions that we had open on P&L start date
    positionsOnToDate = Position.get_positions_on_inclusive_date_and_account(from_date, acct)
    positionsOnToDate.each { |pos|
      equity = Equity.find(pos.tradeable_id)
      openSyntheticCashflow = get_synthetic_cashflow(from_date, acct, pos.tradeable_id, equity.m_symbol_root)
      closeSyntheticCashflow = get_synthetic_cashflow(to_date, acct, pos.tradeable_id, equity.m_symbol_root)
      if(cashflows[equity.m_symbol_root].nil?)
        cashflows[equity.m_symbol_root] = CashFlow.new(closeSyntheticCashflow - openSyntheticCashflow, 
                                  equity.m_symbol_root, pos.account.nickname, pos.tradeable_id)
      else 
        cf = cashflow[equity.m_symbol_root]
        cf.cashflow += closeSyntheticCashflow - openSyntheticCashflow
      end                                 
    }
    
    return cashflows.values.sort { |x, y| x.symbol <=> y.symbol }
  end

  # Gets the synthetic cashflow for a particular equity on a date meant to reflect what it'd 
  # cost to get into that position on that date.
  # Throws an exception if there's no mark for that date
  # Returns 0 if we didn't have a position on the date, or pos * markValue otherwise
  def CashFlow.get_synthetic_cashflow(date, acct, tradeable_id, symbol)
    posOnDate = Position.get_position_on_date_for_equity(tradeable_id, date, acct)
    if(posOnDate.nil? || posOnDate.empty?)
      return 0
    end
    markOnDate = Mark.find(:first, :conditions => ['equity_id = ? AND mark_date =? ', tradeable_id, date])
    if(markOnDate.blank?) 
      raise Exception.new("Please enter a mark for #{symbol} on #{date}.")
    end
    return BigDecimal.new(posOnDate[0].position) * markOnDate.mark_value
  end  
end