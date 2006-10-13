class QueriesController < ApplicationController
  auto_complete_for :m_symbol, :root, {}
  auto_complete_for :account, :nickname, {}
  include ApplicationHelper
  
  def index
    # display the index page   
  end

  def by_symbol
    symbol_str = params[:m_symbol][:root]
    allTrades = Trade.find(:all)
    matched = (symbol_str == '')  ? allTrades : allTrades.select{ |oneTrade|
                                       (!oneTrade.tradeable.nil?) && (oneTrade.tradeable.m_symbol.root == symbol_str) }
    @trade_pages = Paginator.new(self, matched.length, 10, params[:page])
    @trades = matched[@trade_pages.current.offset .. @trade_pages.current.offset + 9] 
    
    render :template => 'trades/list'
  end
  
  def by_date
    allTrades = Trade.find(:all)
    date = parse_date_from_params(params, :date, "trade_date")
    matched = allTrades.select{|oneTrade| oneTrade.journal.post_date == date}
    
    @trade_pages = Paginator.new(self, matched.length, 10, params[:page])
    @trades = matched[@trade_pages.current.offset .. @trade_pages.current.offset + 9] 
    
    render :template => 'trades/list' 
  end
  
  def by_account
    allTrades = Trade.find(:all)
    acct = Account.find_by_nickname(params[:account][:nickname])
    matched = allTrades.select{|oneTrade| oneTrade.account == acct}
    
    @trade_pages = Paginator.new(self, matched.length, 10, params[:page])
    @trades = matched[@trade_pages.current.offset .. @trade_pages.current.offset + 9] 
    
    render :template => 'trades/list' 
  end
  
end
