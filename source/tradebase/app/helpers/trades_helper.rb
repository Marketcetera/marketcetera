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
                          per_share_commission, currency_alpha_code, account_nickname, trade_date)
    notional = trade.quantity * price_per_share
    total_commission = trade.quantity.abs() * per_share_commission
    logger.error("trade so far: "+trade.to_s)
    equity = get_equity(symbol)
    trade.tradeable = equity
    trade.price_per_share = price_per_share
  
    trade.account = get_account_by_nickname(account_nickname)
    if(trade.account == nil)
      trade.account = Account.new(:nickname => account_nickname)
      fill_in_sub_accounts(trade.account)
      trade.account.save
      logger.error("no account found, creating new one with all subaccounts")
    end
    sub_accounts = trade.account.sub_accounts
    short_term_investment_sub_account = sub_accounts.select {|a| a.sub_account_type.description == ShortTermInvestmentDescription}[0]
    cash_sub_account = sub_accounts.select {|a| a.sub_account_type.description == CashDescription}[0]
    commission_sub_account = sub_accounts.select {|a| a.sub_account_type.description == CommissionsDescription}[0]
    
    trade.journal = Journal.new( :post_date => trade_date )
    base_currency = get_currency(currency_alpha_code)
    short_term_investment_posting = Posting.new(:journal=>trade.journal, :currency=>base_currency, :quantity=>notional, :sub_account=>short_term_investment_sub_account, :pair_id => 1)
    cash_notional_posting = Posting.new(:journal=>trade.journal, :currency=>base_currency, :quantity=>(-1*notional), :sub_account=>cash_sub_account, :pair_id => 1)
    commission_posting = Posting.new(:journal=>trade.journal, :currency=>base_currency, :quantity=>total_commission, :sub_account=>commission_sub_account, :pair_id => 2)
    cash_commission_posting = Posting.new(:journal=>trade.journal, :currency=>base_currency, :quantity=>(-1*total_commission), :sub_account=>cash_sub_account, :pair_id => 2)
    
    trade.journal.postings.push(short_term_investment_posting)
    trade.journal.postings.push(cash_notional_posting)
    trade.journal.postings.push(commission_posting)
    trade.journal.postings.push(cash_commission_posting)
  end
  
end
