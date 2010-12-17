class CurrenciesController < ApplicationController
  def index
    list
    render :action => 'list'
  end

  # GETs should be safe (see http://www.w3.org/2001/tag/doc/whenToUseGet.html)
  verify :method => :post, :only => [ :destroy, :create, :update ],
         :redirect_to => { :action => :list }

  def list
    @currency_pages, @currencies = paginate :currencies, :per_page => MaxPerPage
  end

  def show
    @currency = Currency.find(params[:id])
  end
end
