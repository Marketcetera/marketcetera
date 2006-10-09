class QueriesController < ApplicationController
  auto_complete_for :m_symbol, :root, {}
  
  
  def index
    
  end

  def by_symbol
    symbol_str = params[:m_symbol][:root]
    allTrades = Trade.find(:all)
    matched = (symbol_str == '') ? allTrades : allTrades.select{|oneTrade| oneTrade.tradeable.m_symbol.root == symbol_str}
    
    @trade_pages = Paginator.new(self, matched.length, 10, params[:page])
    @trades = matched[@trade_pages.current.offset .. @trade_pages.current.offset + 9] 
    
    render :template => 'trades/list'
  end
  
  def by_date
    allTrades = Trade.find(:all)
    date = Date.civil(params[:date]['trade_date(1i)'].to_i, params[:date]['trade_date(2i)'].to_i, params[:date]['trade_date(3i)'].to_i )
    logger.error("*****date is "+date.to_s)  
    matched = allTrades.select{|oneTrade| oneTrade.journal.post_date == date}
    
    @trade_pages = Paginator.new(self, matched.length, 10, params[:page])
    @trades = matched[@trade_pages.current.offset .. @trade_pages.current.offset + 9] 
    
    render :template => 'trades/list' 
  end
  
end
