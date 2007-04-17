class MarksController < ApplicationController
  include ApplicationHelper

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
    if(symbol_str.blank?)
      flash[:error] = "Please specify a symbol."
      redirect_to :action => 'index'
      return
    end
    equity = Equity.get_equity(symbol_str, false)
    @from_date = get_date_from_params(params, :date, "from", "from_date")
    @to_date = get_date_from_params(params, :date, "to", "to_date")
    if(@to_date.blank?) 
      @to_date = Date.today 
    end
    if(@from_date.blank?)
      conditionsArr = ['mark_date <= ? and equities.id= ?', @to_date, equity]
    else 
      conditionsArr = ['mark_date >= ? and mark_date <= ? and equities.id= ?', @from_date, @to_date, equity]
    end

    @mark_pages, @marks = paginate :marks, :per_page => MaxPerPage, 
            :conditions => conditionsArr,
            :joins => 'as m inner join equities on equities.id = m.equity_id',
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
    @on_date = get_date_from_params(params, :date, "on", "on_date") 
    if(@on_date.blank?)
      @on_date = Date.today
    end
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
        @mark.equity = Equity.get_equity(get_non_empty_string_from_two(params, :m_symbol, :root, nil))
        if @mark.save
          flash[:notice] = 'Mark was successfully created.'
          redirect_to :action => 'by_symbol', :m_symbol_root => @mark.equity.m_symbol.root, :to_date => Date.today
        else
          throw Exception.new
        end
      rescue => ex
        logger.debug("exception in mark save with errors: "+@mark.errors.length.to_s + 
          " and ex is: "+ex.class.to_s + ":" + ex.message)
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
