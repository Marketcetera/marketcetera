class CurrencyPairsController < ApplicationController
  include ApplicationHelper

  def auto_complete_for_first_currency_alpha_code
    auto_complete_responder_for_currency_alpha_code params[:first_currency][:alpha_code]
  end

  def auto_complete_for_second_currency_alpha_code
    auto_complete_responder_for_currency_alpha_code params[:second_currency][:alpha_code]
  end

  def index
    list
    render :action => 'list'
  end

  # GETs should be safe (see http://www.w3.org/2001/tag/doc/whenToUseGet.html)
  verify :method => :post, :only => [ :destroy, :create, :update ],
         :redirect_to => { :action => :list }

  def list
    @currency_pair_pages, @currency_pairs = paginate :currency_pairs, :per_page => MaxPerPage
  end

  def show
    @currency_pair = CurrencyPair.find(params[:id])
  end

  def new
    @currency_pair = CurrencyPair.new
  end

  def create
    first_currency = Currency.find(:first, :conditions => params[:first_currency])
    second_currency = Currency.find(:first, :conditions => params[:second_currency])
    @currency_pair = CurrencyPair.new(:description => params[:currency_pair][:description],
            :first_currency => first_currency, :second_currency => second_currency)
    if @currency_pair.save
      flash[:notice] = "Currency pair #{@currency_pair} was successfully created."
      redirect_to :action => 'list'
    else
      render :action => 'new'
    end
  end

  def edit
    @currency_pair = CurrencyPair.find(params[:id])
  end

  def update
    @currency_pair = CurrencyPair.find(params[:id])
    if @currency_pair.update_attributes(params[:currency_pair])
      flash[:notice] = 'Currency pair was successfully updated.'
      redirect_to :action => 'show', :id => @currency_pair
    else
      render :action => 'edit'
    end
  end

  def destroy
    a_currency_pair = CurrencyPair.find(params[:id])
    a_currency_pair.destroy
    redirect_to :action => 'list'
  end

end
