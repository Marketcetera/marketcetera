class Equity < ActiveRecord::Base
  has_many :dividends
  belongs_to :m_symbol
  
  has_many :trades, :as => :tradeable

  # Returns the equity for the underlying root symbol, or creates a new one if it's not there
  def Equity.get_equity(ref_symbol)
    symbol = MSymbol.find(:first, :conditions=>["root = ?", ref_symbol])
    if(symbol == nil)
      symbol = MSymbol.new(:root => ref_symbol)
      symbol.save
      equity = Equity.new(:m_symbol => symbol)
      equity.save
      return equity
    else
      return Equity.find_by_m_symbol_id(symbol.id)
    end
  end

end

