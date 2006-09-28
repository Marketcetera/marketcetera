module TradesHelper
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
        return "Unknown: "+trade_type
    end
  end  

  # Asset Types
  AssetTypeEquity = 'E'
  AssetTypeEquityOption = 'O'
  
  def get_human_asset_type(asset_type)
    case (asset_type)
    when AssetTypeEquity:
      return "Equity"
    when AssetTypeEquityOption:
      return "EquityOption"
    else
      return "Unknown: " + asset_type
    end
  end
end
