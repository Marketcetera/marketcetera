class QueriesController < ApplicationController
  auto_complete_for :m_symbol, :root, {}
  auto_complete_for :account, :nickname, {}
  include ApplicationHelper
  
  def index
    # display the index page   
  end

  def positions
    # display positions index page
    render :template => 'queries/positions_queries'
  end

  def by_symbol
    symbol_str = get_non_empty_string_from_two(params, :m_symbol, :root, "m_symbol_root")
    if(symbol_str.blank?) 
      @trade_pages, @trades = paginate :trades, :per_page => MaxPerPage
    else
      @trade_pages, @trades  = paginate :trades, :per_page => MaxPerPage, 
                           :joins => 'as t, equities, m_symbols, journals',
                           :conditions => ['m_symbols.id=equities.m_symbol_id AND equities.id = t.tradeable_id AND '+
                                         't.journal_id = journals.id AND m_symbols.root = ? ', symbol_str],
                           :order => 'journals.post_date',
                           :select => 't.*'
    end
    if(@trades.empty?)
      flash.now[:error] = "No Matching trades were found for symbol [#{symbol_str}]"                                       
    end
    
    @param_name = :m_symbol_root
    @param_value = symbol_str
    @query_type = "Symbol"
    
    render :template => 'queries/queries_output'
  end
  
  def on_date
    @on_date = VDate.get_date_from_params(params, :date, "on", "on_date").as_date
    by_date_helper(@on_date, @on_date)
  end
  
  def by_date
    @from_date = VDate.get_date_from_params(params, :date, "from", "from_date").as_date
    @to_date = VDate.get_date_from_params(params, :date, "to", "to_date").as_date
    by_date_helper(@from_date, @to_date)
  end

  def by_account
    nickname = get_non_empty_string_from_two(params, :account, :nickname, "nickname")
    if(nickname.nil? || nickname.empty?) 
      @trade_pages, @trades = paginate :trades, :per_page => MaxPerPage
    else
      @trade_pages, @trades  = paginate :trades, :per_page => MaxPerPage, :conditions => ['accounts.nickname = ? ', nickname], 
                           :joins => 'inner join accounts on accounts.id = account_id', 
                           :select => 'trades.*'
                           
    end                           

    if(@trades.empty?)
      flash.now[:error] = "No Matching trades were found for account: [#{nickname}]"                                       
    end
    @param_name = "nickname"
    @param_value = nickname
    @query_type = "Account"
    
    render :template => 'queries/queries_output'
  end
  
  private
  def by_date_helper(from_date, to_date)
      @trade_pages, @trades  = paginate :trades, :per_page => MaxPerPage, 
                         :conditions => ['post_date >= ? and post_date <= ?', from_date, to_date], 
                         :joins => 'as t inner join journals on journals.id = t.journal_id', 
                         :select => 't.*'

    if(@trades.empty?)
      flash.now[:error] = "No trades were found in range  [#{from_date} to #{to_date}]"                                       
    end
  end
end
