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
      equity = Equity.get_equity(symbol_str, false)
      suffix = (params[:suffix].nil?) ? '' : params[:suffix]
      @report = MarksBySymbol.new(symbol_str, params, suffix)
      @report.validate()
      if(!@report.valid?)
        render :action => :index
        return
      end
      
      @to_date, @from_date = @report.to_date.as_date, @report.from_date.as_date
      @mark_pages, @marks = paginate :marks, :per_page => MaxPerPage, 
              :conditions => ['mark_date >= ? and mark_date <= ? and equities.id= ?', @from_date, @to_date, equity],
              :joins => 'as m inner join equities on equities.id = m.tradeable_id',
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

  def create
    @mark = Mark.new(params[:mark])
    @mark.transaction() do
      begin
        @mark.tradeable = Equity.get_equity(get_non_empty_string_from_two(params, :m_symbol, :root, nil))
        if @mark.save
          flash[:notice] = 'Mark was successfully created.'
          redirect_to :action => 'by_symbol', :m_symbol_root => @mark.tradeable.m_symbol.root,
                                              :to_date => Date.today, :from_date => Date.today
        else
          throw Exception.new
        end
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
    symbol = m.equity_m_symbol_root
    m.destroy
    redirect_to :action => 'by_symbol', :m_symbol_root => symbol, :to_date => Date.today
  end
end
