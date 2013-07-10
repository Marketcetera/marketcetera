class Dividend < ActiveRecord::Base
  belongs_to :equity
  belongs_to :currency
  validates_numericality_of :amount
    
  def validate
    errors.add(:amount, "should be positive") unless amount.nil? || amount >= 0
    errors.add(:symbol, "Symbol cannot be empty") unless !equity_m_symbol_root.blank?
    errors.add(:currency, "Could not find specified currency") unless !currency.nil?
  end
  
  def equity_m_symbol_root
      (self.equity.nil? || self.equity.m_symbol.nil?) ? nil : self.equity.m_symbol.root
  end

  def currency_alpha_code
    (self.currency.nil?) ? nil : self.currency.alpha_code
  end
end
