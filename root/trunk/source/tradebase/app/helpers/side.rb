class Side

  SIDES = { :buy => "Buy", :sell => "Sell", :sellShort => "Sell Short", :sellShortExempt => "Sell Short Exempt"}
  QF_SIDE_CODE = { :buy => Quickfix::Side_BUY(), :sell => Quickfix::Side_SELL(), :sellShort => Quickfix::Side_SELL_SHORT(), 
                   :sellShortExempt => Quickfix::Side_SELL_SHORT_EXEMPT() }
  
  SIDE_SHORT_CODE = {Quickfix::Side_BUY().to_i => "B", Quickfix::Side_SELL().to_i => "S", 
                     Quickfix::Side_SELL_SHORT().to_i => "SS", Quickfix::Side_SELL_SHORT_EXEMPT().to_i => "SSE"}
  
  def Side.SIDES_HI_COLLECTION 
    Side::SIDES.collect{ |s| [ s[1], Side::QF_SIDE_CODE[s[0]] ] }.sort { |x,y| x[1].to_i - y[1].to_i}
  end
  
  def initialize(name, value)
    @name = name
    @value = value    
  end
  
  def name
    @name
  end
  
  def first 
    @name end
  def last
    @value end
  
  def value
    @value
  end  
  
  def Side.get_human_side(inCode)
    if(inCode.blank?) then return '' end
    DataDictionaryMgr.get_value_name(Quickfix::Side.new, inCode.to_s)
  end
end