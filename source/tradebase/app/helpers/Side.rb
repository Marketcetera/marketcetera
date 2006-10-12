class Side

  SIDES = { :buy => "Buy", :sell => "Sell", :sellShort => "Sell Short", :sellShortExempt => "Sell Short Exempt"}
  QF_SIDE_CODE = { :buy => Quickfix::Side_BUY(), :sell => Quickfix::Side_SELL(), :sellShort => Quickfix::Side_SELL_SHORT(), 
                   :sellShortExempt => Quickfix::Side_SELL_SHORT_EXEMPT() }
  
  def Side.SIDES_HI_COLLECTION 
    SIDES.collect {|s| Side.new(s[1], QF_SIDE_CODE[s[0]]) }
  end
  
  def initialize(name, value)
    @name = name
    @value = value    
  end
  
  def name
    @name
  end
  
  def value
    @value
  end  
  
end