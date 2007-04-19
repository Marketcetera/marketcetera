module PositionsHelper

  # Lookup positions for a particular date.
  # Here's the SQL
  # 'select sum(trades.position_qty) as position, tradeable_id, tradeable_type, account_id, journal_id from trades'+
  # ' LEFT JOIN journals on trades.journal_id=journals.id '+
  # ' WHERE journals.post_date< ? GROUP BY tradeable_id, account_id, tradeable_type',
  #  date])
  def get_positions_as_of_date(date)
     return( paginate_by_sql Position, 
            [ 'SELECT sum(trades.position_qty) as position, tradeable_id, tradeable_type, account_id, journal_id '+
              ' FROM trades'+
              ' LEFT JOIN journals ON trades.journal_id=journals.id '+
              ' WHERE journals.post_date< ? GROUP BY tradeable_id, account_id, tradeable_type'+
              ' HAVING position != 0 ',
              date], MaxPerPage)
  end
  
  # returns all the positions for a given date and account, with date being inclusive
  # account is an account Object, not just a string nickname
  def get_positions_as_of_inclusive_date_and_account(date, account)
    Position.find_by_sql( 
            [ 'SELECT sum(trades.position_qty) as position, tradeable_id, tradeable_type, account_id, journal_id '+
              ' FROM accounts, trades'+
              ' LEFT JOIN journals ON trades.journal_id = journals.id '+
              ' WHERE accounts.id = ? AND journals.post_date <= ? AND trades.account_id=accounts.id ' +
              'GROUP BY account_id, tradeable_id, tradeable_type'+
              ' HAVING position != 0 ',
              account, date])
  end
  
  # returns the N top accounts with the most positions
  def get_top_positioned_accounts(num_accounts)
    accountIDs = Position.count(:group => 'account_id', :limit => num_accounts, :order => 'count_all desc, account_id')
    accounts = Array.new
    accountIDs.each{ |id, n| accounts.push([Account.find(id), n]) }
    return accounts
  end
end