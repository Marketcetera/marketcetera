class DividendsController < ApplicationController
  include ApplicationHelper

  auto_complete_for :m_symbol, :root, {}

  def index
    list
    render :action => 'list'
  end

  # GETs should be safe (see http://www.w3.org/2001/tag/doc/whenToUseGet.html)
  verify :method => :post, :only => [ :destroy, :create, :update ],
         :redirect_to => { :action => :list }

  def list
    @dividend_pages, @dividends = paginate :dividends, :per_page => MaxPerPage
  end

  def show
    @dividend = Dividend.find(params[:id])
  end

  def new
    @dividend = Dividend.new
  end

  def create
    logger.debug("params[:dividend] "+params[:dividend].to_s)
    @dividend = Dividend.new(params[:dividend])
    @dividend.transaction() do
      begin
        @params = params
        @dividend.equity = Equity.get_equity(get_non_empty_string_from_two(params, :m_symbol, :root, nil))
        @dividend.currency = Currency.get_currency(get_non_empty_string_from_two(params, :currency, :alpha_code, nil))
        if @dividend.save
          flash[:notice] = 'Dividend was successfully created.'
          redirect_to :action => 'list'
        else
          throw Exception.new
        end
      rescue => ex
        logger.debug("exception in dividend save with errors: "+@dividend.errors.length.to_s + 
          " and ex is: "+ex.class.to_s + ":" + ex.message)
        logger.debug(ex.backtrace.join("\n"))
        render :action => 'new'
     end
    end
  end

  def edit
    @dividend = Dividend.find(params[:id])
  end

  def update
    @dividend = Dividend.find(params[:id])
    @dividend.equity = Equity.get_equity(params[:m_symbol][:root])
    @dividend.currency = Currency.get_currency(params[:currency][:alpha_code])
    if @dividend.update_attributes(params[:dividend])
      flash[:notice] = 'Dividend was successfully updated.'
      redirect_to :action => 'show', :id => @dividend
    else
      render :action => 'edit'
    end
  end


  def destroy
    Dividend.find(params[:id]).destroy
    redirect_to :action => 'list'
  end
end
