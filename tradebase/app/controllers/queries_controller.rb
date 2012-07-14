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

  # Construct a query based on the incoming parameters.
  # Resulting query is always ordered by date, and account and symbol if we specify those in the search criteria
  def trade_search
    @symbol_str = get_non_empty_string_from_two(params, :m_symbol, :root, "m_symbol_root")
    @nickname = get_non_empty_string_from_two(params, :account, :nickname, "nickname")
    all_dates = checked?(:all_dates, "yes")
    suffix = (params[:suffix].nil?) ? '' : params[:suffix]
    @report = ReportWithToFromDates.new(params, suffix)

    queryParams = []
    joinsLine = "as t"
    conditionsLine, orderLine = "", ""
    if(!@symbol_str.blank?)
      # check if Forex symbol
      begin
        currencyPair = CurrencyPair.get_currency_pair(@symbol_str)
        queryParams << currencyPair
        joinsLine += ", currency_pairs"
        conditionsLine += " AND currency_pairs.id = ? AND currency_pairs.id = t.tradeable_id AND t.tradeable_type = 'CurrencyPair' "
      rescue UnknownCurrencyPairException => ex
        # guess what? not a forex symbol. try equities instead
        queryParams << @symbol_str
        joinsLine += ", equities, m_symbols"
        conditionsLine += "AND m_symbols.id=equities.m_symbol_id AND equities.id = t.tradeable_id AND m_symbols.root = ? " +
                          "AND t.tradeable_type = 'Equity' "
      end
    end
    if(!@nickname.blank?)
        queryParams << @nickname
        joinsLine += ", accounts"
        conditionsLine += "AND t.account_id = accounts.id AND accounts.nickname = ? "
    end
    if(!all_dates)
        if(!@report.valid?)
            render :action => :index
            return
        end
        @from_date, @to_date = @report.from_date.as_date, @report.to_date.as_date
        queryParams << @from_date
        # since we are dealing with dates and not DateTime, make sure to check entire "today", ie until next day for to_date
        queryParams << @to_date+1
        conditionsLine += 'AND post_date >= ? AND post_date < ?'
    end
      @trade_pages, @trades  = paginate :trades, :per_page => MaxPerPage,
                           :joins =>  joinsLine + ", journals",
                           :conditions => ['t.journal_id = journals.id ' + conditionsLine, queryParams].flatten,
                           :order => 'journals.post_date DESC' + orderLine,
                           :select => 't.*'

    if(@trades.empty?)
      flash.now[:error] = "No Matching trades were found for your search criteria."
    end
    
    render :template => 'queries/queries_output'
  end
end
