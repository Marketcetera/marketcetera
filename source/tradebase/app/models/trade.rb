class Trade < ActiveRecord::Base

  belongs_to :journal
  belongs_to :account
  belongs_to :tradeable, :polymorphic => true
  
  # validation
  validates_numericality_of :price_per_share
  validates_numericality_of :quantity
  
  def validate
    logger.error("**** in validation: price/qty/side: "+price_per_share.to_s + "/"+
                quantity.to_s+"/"+Side.get_human_side(side))

    if(tradeable_m_symbol_root.blank?)
      errors.add(:symbol, "Symbol cannot be empty")
    end
    #breakpoint("trade validation")
    if(quantity.blank? ||
    ((quantity <= 0 && side == Side::QF_SIDE_CODE[:buy].to_i) ||
      (quantity >= 0 && side != Side::QF_SIDE_CODE[:buy].to_i)))
      errors.add(:quantity, 'Please specify positive quantity.')
    end    
    if(price_per_share.blank? || price_per_share <= 0)
      errors.add(:price_per_share, 'Please specify positive price per share.')
    end
  end 
  
  def journal_post_date
    (self.journal.nil?) ? nil : self.journal.post_date
  end
  
  def journal_post_date=(inDate)
      logger.debug("setting the journal post date: "+inDate.to_s)
    if(!self.journal.nil?) 
      self.journal.post_date = inDate
      self.journal.save
    end
  end

  def tradeable_m_symbol_root
    (self.tradeable.nil?) ? nil : self.tradeable.m_symbol.root
  end
  
  def tradeable_m_symbol_root=(inSymbol)
    self.tradeable.m_symbol.root = inSymbol
    self.tradeable.m_symbol.save
  end
  
  def account_nickname
    (self.account.nil?) ? '' : self.account.nickname
  end
  
  # todo: should this create a new account if it DNE?
  def account_nickname=(inNick)
    newAcct = Account.find_by_nickname(inNick)
    if(!newAcct.nil?) 
      self.account = newAcct
    else
      self.account = Account.create(:nickname => inNick, :institution_identifier=>inNick)
      logger.debug("created new acct")
    end
  end
  
  def total_commission
    if(self.journal.nil?) 
      return nil
    else 
      commissions = self.journal.find_posting_by_sat(SubAccountType::DESCRIPTIONS[:commissions])
      return commissions.quantity
    end
  end
    
  def total_commission=(inCommission)
    if(self.journal.nil?)
      logger.debug("we are not initialized yet, so jumping out early")
      return
    end

    update_underlying_posting_pairs(SubAccountType::DESCRIPTIONS[:commissions], SubAccountType::DESCRIPTIONS[:cash], 
                                    inCommission)
  end
    
  def total_price
    sti = self.journal.find_posting_by_sat(SubAccountType::DESCRIPTIONS[:sti])
    return sti.quantity
  end
    
  # Need to update all the underlying journals/postings when we update the price_per_share
  def price_per_share=(new_price)
    super(new_price)
    if(self.journal.nil?)
      logger.debug("we are not initialized yet, so jumping out early, internal pps set to "+self.price_per_share.to_s)
      return
    end
    
    update_underlying_posting_pairs(SubAccountType::DESCRIPTIONS[:sti], SubAccountType::DESCRIPTIONS[:cash], 
                                    self.quantity * Float(new_price))
  end
  
  # Need to update the underlying journals/postings when we update the price_per_share
  def quantity=(new_qty)
    super(new_qty)
    if(self.journal.nil?)
      logger.debug("we are not initialized yet, so jumping out early, internal qty set to "+self.quantity.to_s)
      return
    end
    update_underlying_posting_pairs(SubAccountType::DESCRIPTIONS[:sti], SubAccountType::DESCRIPTIONS[:cash], 
                                    self.quantity * self.price_per_share)
  end    
  
  # Override the to_s function to print something more useful for debugging
  def to_s
    "["+Side.get_human_side(self.side.to_s)+"] "+self.quantity.to_s + 
    " <"+self.tradeable_m_symbol_root+"> @"+self.price_per_share.to_s + " in acct ["+self.account_nickname+"]"
  end
  
  # this is the var we want to use for all displaying and calculations in the order
  # It is adjusted according to the side, ie it's positive if it's a Buy or 
  # negative if it's a sell
  def quantity
    if(!side.blank? && side != Quickfix::Side_BUY().to_i)
      return super * (-1)  
    else return super
    end
  end

  ##### Helper Methods #####
    
 def create_equity_trade(quantity, symbol, price_per_share, 
                          total_commission, currency_alpha_code, account_nickname, trade_date)
    self.price_per_share = Float(price_per_share)
    self.quantity = quantity
    self.tradeable = Equity.get_equity(symbol)
    if(!valid?) 
      logger.debug("Skipping out on create_equity_trade b/c trade doesn't validate with errors: "+self.errors.full_messages().to_s)
      return false
    end
    
    notional = self.quantity * self.price_per_share
    total_commission = total_commission
    logger.debug("creating a trade for "+self.tradeable_m_symbol_root + " for "+notional.to_s + "/("+total_commission.to_s + ")")
    self.account = Account.find_by_nickname(account_nickname)
    if(self.account == nil)
      self.account = Account.create(:nickname => account_nickname, :institution_identifier => account_nickname)
      logger.debug("created new account [" + account_nickname + "] and sub-accounts")
      self.account.save
    end
    logger.debug("Using account " + self.account.to_s)
    sub_accounts = self.account.sub_accounts
    short_term_investment_sub_account = self.account.find_sub_account_by_sat(SubAccountType::DESCRIPTIONS[:sti])
    cash_sub_account = self.account.find_sub_account_by_sat(SubAccountType::DESCRIPTIONS[:cash])
    commission_sub_account = self.account.find_sub_account_by_sat(SubAccountType::DESCRIPTIONS[:commissions])

    self.journal = Journal.create( :post_date => trade_date )
    base_currency = Currency.get_currency(currency_alpha_code)
    self.journal.postings << Posting.create(:journal=>self.journal, :currency=>base_currency, :quantity=>notional, :sub_account=>short_term_investment_sub_account, :pair_id => 1)
    self.journal.postings << Posting.create(:journal=>self.journal, :currency=>base_currency, :quantity=>(-1*notional), :sub_account=>cash_sub_account, :pair_id => 1)
    self.journal.postings << Posting.create(:journal=>self.journal, :currency=>base_currency, :quantity=>total_commission, :sub_account=>commission_sub_account, :pair_id => 2)
    self.journal.postings << Posting.create(:journal=>self.journal, :currency=>base_currency, :quantity=>(-1*total_commission), :sub_account=>cash_sub_account, :pair_id => 2)
    
    self.journal.save
    logger.debug("created and saved all related postings")
    return true
  end
  
  def update_underlying_posting_pairs(debitName, creditName, qty)
    logger.debug("updating the posting transactions when updating trade for postings: "+debitName+"/"+creditName + " for total: "+qty.to_s)
    debitP = self.journal.find_posting_by_sat(debitName)
    creditP = self.journal.find_posting_by_sat_and_pair_id(creditName, debitP.pair_id)
    debitP.quantity = qty
    creditP.quantity = -Float(qty)
    logger.debug("updated total price for "+self.tradeable_m_symbol_root + "["+debitName+"] to "+debitP.quantity.to_s)
    debitP.save
    creditP.save
  
  end
end
