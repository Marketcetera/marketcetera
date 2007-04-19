class Position < ActiveRecord::Base
  set_table_name "trades"
  belongs_to :account
  belongs_to :tradeable, :polymorphic=>true
  
  # returns all the positions for a given date and account, with date being inclusive
  # account is an account Object, not just a string nickname
  def Position.get_positions_as_of_inclusive_date_and_account(date, account)
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
  def Position.get_top_positioned_accounts(num_accounts)
    accountIDs = Position.count(:group => 'account_id', :limit => num_accounts, :order => 'count_all desc, account_id')
    accounts = Array.new
    accountIDs.each{ |id, n| accounts.push([Account.find(id), n]) }
    return accounts
  end
end
