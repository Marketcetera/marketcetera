class MarksController < ApplicationController
  include ApplicationHelper, ActionView::Helpers::ActiveRecordHelper
    
  auto_complete_for :m_symbol, :root, {}

  def index
    # display the index page   
  end

  # GETs should be safe (see http://www.w3.org/2001/tag/doc/whenToUseGet.html)
  verify :method => :post, :only => [ :destroy, :create, :update ],
         :redirect_to => { :action => :index }

  # List of all the marks for a particular symbol for the date range specified
  def by_symbol
      symbol_str = get_non_empty_string_from_two(params, :m_symbol, :root, "m_symbol_root")
      suffix = (params[:suffix].nil?) ? '' : params[:suffix]
      @report = MarksBySymbol.new(symbol_str, params, suffix)
      if (params[:security_type] == TradesHelper::SecurityTypeEquity)
        theSymbol = Equity.get_equity(symbol_str, false)
        join_table = "equities "
      else
        begin
          theSymbol = CurrencyPair.get_currency_pair(symbol_str, false)
        rescue UnknownCurrencyPairException => ex
          @report.unknown_currency_pair = true
        end
        join_table = "currency_pairs "
      end
      @report.validate()
      if(!@report.valid?)
        render :action => :index
        return
      end
      
      @to_date, @from_date = @report.to_date.as_date, @report.from_date.as_date
      @mark_pages, @marks = paginate :marks, :per_page => MaxPerPage, 
              :conditions => ['mark_date >= ? and mark_date <= ? and tradeable.id= ?', @from_date, @to_date, theSymbol],
              :joins => 'as m inner join ' + join_table + "as tradeable on tradeable.id = m.tradeable_id",
               :select => 'm.*'

    @param_name = :m_symbol_root
    @param_value = symbol_str
                
    if(@marks.empty?)
      flash.now[:error] = "No marks were found for [#{symbol_str}] from #{@from_date} to #{@to_date}"                                       
    end
    render :template => 'marks/list_by_symbol'
  end
  
  # List of all marks on the specified date
  def on_date
    @report = ReportOnDate.new(params)
    if(!@report.valid?)
        render :action => :index
        return
    end
    @on_date = @report.on_date.as_date
    @mark_pages, @marks = paginate :marks, :per_page => MaxPerPage, :conditions => [ 'mark_date = ?', @on_date]

    @param_name = :m_symbol_root
    @param_value = @on_date

    if(@marks.empty?)
      flash.now[:error] = "No marks were found on #{@on_date}"
    end
    render :template => 'marks/list_on_date'
  end   


  def show
    @mark = Mark.find(params[:id])
  end

  def new
    @mark = Mark.new
  end

  # used for creation of missing marks - the mark data is prepopulated
  def new_missing
    flash.clear
    if(params[:tradeable_type] == CurrencyPair.name)
      @mark = ForexMark.new(:mark_date => params[:mark_date])
      if (params[:tradeable_id].blank?)
        @mark.tradeable = CurrencyPair.get_currency_pair(params[:currency_pair])
      else 
        @mark.tradeable = CurrencyPair.find(params[:tradeable_id])
      end
    else
      @mark = Mark.new(:mark_date => params[:mark_date], :tradeable_id => params[:tradeable_id])
      @mark.tradeable = Equity.find(params[:tradeable_id])
    end
    render :template => 'marks/new'
  end

  def create
    Mark.transaction() do
      begin
        symbol = get_non_empty_string_from_two(params, :m_symbol, :root, nil)
        if (params[:security_type] == TradesHelper::SecurityTypeEquity)
          @mark = Mark.new(params[:mark])
          @mark.tradeable = Equity.get_equity(symbol)
        else
          @mark = ForexMark.new(params[:mark])
          @mark.tradeable = CurrencyPair.get_currency_pair(symbol, true)
        end

        if @mark.save
          flash[:notice] = 'Mark was successfully created.'
          # show all marks for symbol from created mark's date till today 
          redirect_to :action => 'by_symbol', :m_symbol_root => @mark.tradeable_m_symbol_root, :security_type => params[:security_type],
                                              :to_date => Date.today, :from_date => @mark.mark_date
        else
          throw Exception.new
        end
      rescue UnknownCurrencyPairException => ucpex
        if(@mark.errors.length == 0)
          @mark.errors.add(:symbol, ucpex.message)
        end
        logger.debug("createTrade encountered error: "+ucpex.message)
        render :action => 'new'
      rescue => ex
        logger.debug("exception in mark save with errors: "+@mark.errors.length.to_s + 
          " and ex is: "+ex.class.to_s + ":" + ex.message)
        logger.debug(ex.backtrace.join("\n"))
        render :action => 'new'
     end
    end
  end

  def edit
    @mark = Mark.find(params[:id])
  end

  def update
    @mark = Mark.find(params[:id])
    if @mark.update_attributes(params[:mark])
      flash[:notice] = 'Mark was successfully updated.'
      redirect_to :action => 'show', :id => @mark
    else
      render :action => 'edit'
    end
  end

  def destroy
    m = Mark.find(params[:id])
    symbol = m.tradeable_m_symbol_root
    m.destroy
    flash[:notice] = "Mark was successfully deleted."
    redirect_to :action => 'index'
  end
end
