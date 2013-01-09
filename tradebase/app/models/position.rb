class Position < ActiveRecord::Base
  set_table_name "trades"
  belongs_to :account
  belongs_to :tradeable, :polymorphic => true
  
  # returns all the positions for a given date and account, with date being inclusive
  # account is an account Object, not just a string nickname
  # If the incoming account is nil, returns positions across all accounts
  def Position.get_positions_on_inclusive_date_and_account(date, account)
    get_position_helper(date, account)
  end
  
  # returns the N top accounts with the most positions
  def Position.get_top_positioned_accounts(num_accounts)
    accountIDs = Position.count(:group => 'account_id', :limit => num_accounts, :order => 'count_all desc, account_id')
    accounts = Array.new
    accountIDs.each{ |id, n| accounts.push([Account.find(id), n]) }
    return accounts
  end
  
  # returns the position for a particular tradeable_id on a given date in a given account
  def Position.get_position_on_date_for_equity(tradeable_id, date, account)
    if(account.nil?)
      raise "Cannot search for position in unspecified account"
    end
    get_position_helper(date, account, tradeable_id)
  end
  
  
  def Position.get_cashflow_as_of(date, account, tradeable_id)
      Position.find_by_sql( 
            [ 'SELECT sum(trades.position_qty) * mark_value as cashflow, sum(trades.position_qty) as position, '+
                      'tradeable_id, tradeable_type, account_id, journal_id '+
              ' FROM accounts, marks, trades '+
              ' LEFT JOIN journals ON trades.journal_id = journals.id '+
              ' WHERE accounts.id = ? AND journals.post_date <= ? AND trades.account_id=accounts.id ' +
                     'AND trades.tradeable_id = ? AND marks.tradeable_id = trades.tradeable_id ' +
                     'AND marks.mark_date = ? '+
              'GROUP BY account_id, tradeable_id, tradeable_type'+
              ' HAVING position != 0 ',
              account, date, tradeable_id, date])
  end
  
  private 
  # If the incoming account is nil, returns positions across all accounts
  def Position.get_position_helper(date, account_id, tradeable_id=nil)
    params = [date]
    tradeableQuery, accountQuery  = "", ""
    if(!account_id.nil?)
      accountQuery = 'AND trades.account_id = ? '
      params << account_id
    end
    if(!tradeable_id.nil?)
      tradeableQuery = 'AND trades.tradeable_id = ? '
      params << tradeable_id
    end
    Position.find_by_sql( 
            [ 'SELECT sum(trades.position_qty) as position, tradeable_id, tradeable_type, account_id, journal_id '+
              ' FROM accounts, trades'+
              ' LEFT JOIN journals ON trades.journal_id = journals.id '+
              ' WHERE accounts.id = trades.account_id AND journals.post_date <= ? ' +
                     accountQuery + tradeableQuery +
              'GROUP BY account_id, tradeable_id, tradeable_type'+
              ' HAVING position != 0 ',
              params].flatten)
  end
end
