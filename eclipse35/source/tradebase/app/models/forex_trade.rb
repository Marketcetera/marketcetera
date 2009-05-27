class ForexTrade < Trade

  def tradeable_m_symbol_root=(inSymbol)
    self.tradeable = CurrencyPair.get_currency_pair(inSymbol, true)
  end

  def tradeable_m_symbol_root
    (self.tradeable.nil?) ? nil : self.tradeable.to_s
  end

  # Creates the postings associated with this trade.
  # Overrides the basic trade behaviour
  def create_trade_journal(total_commission, currency_alpha_code, trade_date, description)
    other_curr_qty = self.quantity * self.price_per_share
    logger.debug("creating a forex trade for "+self.tradeable.to_s + " for "+other_curr_qty.to_s + "/("+total_commission.to_s + ")")
    sub_accounts = self.account.sub_accounts
    cash_sub_account = self.account.find_sub_account_by_sat(SubAccountType::DESCRIPTIONS[:cash])
    commission_sub_account = self.account.find_sub_account_by_sat(SubAccountType::DESCRIPTIONS[:commissions])

    self.journal = Journal.create( :post_date => trade_date, :description => description )

    self.journal.postings << Posting.create(:journal=>self.journal, :currency=>self.tradeable.first_currency,
                                            :quantity=>self.quantity, :sub_account=>cash_sub_account, :pair_id => 1)

    self.journal.postings << Posting.create(:journal=>self.journal, :currency=>self.tradeable.second_currency,
                                            :quantity=>(-1*other_curr_qty), :sub_account=>cash_sub_account, :pair_id => 1)

    # deal with commissions
    commissions_currency = Currency.get_currency(currency_alpha_code)
    self.journal.postings << Posting.create(:journal=>self.journal, :currency=>commissions_currency,
                                            :quantity=>total_commission, :sub_account=>commission_sub_account, :pair_id => 2)
    self.journal.postings << Posting.create(:journal=>self.journal, :currency=>commissions_currency,
                                            :quantity=>(-1*total_commission), :sub_account=>cash_sub_account, :pair_id => 2)
    self.journal.save

    logger.debug("created and saved all related postings")
  end

  # Forex stores everything in cash
  def get_notional_account_name
    SubAccountType::DESCRIPTIONS[:cash]
  end
end