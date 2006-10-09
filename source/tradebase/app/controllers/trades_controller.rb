require 'bigdecimal'

class TradesController < ApplicationController
  include ApplicationHelper
  include SubAccountsHelper
  include QF_BuyHelper
  include TradesHelper

  auto_complete_for :m_symbol, :root, {}
  auto_complete_for :account, :nickname, {}

  def index
    list
    render :action => 'list'
  end

  # GETs should be safe (see http://www.w3.org/2001/tag/doc/whenToUseGet.html)
  verify :method => :post, :only => [ :destroy, :create, :update ],
         :redirect_to => { :action => :list }

  def list
    logger.error("above paginate")
    @trade_pages, @trades = paginate :trades, :per_page => 10
    logger.error("below paginate")
  end

  def show
    @trade = Trade.find(params[:id])
  end

  def new
    @trade = Trade.new
  end

  def create
    @trade = Trade.new(params[:trade])
    
    trade2 =  Date.civil(params[:trade_date] )
    logger.error("trade2 is "+trade2.to_s)
    
    trade_date = Date.civil(params['trade_date(1i)'].to_i, params['trade_date(2i)'].to_i, params['trade_date(3i)'].to_i )
    create_equity_trade(@trade, params[:m_symbol][:root],
     BigDecimal(params[:per_share_price]), BigDecimal(params[:per_share_commission]),
     params[:currency][:alpha_code], params[:account][:nickname], trade_date)
    
    if @trade.save
      flash[:notice] = 'Trade was successfully created.'
      redirect_to :action => 'list'
    else
      render :action => 'new'
    end
  end

  def edit
    @trade = Trade.find(params[:id])
  end

  def update
    @trade = Trade.find(params[:id])
    if @trade.update_attributes(params[:trade])
      flash[:notice] = 'Trade was successfully updated.'
      redirect_to :action => 'show', :id => @trade
    else
      render :action => 'edit'
    end
  end

  def destroy
    Trade.find(params[:id]).destroy
    redirect_to :action => 'list'
  end
  
  
end
