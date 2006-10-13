class QueriesController < ApplicationController
  auto_complete_for :m_symbol, :root, {}
  auto_complete_for :account, :nickname, {}
  include ApplicationHelper
  
  def index
    # display the index page   
  end

  def by_symbol
    symbol_str = get_non_empty_string_from_two(params, :m_symbol, :root, "m_symbol_root")
    if(symbol_str.nil? || symbol_str.empty?) 
      @trade_pages, @trades = paginate :trades, :per_page => 10
    else
      @trade_pages, @trades  = paginate :trades, :per_page => 10, :conditions => ['m_symbols.root = ? ', symbol_str], 
                           :joins => 'as t inner join ( equities inner join m_symbols on m_symbols.id=equities.m_symbol_id) on equities.id = t.tradeable_id'
    end
    if(@trades.empty?)
      flash.now[:error] = "No Matching trades were found for symbol [#{symbol_str}]"                                       
    end
    
    @param_name = :m_symbol_root
    @param_value = symbol_str
    
    render :template => 'queries/queries_output'
  end
  
  def by_date
    @from_date  = get_date_from_params(params, :date, "from", "from_date")
    @to_date    = get_date_from_params(params, :date, "to", "to_date")

    @trade_pages, @trades  = paginate :trades, :per_page => 10, 
                         :conditions => ['post_date >= ? and post_date <= ?', @from_date, @to_date], 
                         :joins => 'as t inner join journals on journals.id = t.journal_id'

    if(@trades.empty?)
      flash.now[:error] = "No trades were found in range  [#{@from_date} to #{@to_date}]"                                       
    end
  end
  
  def by_account
    nickname = get_non_empty_string_from_two(params, :account, :nickname, "nickname")
    if(nickname.nil? || nickname.empty?) 
      @trade_pages, @trades = paginate :trades, :per_page => 10
    else
      @trade_pages, @trades  = paginate :trades, :per_page => 10, :conditions => ['accounts.nickname = ? ', nickname], 
                           :joins => 'as t inner join accounts on accounts.id = t.account_id'
    end                           

    if(@trades.empty?)
      flash.now[:error] = "No Matching trades were found for account: [#{nickname}]"                                       
    end
    @param_name = "nickname"
    @param_value = nickname
    
    render :template => 'queries/queries_output'
  end
  
  # Take 2 params: either the nested hash or a flat varname stringified date and return whichever is setup, 
  # giving the nested one preference
  def get_date_from_params(params, nested_parent, nested_child, secondary)
    parent = params[nested_parent]
    logger.debug "have parent:  " + parent.to_s
    if(!parent.nil?)
        return parse_date_from_params(params, nested_parent, nested_child)
    end
    return Date.parse(params[secondary])
  end  
end
