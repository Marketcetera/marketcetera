module TradesHelper
  include ApplicationHelper
  include SubAccountsHelper
  include QF_BuyHelper
  
  
  TradeTypeTrade = 'T'
  TradeTypeReconciliation = 'R'
  TradeTypeCorporateAction = 'C'
  TradeTypeExerciseOrExpire = 'E'
  
  def get_human_trade_type(trade_type)
    case (trade_type)
    when TradeTypeTrade:
      return "Trade"
    when TradeTypeReconciliation:
      return "Reconciliation"
    when TradeTypeExerciseOrExpire:
      return "Exercise or expire"
    when TradeTypeCorporateAction:
      return "Corporate action"
    else
      return "Unknown: "+trade_type.to_s
    end
  end  
  
  # Asset Types
  AssetTypeEquity = 'E'
  AssetTypeEquityOption = 'O'
  
  def create_equity_trade(trade, symbol, price_per_share, 
                          total_commission, currency_alpha_code, account_nickname, trade_date)
    notional = trade.quantity * price_per_share
    total_commission = total_commission
    trade.tradeable = get_equity(symbol)
    trade.price_per_share = price_per_share
    logger.debug("creating a trade for "+symbol + " for "+notional.to_s + "/("+total_commission.to_s + ")")
  
    trade.account = Account.find_by_nickname(account_nickname)
    if(trade.account == nil)
      trade.account = Account.create(:nickname => account_nickname)
      logger.debug("created new account [" + account_nickname + "] and sub-accounts")
      trade.account.save
    end
    sub_accounts = trade.account.sub_accounts
    short_term_investment_sub_account = trade.account.find_sub_account_by_sat(SubAccountType::DESCRIPTIONS[:sti])
    cash_sub_account = trade.account.find_sub_account_by_sat(SubAccountType::DESCRIPTIONS[:cash])
    commission_sub_account = trade.account.find_sub_account_by_sat(SubAccountType::DESCRIPTIONS[:commissions])
    
    trade.journal = Journal.create( :post_date => trade_date )
    base_currency = get_currency(currency_alpha_code)
    trade.journal.postings << Posting.create(:journal=>trade.journal, :currency=>base_currency, :quantity=>notional, :sub_account=>short_term_investment_sub_account, :pair_id => 1)
    trade.journal.postings << Posting.create(:journal=>trade.journal, :currency=>base_currency, :quantity=>(-1*notional), :sub_account=>cash_sub_account, :pair_id => 1)
    trade.journal.postings << Posting.create(:journal=>trade.journal, :currency=>base_currency, :quantity=>total_commission, :sub_account=>commission_sub_account, :pair_id => 2)
    trade.journal.postings << Posting.create(:journal=>trade.journal, :currency=>base_currency, :quantity=>(-1*total_commission), :sub_account=>cash_sub_account, :pair_id => 2)
    
    trade.journal.save
    logger.debug("created and saved all related postings")
  end
  
end
