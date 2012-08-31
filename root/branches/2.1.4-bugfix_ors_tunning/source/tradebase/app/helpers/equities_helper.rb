module EquitiesHelper

  def equity_symbol_link(equity)
    link_to equity.m_symbol.root, {:action => 'show', :id => equity.m_symbol, :controller => 'm_symbols' }
  end

end
