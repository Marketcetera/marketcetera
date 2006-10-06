module QF_BuyHelper

  # English display constants
  SideBuy = "Buy"
  SideSell = "Sell"
  SideSellShort = "Sell Short"
  SideSellShortExempt = "Sell Short Exempt"
  
  # codes
  SideBuyCode = Quickfix::Side_BUY()
  SideSellCode = Quickfix::Side_SELL()
  SideSellShortCode = Quickfix::Side_SELL_SHORT()
  SideSellShortExemptCode = Quickfix::Side_SELL_SHORT_EXEMPT()

  # Takes an integer code and translates that into a human string
  def get_human_side(inSideCode)
    case inSideCode
    when SideBuyCode
      return SideBuy
    when SideSellCode
      return SideSell
    when SideSellShortCode 
      return SideSellShort
    when SideSellShortExemptCode 
      return SideSellShortExempt
    else return "Unknown side: " + inSideCode.to_s   
    end
  end
end