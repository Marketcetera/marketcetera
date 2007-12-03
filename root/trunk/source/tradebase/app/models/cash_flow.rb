# Cashflow is a regular Ruby class, it doesn't ahve a corresponding database table 
# and is intended to be a helper class (a struct) holding relevant cash flow-related information.
class CashFlow
  attr_reader :cashflow, :account, :symbol, :tradeable_id
  attr_writer :cashflow

  # Cashflow is a BigDecimal, account and symbols are strings
  def initialize(cashflow, symbol, account, tradeable_id)
    @cashflow = cashflow
    @account  = account
    @symbol   = symbol
    @tradeable_id   = tradeable_id
  end
  
  def to_s
    @cashflow.to_s + " for #{@symbol} in account [#{account}]"
  end

  # returns an array of cashflows for the specified account 
  # Incoming acct is an Account object
  # IF the incoming accoutn is nil, then we get a set of cashflows across all available accounts
  # Returns an unsorted double hashtable, where the first key is an account id pointing to another table of
  # [symbol, cashflow value] pairs.
  # Users should sort the individual per-account cashflows accordingly (probably by symbol)
  # Ex: To get list of cashflows sorted by symbol for a given account (by acct nickname)
  #   cashflows[theAcct.nickname].values.sort { |x,y| x.symbol <=> y.symbol}
  #   
  # Essentially, you get back something like this for each account:
  # result[toli] => {sunw=> <sunw cashflow>, goog => <goog cashflow>, etc}
  # result[bob] =>  {sunw=> <sunw cashflow>, goog => <goog cashflow>, etc}
  def CashFlow.get_cashflows_from_to_in_acct(acct, from_date, to_date)
    params = [SubAccountType.CASH, from_date, to_date]
    acctQuery = ''
    if(!acct.blank?)
      acctQuery = 'AND t.account_id = ?'
      params << acct
    end

    results = Journal.find_by_sql(['SELECT sum(p.quantity) as cashflow, t.tradeable_id, '+
                                          's.root as symbol, a.nickname as account_nick, a.id as account_id ' +
              'FROM trades AS t, postings p, journals j, m_symbols s, sub_accounts sa, equities e, accounts a ' +
              'WHERE t.journal_id = j.id AND p.journal_id = j.id AND t.tradeable_id = e.id AND e.m_symbol_id = s.id AND '+
              't.account_id = a.id AND p.sub_account_id = sa.id AND sa.sub_account_type_id = ? AND '+
              'j.post_date > ? AND j.post_date <= ? '+acctQuery +
              ' GROUP BY t.tradeable_id, t.account_id '+
              ' HAVING cashflow != 0 '+
              ' ORDER BY symbol ', params].flatten)
#    logger.debug("got Journal query results between from/to dates: #{results.inspect}")
    cashflows = {}
    results.each { |cf|
      openSyntheticCashflow = get_synthetic_cashflow(from_date, cf.account_id, cf.tradeable_id, cf.symbol)
      closeSyntheticCashflow = get_synthetic_cashflow(to_date, cf.account_id, cf.tradeable_id, cf.symbol)
#      logger.debug("Synthetic cashflow for [#{acct}] on open on #{from_date.to_s}: "+openSyntheticCashflow.to_s)
#      logger.debug("Synthetic cashflow for [#{acct}] on close on #{to_date.to_s}: "+closeSyntheticCashflow.to_s)
      if(cashflows[cf.account_nick].nil?)
        cashflows[cf.account_nick] = {}
      end
      cashflows[cf.account_nick][cf.symbol] = CashFlow.new(BigDecimal.new(cf.cashflow)+closeSyntheticCashflow - openSyntheticCashflow,
                                cf.symbol, cf.account_id, cf.tradeable_id)
    }
    
    # now look at all positions that we had open on P&L start date (ie from_date)
    posOnFromDate = Position.get_positions_on_inclusive_date_and_account(from_date, acct)
    posOnFromDate.each { |pos|
#      logger.debug("posOnFromDate: " + pos.to_s)
      equity = Equity.find(pos.tradeable_id)
      openSyntheticCashflow = get_synthetic_cashflow(from_date, pos.account, pos.tradeable_id, equity.m_symbol_root)
      closeSyntheticCashflow = get_synthetic_cashflow(to_date, pos.account, pos.tradeable_id, equity.m_symbol_root)
#      logger.debug("Synthetic cashflow for [#{acct}] on open on #{from_date.to_s}: "+openSyntheticCashflow.to_s)
#      logger.debug("Synthetic cashflow for [#{acct}] on close on #{to_date.to_s}: "+closeSyntheticCashflow.to_s)
      if(cashflows[pos.account.nickname].nil?)
        cashflows[pos.account.nickname] = {}
      end
      if(cashflows[pos.account.nickname][equity.m_symbol_root].nil?)
        cashflows[pos.account.nickname][equity.m_symbol_root]  = CashFlow.new(closeSyntheticCashflow - openSyntheticCashflow, 
                                  equity.m_symbol_root, pos.account.nickname, pos.tradeable_id)
#        logger.debug("added cashflow for [#{pos.account}][#{equity.m_symbol_root}] --> #{cashflows[pos.account.nickname][equity.m_symbol_root].to_s}")
      else 
        cf = cashflows[pos.account.nickname][equity.m_symbol_root]
        calculatedSyntheticOpenCloseDiff = closeSyntheticCashflow - openSyntheticCashflow
#        logger.debug("[#{pos.account}][#{equity.m_symbol_root}] --> adding calculated cf #{calculatedSyntheticOpenCloseDiff.to_s} to #{cf.cashflow.to_s}")
        cf.cashflow += calculatedSyntheticOpenCloseDiff
      end                                 
    }
    return cashflows
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
#    if(posOnDate[0].journal.post_date == date)
#      markOnDate = Mark.new(:tradeable_id => tradeable_id, :mark_value => posOnDate[0].price_per_share, :mark_date => date)
#      logger.debug("Trade happened on posOnDate so using that for mark #{markOnDate.to_s}")
#    else
      markOnDate = Mark.find(:first, :conditions => ['tradeable_id = ? AND mark_date =? ', tradeable_id, date])
#    end
    if(markOnDate.blank?)
      raise Exception.new("Please enter a mark for #{symbol} on #{date}.")
    end
    return BigDecimal.new(posOnDate[0].position) * markOnDate.mark_value
  end  
end