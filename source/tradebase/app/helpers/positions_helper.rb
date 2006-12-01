module PositionsHelper

  def get_positions_as_of_date(date)
     return( paginate_by_sql Position, 
            [ 'SELECT sum(trades.position_qty) as position, tradeable_id, tradeable_type, account_id, journal_id '+
              ' FROM trades'+
              ' LEFT JOIN journals ON trades.journal_id=journals.id '+
              ' WHERE journals.post_date< ? GROUP BY tradeable_id, account_id, tradeable_type'+
              ' HAVING position != 0 ',
              date], 10)
  end
  
  # returns the N top accounts with the most positions
  def get_top_positioned_accounts(num_accounts)
    accountIDs = Position.count(:group => 'account_id', :limit => num_accounts, :order => 'count_all desc')
    accounts = Array.new
    accountIDs.each{ |id, n| accounts.push([Account.find(id), n]) }
    return accounts
  end
end