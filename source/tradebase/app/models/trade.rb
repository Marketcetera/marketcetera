class Trade < ActiveRecord::Base

include SubAccountsHelper

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
      commissions = self.journal.postings.select { |p| p.sub_account.sub_account_type.description == CommissionsDescription }[0]
      return commissions.quantity
    end
  end
    
  def total_commission=(inCommission)
    commissions = self.journal.postings.select { |p| p.sub_account.sub_account_type.description == CommissionsDescription }[0]
    cash = self.journal.postings.select { |p| (p.pair_id == commissions.pair_id) && (p.sub_account.sub_account_type.description == CashDescription) }[0]
    commissions.quantity = inCommission
    cash.quantity = -Float(inCommission)
  end
    
    
end
