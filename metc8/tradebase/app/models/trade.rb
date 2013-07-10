class Trade < ActiveRecord::Base
  include ApplicationHelper

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
      errors.add(:symbol, "cannot be empty")
    end
    
    if(side.nil? || !Side::QF_SIDE_CODE.invert.key?(side.to_s))
      errors.add(:side, "is unknown")
    end

    if(quantity.blank? ||
      ((quantity <= 0 && side == Side::QF_SIDE_CODE[:buy].to_i) ||
      (quantity >= 0 && side != Side::QF_SIDE_CODE[:buy].to_i)))
      logger.debug("failed validation of trade: price/qty/side: "+price_per_share.to_s + "/"+
                quantity.to_s+"/"+Side.get_human_side(side))
      errors.add(:quantity, 'Please specify positive quantity.')
    end    
    
    if(price_per_share.blank? || price_per_share <= 0)
      errors.add(:price_per_share, 'Please specify positive price per share.')
    end
    
    if(!total_commission.blank? && BigDecimal(total_commission.to_s) < 0)
      errors.add(:total_commission, "Commissions cannot be negative")
    end
  end 

  # delete the journal after our deletion to avoid constraint violations
  def after_destroy
    self.journal.destroy if !self.journal.nil?
  end

  def journal_post_date
    (self.journal.nil?) ? nil : self.journal.post_date
  end
  
  def journal_post_date_local_TZ
   date = (self.journal.nil?) ? nil : self.journal.post_date
   if(date.nil?)
     return nil
   end
   ld = LOCAL_TZ.utc_to_local(date)
   # figure out what timezone that time was and append that (ie PST or PDT for that date)
   ld.strftime(DATETIME_FORMAT) + " " +Time.local(ld.year, ld.month, ld.day, ld.hour, ld.min, ld.sec).zone
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
  
  # for now, we deal with equities only
  def tradeable_m_symbol_root=(inSymbol)
    self.tradeable = Equity.get_equity(inSymbol)
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
  
  # When an account is updated make sure you update all the refrenced postings as well
  def after_update
    logger.debug("**** currently self.account is "+((self.account.nil?) ? 'nil': self.account.to_s))
    if(!self.journal.nil?)
      if(!self.account.nil?)
        logger.debug("updating " + self.journal.postings.length.to_s + " postings from old acct "+
            self.journal.postings[0].sub_account.account.to_s + " to new acct "+self.account.to_s)
      end           
      self.journal.postings.each { |p| p.sub_account.account = self.account} 
    end
  end
  
  # set the position_qty to the side-adjusted quantity
  def before_save
    self.position_qty = self.quantity
  end
  
  def total_commission
    if(self.journal.nil?) 
      return 0
    else 
      commissions = self.journal.find_posting_by_sat(SubAccountType::DESCRIPTIONS[:commissions])
      return commissions.quantity
    end
  end
    
  def total_commission=(inCommission)
    if(self.journal.nil?)
      logger.debug("we are not initialized yet, so jumping out early[commission]")
      return
    end
    if(inCommission.blank?) 
      inCommission = 0
    end
    update_underlying_posting_pairs(SubAccountType::DESCRIPTIONS[:commissions], SubAccountType::DESCRIPTIONS[:cash], 
                                    inCommission, Posting::CommissionsPairID)
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
    
    update_underlying_posting_pairs(get_notional_account_name(), SubAccountType::DESCRIPTIONS[:cash],
                                    self.quantity * BigDecimal(new_price.to_s), Posting::NotionalPairID)
  end
  
  # Need to update the underlying journals/postings when we update the price_per_share
  def quantity=(new_qty)
    super(new_qty)
    logger.debug("updated qty to "+new_qty.to_s)
    if(!self.journal.nil?)
      update_underlying_posting_pairs(get_notional_account_name(), SubAccountType::DESCRIPTIONS[:cash],
                                      self.quantity * self.price_per_share, Posting::NotionalPairID)
    end
  end    

  # Subclasses should override in case they store the costs in subaccounts other than STI
  def get_notional_account_name
    SubAccountType::DESCRIPTIONS[:sti]
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

  # Produces short summary: B 1000 IBM 94.32 
  # or SS 1000 IBM 23.34
  # Always return absolute value of qty here since we have the leading side code 
  def summary
    qty_str = (self.quantity.nil?) ? '<no qty>' : self.quantity.abs.to_s
    Side::SIDE_SHORT_CODE[self.side] + " " + qty_str + " " + self.tradeable_m_symbol_root+" "+self.price_per_share.to_s
  end

  # Override the to_s function to print something more useful for debugging
  # same as summary, but with account added
  def to_s
    qty_str = (self.quantity.nil?) ? '<no qty>' : self.quantity.to_s
    "["+Side.get_human_side(self.side.to_s)+"] "+ qty_str + 
    " <"+self.tradeable_m_symbol_root+"> @"+self.price_per_share.to_s + " in acct ["+self.account_nickname+"]"
  end
  
  ##### Helper Methods #####
   # Note that trade_date here is a string and not a Date object as all callers format it using to_s(:db)
   def create_trade_journal(total_commission, currency_alpha_code, trade_date, description)
      notional = self.quantity * self.price_per_share
      logger.debug("creating a trade for "+self.tradeable_m_symbol_root + " for "+notional.to_s + "/("+total_commission.to_s + ")")
      sub_accounts = self.account.sub_accounts
      short_term_investment_sub_account = self.account.find_sub_account_by_sat(SubAccountType::DESCRIPTIONS[:sti])
      cash_sub_account = self.account.find_sub_account_by_sat(SubAccountType::DESCRIPTIONS[:cash])
      commission_sub_account = self.account.find_sub_account_by_sat(SubAccountType::DESCRIPTIONS[:commissions])

      self.journal = Journal.create( :post_date => trade_date, :description => description )
      base_currency = Currency.get_currency(currency_alpha_code)
      self.journal.postings << Posting.create(:journal=>self.journal, :currency=>base_currency,
                                              :quantity=>notional, :sub_account=>short_term_investment_sub_account,
                                              :pair_id => Posting::NotionalPairID)
      self.journal.postings << Posting.create(:journal=>self.journal, :currency=>base_currency,
                                              :quantity=>(-1*notional), :sub_account=>cash_sub_account,
                                              :pair_id => Posting::NotionalPairID)

      # deal with commissions
      self.journal.postings << Posting.create(:journal=>self.journal, :currency=>base_currency,
                                              :quantity=>total_commission, :sub_account=>commission_sub_account,
                                              :pair_id => Posting::CommissionsPairID)
      self.journal.postings << Posting.create(:journal=>self.journal, :currency=>base_currency,
                                              :quantity=>(-1*total_commission), :sub_account=>cash_sub_account,
                                              :pair_id => Posting::CommissionsPairID)
      self.journal.save

      logger.debug("created and saved all related postings")
 end

 # Incoming qty can be negative (if, for example, we are doing pre-created @trade.qty
 # That's OK, we just do an abs() on that and rely on the qty/side combo to determine whether 
 # the qty is positive or negative
 def create_trade(inQuantity, symbol, price_per_share, 
                          total_commission, currency_alpha_code, account_nickname, trade_date)
    self.price_per_share = BigDecimal(price_per_share.to_s)
    self.quantity = BigDecimal(inQuantity.to_s).abs

    self.tradeable_m_symbol_root = symbol
    if(!valid?) 
      logger.debug("Skipping out on create_trade b/c trade doesn't validate with errors: "+self.errors.full_messages().to_s)
      return false
    end

    total_commission = BigDecimal(total_commission.to_s)
    self.account = Account.find_by_nickname(account_nickname)
    if(self.account.nil?)
      self.account = Account.create(:nickname => account_nickname, :institution_identifier => account_nickname)
      logger.debug("created new account [" + account_nickname + "] and sub-accounts and self.account is "+self.account.to_s)
      self.account.save
    end
    logger.debug("Using account " + self.account.to_s)
    description = Side.get_human_side(self.side) + " " + inQuantity.to_s + " " + self.tradeable_m_symbol_root + " " + price_per_share.to_s
    create_trade_journal(total_commission, currency_alpha_code, trade_date, description)
    return true
  end
  
  def update_underlying_posting_pairs(debitName, creditName, qty, pair_id)
    logger.debug("updating the posting transactions when updating trade for postings: "+debitName+"/"+creditName + " for total: "+qty.to_s)
    debitP = self.journal.find_posting_by_sat_and_pair_id(debitName, pair_id)
    creditP = self.journal.find_posting_by_sat_and_pair_id(creditName, pair_id)
    debitP.quantity = qty
    creditP.quantity = -BigDecimal(qty.to_s)
    logger.debug("updated total price for "+self.tradeable_m_symbol_root + "["+debitName+"] to "+debitP.quantity.to_s)
    debitP.save
    creditP.save
  
  end
  
  # gets the currency code for this trade (assumption is that all postings have same currency)
  def currency_alpha_code
    (self.journal.nil?) ? nil : self.journal.postings[0].currency.alpha_code
  end
  
  # setting the currency just updates all the postings to that currency
  def currency= (inCur)
    if(!self.journal.nil?)
      self.journal.postings.each{ |p| p.currency = inCur }
    end
  end
end
