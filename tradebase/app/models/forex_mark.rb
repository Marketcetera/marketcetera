class ForexMark < Mark
  def tradeable_m_symbol_root=(inSymbol)
    self.tradeable = CurrencyPair.get_currency_pair(inSymbol, true)
  end

  def security_type
    TradesHelper::SecurityTypeForex
  end

  def tradeable_m_symbol_root
    (self.tradeable.nil?) ? nil : self.tradeable.to_s
  end
end