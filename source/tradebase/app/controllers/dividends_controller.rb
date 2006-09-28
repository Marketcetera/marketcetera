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
    @dividend_pages, @dividends = paginate :dividends, :per_page => 10
  end

  def show
    @dividend = Dividend.find(params[:id])
  end

  def new
    @dividend = Dividend.new
  end

  def create
   begin
    @dividend = Dividend.new(params[:dividend])
    @params = params
    @dividend.equity = get_equity(params[:m_symbol][:root])
    @dividend.currency = get_currency(params[:currency][:alpha_code])
    if @dividend.save
      flash[:notice] = 'Dividend was successfully created.'
      redirect_to :action => 'list'
    else
      render :action => 'new'
    end
    
    rescue SyntaxError
      flash[:notice] = 'Please specify the symbol'
      redirect_to :action => 'new'
   end
  end

  def edit
    @dividend = Dividend.find(params[:id])
  end

  def update
    @dividend = Dividend.find(params[:id])
    @dividend.equity = get_equity(params[:m_symbol_root])
    @dividend.currency = get_currency(params[:currency][:alpha_code])
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
