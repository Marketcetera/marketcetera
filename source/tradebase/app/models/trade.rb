class Trade < ActiveRecord::Base

  belongs_to :journal
  belongs_to :account
  
  belongs_to :tradeable, :polymorphic => true
  
  def journal_post_date
    (self.id.nil?) ? nil : self.journal.post_date
  end
  
  def journal_post_date=(inDate)
    self.journal.post_date = inDate
  end

  def tradeable_m_symbol_root
    (self.id.nil?) ? nil : self.tradeable.m_symbol.root
  end
  
  def tradeable_m_symbol_root=(inSymbol)
    self.tradeable.m_symbol.root = inMSymbol
  end
  
  def account_nickname
    (self.id.nil?) ? nil : self.account.nickname
  end
  
  def account_nickname=(inNick)
    self.account.nickname = inNick
  end
  
  def total_commission
    if(self.id.nil?) 
      return nil
    else 
      commissions = self.journal.find_posting_by_sat(SubAccountType::DESCRIPTIONS[:commissions])
      return commissions.quantity
    end
  end
    
  def total_commission=(inCommission)
    commissions = self.journal.find_posting_by_sat(SubAccountType::DESCRIPTIONS[:commissions])
    cash = self.journal.find_posting_by_sat_and_pair_id(SubAccountType::DESCRIPTIONS[:cash], commissions.pair_id)
    commissions.quantity = inCommission
    cash.quantity = -Float(inCommission)
  end
    
  def total_price
    sti = self.journal.find_posting_by_sat(SubAccountType::DESCRIPTIONS[:sti])
    return sti.quantity
  end
    
end
