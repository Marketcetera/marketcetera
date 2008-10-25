# Profitandloss is a regular Ruby class, it doesn't ahve a corresponding database table 
# and is intended to be a helper class (a struct) holding relevant cash flow-related information.
class ProfitAndLoss
  attr_reader :profit_and_loss, :currency_id, :account_id, :strategy, :tradeable

  def ProfitAndLoss.generate_position_query(tradeable_type_string, inclusive_of_date)
    "   SELECT sum(position_qty) AS position, tradeable_id, account_id, strategy "+
    "   FROM trades JOIN journals ON journals.id=trades.journal_id "+
    "   WHERE journals.post_date #{inclusive_of_date ? '<=' : '<'} ? AND tradeable_type = \"#{tradeable_type_string}\" "+
    "   GROUP BY tradeable_id, account_id, strategy "+
    "   HAVING position != 0 "
  end
  
  @@pnl_equity_cashflow_queries =
    '	/* real cash flows */ '+
    '	SELECT postings.quantity as quantity, postings.currency_id, trades.tradeable_id, trades.account_id, strategy '+
    '	FROM postings JOIN journals ON postings.journal_id=journals.id '+
    '		      JOIN trades ON trades.journal_id=journals.id '+
    '         JOIN sub_accounts ON sub_account_id = sub_accounts.id '+
    '	WHERE journals.post_date >= ? AND journals.post_date <= ? AND sub_accounts.sub_account_type_id=2 AND tradeable_type="Equity" '+
    '	UNION ALL '+
    ' '+
    '	/* phantom cash flows for start date */ '+
    '	SELECT -1*mark_value * PositionTable.position as quantity, marks.currency_id, PositionTable.tradeable_id, PositionTable.account_id, PositionTable.strategy '+
    '	FROM marks JOIN '+
    '	( '+
      generate_position_query("Equity", false) +
    '   ) AS PositionTable ON PositionTable.tradeable_id = marks.tradeable_id AND mark_date = ? AND mark_type="C" AND tradeable_type="Equity" '+
    '	UNION ALL '+
    ' '+
    '	/* phantom cash flows for end date */ '+
    '	SELECT mark_value * PositionTable.position as quantity, marks.currency_id, PositionTable.tradeable_id, PositionTable.account_id, PositionTable.strategy '+
    '	FROM marks JOIN '+
    '	( '+
      generate_position_query("Equity", true) +
    '   ) AS PositionTable ON PositionTable.tradeable_id = marks.tradeable_id AND mark_date = ? AND mark_type="C" AND tradeable_type="Equity" '

    
   def ProfitAndLoss.generate_missing_equity_marks_query(inclusive_of_date)
    'SELECT DISTINCT PositionTable.tradeable_id, mark_value, ? as mark_date, null as mark_type, null as created_on, null as updated_on, "Equity" as tradeable_type '+
    'FROM marks RIGHT JOIN '+
    '( '+
      generate_position_query("Equity", inclusive_of_date) +
    ') AS PositionTable ON marks.tradeable_id = PositionTable.tradeable_id AND mark_date = ? AND mark_type="C" AND tradeable_type="Equity" '+
    'WHERE mark_value IS NULL '
  end
  
  @@pnl_forex_cashflow_queries = 
    ' /* real cash flows */ '+
    ' SELECT postings.quantity as quantity, postings.currency_id as currency_id, trades.tradeable_id, trades.account_id, strategy '+
    ' FROM postings JOIN journals ON postings.journal_id=journals.id '+
    '         JOIN trades ON trades.journal_id=journals.id '+
    '         JOIN sub_accounts ON sub_account_id = sub_accounts.id '+
    ' WHERE journals.post_date >= ? AND journals.post_date <= ? AND sub_accounts.sub_account_type_id=2 AND tradeable_type="CurrencyPair" '+
    ' UNION ALL '+
    ' /* phantom cash flows for start date */ '+
    ' SELECT -1*mark_value * PositionTable.position as quantity, currency_pairs.second_currency_id as currency_id, PositionTable.tradeable_id, PositionTable.account_id, PositionTable.strategy '+
    ' FROM marks JOIN '+
    ' ( '+
      generate_position_query("CurrencyPair", false) +
    ' ) AS PositionTable ON PositionTable.tradeable_id = marks.tradeable_id AND mark_date = ? AND mark_type="C" AND tradeable_type="CurrencyPair" '+
    ' JOIN currency_pairs ON currency_pairs.id = PositionTable.tradeable_id '+
    ' UNION ALL '+
    ' SELECT PositionTable.position as quantity, currency_pairs.first_currency_id as currency_id, PositionTable.tradeable_id, PositionTable.account_id, PositionTable.strategy '+
    ' FROM ( '+
      generate_position_query("CurrencyPair", false) +
    ' ) AS PositionTable '+
    ' JOIN currency_pairs ON currency_pairs.id = PositionTable.tradeable_id '+
    ' UNION ALL '+
    ' /* phantom cash flows for end date */ '+
    ' SELECT mark_value * PositionTable.position as quantity, currency_pairs.second_currency_id as currency_id, PositionTable.tradeable_id, PositionTable.account_id, PositionTable.strategy '+
    ' FROM marks JOIN '+
    ' ( '+
      generate_position_query("CurrencyPair", true) +
    ' ) AS PositionTable ON PositionTable.tradeable_id = marks.tradeable_id AND mark_date = ? AND mark_type="C" AND tradeable_type="CurrencyPair" '+
    ' JOIN currency_pairs ON currency_pairs.id = PositionTable.tradeable_id '+
    ' UNION ALL '+
    ' SELECT -1*PositionTable.position as quantity, currency_pairs.first_currency_id as currency_id, PositionTable.tradeable_id, PositionTable.account_id, PositionTable.strategy '+
    ' FROM ( '+
      generate_position_query("CurrencyPair", true) +
    ' ) AS PositionTable  '+
    ' JOIN currency_pairs ON currency_pairs.id = PositionTable.tradeable_id ';

  def ProfitAndLoss.generate_missing_forex_marks_query(inclusive_of_date)
    'SELECT DISTINCT PositionTable.tradeable_id, mark_value, ? as mark_date, null as mark_type, null as created_on, null as updated_on, "CurrencyPair" as tradeable_type '+
    'FROM marks RIGHT JOIN '+
    '( '+
      generate_position_query("CurrencyPair", inclusive_of_date) +
    ') AS PositionTable ON marks.tradeable_id = PositionTable.tradeable_id AND mark_date = ? AND mark_type="C" AND tradeable_type="CurrencyPair" '+
    'WHERE mark_value IS NULL '
  end

  # Profitandloss is a BigDecimal, account and symbols are strings
  def initialize(profit_and_loss, currency_id, account_id, strategy, tradeable = nil)
    @profit_and_loss = profit_and_loss
    @account_id  = account_id
    @currency_id = currency_id
    @strategy = strategy
    @tradeable = tradeable
  end
  
  def to_s
    @profit_and_loss.to_s + " for #{@symbol} in account [#{account}]"
  end

  def account
    Account.find(@account_id)
  end
  
  def currency
    Currency.find(@currency_id)
  end
  
  
  def ProfitAndLoss.get_equity_pnl(from_date, to_date)
    pnl_equity_query = '/* pnl in all currencies */ '+
    'SELECT sum(quantity) as pnl_local_currency, currency_id, account_id, strategy from  '+
    '( '+
      @@pnl_equity_cashflow_queries +
    ') AS PnL GROUP BY currency_id, account_id, strategy'

    sane = ActiveRecord::Base.sanitize_sql_accessor([pnl_equity_query, from_date, to_date, from_date, from_date, to_date, to_date])
    ActiveRecord::Base.connection.select_all(sane).collect! { |row| 
      ProfitAndLoss.new(row["pnl_local_currency"], row["currency_id"], row["account_id"], row["strategy"])
    }
  end 

  def ProfitAndLoss.get_equity_pnl_detail(account, from_date, to_date)
    pnl_equity_query = '/* pnl in all currencies */ '+
    'SELECT sum(quantity) as pnl_local_currency, currency_id, tradeable_id, account_id, strategy from  '+
    '( '+
      @@pnl_equity_cashflow_queries +
    ') AS PnL WHERE account_id = ? GROUP BY account_id, currency_id, strategy, tradeable_id'

    sane = ActiveRecord::Base.sanitize_sql_accessor([pnl_equity_query, from_date, to_date, from_date, from_date, to_date, to_date, account.id])
    ActiveRecord::Base.connection.select_all(sane).collect! { |row| 
      tradeable = Equity.find_by_id(row["tradeable_id"])
      ProfitAndLoss.new(row["pnl_local_currency"], row["currency_id"], row["account_id"], row["strategy"], tradeable)
    }
  end

  def ProfitAndLoss.get_forex_pnl(from_date, to_date)
    pnl_forex_query = 
    'SELECT sum(quantity) as pnl_local_currency, currency_id, account_id, strategy from '+
    '( '+
      @@pnl_forex_cashflow_queries +
    ') AS PnL GROUP BY account_id, currency_id, strategy ORDER BY account_id, strategy ';

    sane = ActiveRecord::Base.sanitize_sql_accessor([pnl_forex_query, from_date, to_date, from_date, from_date, from_date, to_date, to_date, to_date])
    ActiveRecord::Base.connection.select_all(sane).collect! { |row| 
      ProfitAndLoss.new(row["pnl_local_currency"], row["currency_id"], row["account_id"], row["strategy"])
    }
  end 

  def ProfitAndLoss.get_forex_pnl_detail(account, from_date, to_date)
    pnl_forex_query = 
    'SELECT sum(quantity) as pnl_local_currency, currency_id, tradeable_id, account_id, strategy from '+
    '( '+
      @@pnl_forex_cashflow_queries +
    ') AS PnL WHERE account_id = ? GROUP BY account_id, currency_id, strategy, tradeable_id ';

    sane = ActiveRecord::Base.sanitize_sql_accessor([pnl_forex_query, from_date, to_date, from_date, from_date, from_date, to_date, to_date, to_date, account.id])
    ActiveRecord::Base.connection.select_all(sane).collect! { |row| 
      tradeable = CurrencyPair.find_by_id(row["tradeable_id"])
      ProfitAndLoss.new(row["pnl_local_currency"], row["currency_id"], row["account_id"], row["strategy"], tradeable)
    }
  end

  def ProfitAndLoss.get_missing_equity_marks(the_date, inclusive_of_date, by_account = nil)
    missing_equity_marks_query = generate_missing_equity_marks_query(inclusive_of_date)
    if (by_account.nil?)
      sane = ActiveRecord::Base.sanitize_sql_accessor([missing_equity_marks_query, the_date, the_date, the_date])
    else 
      missing_equity_marks_query += " AND account_id = ?"
      sane = ActiveRecord::Base.sanitize_sql_accessor([missing_equity_marks_query, the_date, the_date, the_date, by_account.id])
    end
    Mark.find_by_sql(sane)
  end
  
  def ProfitAndLoss.get_missing_forex_marks(the_date, inclusive_of_date, by_account = nil)
    missing_forex_marks_query = generate_missing_forex_marks_query(inclusive_of_date)
    
    if (by_account.nil?)
      sane = ActiveRecord::Base.sanitize_sql_accessor([missing_forex_marks_query, the_date, the_date, the_date])
    else 
      missing_forex_marks_query += " AND account_id = ?"
      sane = ActiveRecord::Base.sanitize_sql_accessor([missing_forex_marks_query, the_date, the_date, the_date, by_account.id])
    end
    
    ForexMark.find_by_sql(sane)
  end
end