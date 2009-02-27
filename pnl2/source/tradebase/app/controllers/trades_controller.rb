require 'bigdecimal'

class TradesController < ApplicationController
  include ApplicationHelper
  
  auto_complete_for :m_symbol, :root, {}
  auto_complete_for :account, :nickname, {}
  auto_complete_for :trade, :strategy

  def index
    list
    render :action => 'list'
  end

  # GETs should be safe (see http://www.w3.org/2001/tag/doc/whenToUseGet.html)
  verify :method => :post, :only => [ :destroy, :create, :update ],
         :redirect_to => { :action => :list }

  def list
    @trade_pages, @trades = paginate :trades, :per_page => MaxPerPage,
                            :select => "t.*",
                            :joins => "as t, journals j, accounts a",
                            :conditions => "t.journal_id = j.id AND t.account_id = a.id",
                            :order => "j.post_date, a.nickname"
  end

  def show
    @trade = Trade.find(params[:id])
  end

  def new
    @trade = Trade.new
  end

  def create
    begin
      if (params[:security_type] == TradesHelper::SecurityTypeEquity)
        @trade = Trade.new(:quantity => get_non_empty_string_from_two(params, :trade, :quantity, nil),
                :comment => params[:trade][:comment],
                :trade_type => params[:trade][:trade_type], :side => params[:trade][:side],
                :price_per_share => params[:trade][:price_per_share],
                :strategy => params[:trade][:strategy])
      else
        if (params[:security_type] == TradesHelper::SecurityTypeForex)
          @trade = ForexTrade.new(:quantity => get_non_empty_string_from_two(params, :trade, :quantity, nil),
                  :comment => params[:trade][:comment],
                  :trade_type => params[:trade][:trade_type], :side => params[:trade][:side],
                  :price_per_share => params[:trade][:price_per_share],
                  :strategy => params[:trade][:strategy])
        else
          @trade = Trade.new(:type => nil)
          @trade.errors.add(:security_type, "#{params[:security_type]} is unknown")
          throw Exception.new
        end
      end
      logger.debug("initial trade creation, qty is: "+@trade.quantity.to_s)
      Trade.transaction() do
        trade_date = VDate.parse_date_from_params(params, :trade, "journal_post_date").as_date
        symbol = get_non_empty_string_from_two(params, :m_symbol, :root, nil)
        total_commission = params[:trade][:total_commission]
        currency_alpha_code = get_non_empty_string_from_two(params, :currency, :alpha_code, nil)
        account_nickname = get_non_empty_string_from_two(params, :account, :nickname, nil)
        @trade.create_trade(
                          @trade.quantity,
                          symbol,
                          @trade.price_per_share,
                          total_commission,
                          currency_alpha_code,
                          account_nickname,
                          Time.parse(trade_date.to_s).utc.to_s(:db))
        logger.debug("after createEqtyTrade, qty is: "+@trade.quantity.to_s)

        if @trade.save
          flash[:notice] = 'Trade was successfully created.'
          logger.debug("created trade: "+@trade.to_s)
          redirect_to :action => 'list'
        else
          logger.debug("trade not created: "+collect_errors_into_string(@trade.errors))
          throw Exception.new
        end
      end
    rescue UnknownCurrencyPairException => ucpex
      if(@trade.errors.length == 0)
        @trade.errors.add(:symbol, ucpex.message)
      end
      logger.debug("createTrade encountered error: "+ucpex.message)
      render :action => 'new'
    rescue => ex
      logger.debug("createTrade encountered error: "+ex.message + ":\n"+ex.backtrace.join("\n"))
      render :action => 'new'
    end
  end

  def edit
    @trade = Trade.find(params[:id])
  end

  def update
    @trade = Trade.find(params[:id])
    begin
      @trade.transaction do
        @trade.tradeable_m_symbol_root = get_non_empty_string_from_two(params, :m_symbol, :root, nil)
        @trade.account_nickname = get_non_empty_string_from_two(params, :account, :nickname, nil)
        @trade.currency = Currency.get_currency(get_non_empty_string_from_two(params, :currency, :alpha_code, nil))
        @trade.journal_post_date = VDate.parse_date_from_params(params, :trade, :journal_post_date.to_s).as_date
        @trade.side = params[:trade][:side]
        @trade.quantity = params[:trade][:quantity]
        @trade.price_per_share = params[:trade][:price_per_share]
        @trade.comment = params[:trade][:comment]
        @trade.total_commission = params[:trade][:total_commission]
        @trade.trade_type = params[:trade][:trade_type]
        @trade.strategy = params[:trade][:strategy]
        if(@trade.save)
          flash[:notice] = 'Trade was successfully updated.'
          redirect_to :action => 'show', :id => @trade
        else
          logger.debug("trade not created, nErrors: "+@trade.errors.length.to_s)
          throw Exception.new
        end
      end 
    rescue => ex
      logger.debug("updateTrade encountered error: "+ex.message + ":\n"+ex.backtrace.join("\n"))
      render :action => 'edit'
    end  
  end

  def destroy
    t = Trade.find(params[:id])
    t.transaction do
      t.destroy
    end
    redirect_to :action => 'list'
  end
end
