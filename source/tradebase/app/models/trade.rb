class Trade < ActiveRecord::Base

  belongs_to :journal
  belongs_to :account
  
  belongs_to :tradeable, :polymorphic => true
  
  def journal_post_date
    (self.id.nil?) ? nil : self.journal.post_date
  end
  
  def journal_post_date=(inDate)
      logger.debug("setting the journal post date: "+inDate.to_s)
    if(!self.id.nil?) 
      self.journal.post_date = inDate
    end
  end

  def tradeable_m_symbol_root
    (self.id.nil?) ? nil : self.tradeable.m_symbol.root
  end
  
  def tradeable_m_symbol_root=(inSymbol)
    self.tradeable.m_symbol.root = inSymbol
  end
  
  def account_nickname
    (self.id.nil?) ? nil : self.account.nickname
  end
  
  def account_nickname=(inNick)
    if(!self.id.nil?)
      self.account.nickname = inNick
    end
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
    if(self.id.nil?)
      logger.debug("we are not initialized yet, so jumping out early")
      return
    end

    commissions = self.journal.find_posting_by_sat(SubAccountType::DESCRIPTIONS[:commissions])
    cash = self.journal.find_posting_by_sat_and_pair_id(SubAccountType::DESCRIPTIONS[:cash], commissions.pair_id)
    commissions.quantity = inCommission
    cash.quantity = -Float(inCommission)
    logger.debug("updated commissions for "+self.tradeable_m_symbol_root + " to "+commissions.quantity.to_s)
    commissions.save
    cash.save
  end
    
  def total_price
    sti = self.journal.find_posting_by_sat(SubAccountType::DESCRIPTIONS[:sti])
    return sti.quantity
  end
    
  # Need to update all the underlying journals/postings when we update the price_per_share
  def price_per_share=(new_price)
    super(new_price)
    if(self.id.nil? || self.journal.nil?)
      logger.debug("we are not initialized yet, so jumping out early, internal pps set to "+self.price_per_share.to_s)
      return
    end
    
    logger.debug("updating the posting transactions when updating price")
    sti = self.journal.find_posting_by_sat(SubAccountType::DESCRIPTIONS[:sti])
    cash = self.journal.find_posting_by_sat_and_pair_id(SubAccountType::DESCRIPTIONS[:cash], sti.pair_id)
    sti.quantity = self.quantity * Float(new_price)
    cash.quantity = -Float(sti.quantity)
    logger.debug("updated total price for "+self.tradeable_m_symbol_root + " to "+sti.quantity.to_s)
    sti.save
    cash.save
  end
    
 def create_equity_trade(quantity, symbol, price_per_share, 
                          total_commission, currency_alpha_code, account_nickname, trade_date)
    self.price_per_share = Float(price_per_share)
    self.quantity = quantity
    logger.debug("incoming pps: "+price_per_share.to_s)
    logger.debug("*** pps: "+self.price_per_share.to_s)
    notional = self.quantity * self.price_per_share
    total_commission = total_commission
    self.tradeable = Equity.get_equity(symbol)
    logger.debug("creating a trade for "+symbol + " for "+notional.to_s + "/("+total_commission.to_s + ")")
    logger.debug("*** pps: "+self.price_per_share.to_s)
    self.account = Account.find_by_nickname(account_nickname)
    if(self.account == nil)
      self.account = Account.create(:nickname => account_nickname)
      logger.debug("created new account [" + account_nickname + "] and sub-accounts")
      self.account.save
    end
    sub_accounts = self.account.sub_accounts
    short_term_investment_sub_account = self.account.find_sub_account_by_sat(SubAccountType::DESCRIPTIONS[:sti])
    cash_sub_account = self.account.find_sub_account_by_sat(SubAccountType::DESCRIPTIONS[:cash])
    commission_sub_account = self.account.find_sub_account_by_sat(SubAccountType::DESCRIPTIONS[:commissions])

    logger.debug("*** pps: "+self.price_per_share.to_s)

    
    self.journal = Journal.create( :post_date => trade_date )
    base_currency = Currency.get_currency(currency_alpha_code)
    self.journal.postings << Posting.create(:journal=>self.journal, :currency=>base_currency, :quantity=>notional, :sub_account=>short_term_investment_sub_account, :pair_id => 1)
    self.journal.postings << Posting.create(:journal=>self.journal, :currency=>base_currency, :quantity=>(-1*notional), :sub_account=>cash_sub_account, :pair_id => 1)
    self.journal.postings << Posting.create(:journal=>self.journal, :currency=>base_currency, :quantity=>total_commission, :sub_account=>commission_sub_account, :pair_id => 2)
    self.journal.postings << Posting.create(:journal=>self.journal, :currency=>base_currency, :quantity=>(-1*total_commission), :sub_account=>cash_sub_account, :pair_id => 2)
    
    self.journal.save
    logger.debug("created and saved all related postings")
    logger.debug("*** pps: "+self.price_per_share.to_s)
  end
  
    
end
