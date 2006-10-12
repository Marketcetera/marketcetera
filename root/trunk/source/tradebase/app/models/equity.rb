class Equity < ActiveRecord::Base
  has_many :dividends
  belongs_to :m_symbol
  
  has_many :trades, :as => :tradeable

  def Equity.get_equity(ref_symbol)
    symbol = MSymbol.find(:first, :conditions=>["root = ?", ref_symbol])
    if(symbol == nil)
      symbol = MSymbol.new(:root => ref_symbol)
      symbol.save
      equity = Equity.new(:m_symbol => symbol)
      equity.save
      return equity
    else
      return Equity.find(symbol.id)
    end
  end



end

