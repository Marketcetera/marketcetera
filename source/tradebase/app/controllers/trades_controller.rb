require 'bigdecimal'

class TradesController < ApplicationController
  include ApplicationHelper
  
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
    @trade_pages, @trades = paginate :trades, :per_page => 10
  end

  def show
    @trade = Trade.find(params[:id])
  end

  def new
    @trade = Trade.new
  end

  def create
    if(params[:m_symbol][:root].empty?)
      flash[:error] = 'Please specify the symbol.'
      redirect_to :action => 'new'
      return
    end
    @trade = Trade.new(:quantity => params[:trade][:quantity], :comment => params[:trade][:comment], 
                       :trade_type => params[:trade][:trade_type], :side => params[:trade][:side], 
                       :price_per_share => BigDecimal.new(params[:trade][:price_per_share]))
    if (!validate_trade_numbers(@trade, 'new')) 
      return
    end
    adjust_quantity_by_side(@trade)
    trade_date = parse_date_from_params(params, :trade, "journal_post_date")
    @trade.create_equity_trade(@trade.quantity, params[:m_symbol][:root], BigDecimal.new(params[:trade][:price_per_share]), 
        BigDecimal.new(params[:trade][:total_commission]), params[:currency][:alpha_code], params[:account][:nickname], trade_date)
    
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
    if(params[:m_symbol][:root].empty?)
      flash.now[:error] = 'Please specify the symbol.'
      redirect_to :action => 'edit'
      return
    end
    
    @trade = Trade.find(params[:id])
    @trade.tradeable_m_symbol_root = params[:m_symbol][:root]
    @trade.account_nickname = params[:account][:nickname]    
    @trade.journal_post_date = parse_date_from_params(params, :trade, :journal_post_date.to_s)
    @trade.side = params[:trade][:side]
    if (!validate_trade_numbers(@trade, 'edit')) 
      return
    end
    adjust_quantity_by_side(@trade)
    @trade.price_per_share = params[:trade][:price_per_share]
    @trade.comment = params[:trade][:comment]
    @trade.total_commission = params[:trade][:total_commission]
    @trade.trade_type = params[:trade][:trade_type]
    
    
    if(@trade.save)
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
  
  def validate_trade_numbers(trade, action)
    if(trade.quantity <= 0)
      flash[:error] = 'Please specify positive quantity.'
      redirect_to :action => action
      return false
    end
    if(trade.price_per_share <= 0)
      flash[:error] = 'Please specify positive price per share.'
      redirect_to :action => action
      return false
    end
    
    return true
  end
  
  def adjust_quantity_by_side(trade)
    if(trade.side != Quickfix::Side_BUY().to_i) 
      trade.quantity *= (-1)
    end
    return trade
  end
end
