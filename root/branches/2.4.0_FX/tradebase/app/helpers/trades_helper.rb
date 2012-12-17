module TradesHelper
  
  TradeTypeTrade = 'T'
  TradeTypeReconciliation = 'R'
  TradeTypeCorporateAction = 'C'
  TradeTypeExerciseOrExpire = 'E'

  SecurityTypeEquity = 'E'
  SecurityTypeForex = 'F'
  
  def get_human_trade_type(trade_type)
    case (trade_type)
    when TradeTypeTrade:
      return "Trade"
    when TradeTypeReconciliation:
      return "Reconciliation"
    when TradeTypeExerciseOrExpire:
      return "Exercise or expire"
    when TradeTypeCorporateAction:
      return "Corporate action"
    else
      return "Unknown: "+trade_type.to_s
    end
  end    

  def get_human_security_type(security_type)
    case (security_type)
    when SecurityTypeEquity:
      return "Equity"
    when SecurityTypeForex:
      return "Forex"
    else
      return "Unknown: "+security_type.to_s
    end
  end    
  
  # find the number of trades executed on a particular date
  # note that we are dealing with Date and not DateTime here
  def number_trades_on_day(date)
    return (Trade.count_by_sql(["select count(*) \
                          from trades, journals where \
                          trades.journal_id = journals.id and journals.post_date < ? AND journals.post_date >= ?", date+1, date]))
  end
end
