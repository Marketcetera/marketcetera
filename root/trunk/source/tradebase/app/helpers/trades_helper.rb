module TradesHelper
#  include Side
  
  TradeTypeTrade = 'T'
  TradeTypeReconciliation = 'R'
  TradeTypeCorporateAction = 'C'
  TradeTypeExerciseOrExpire = 'E'
  
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
end
