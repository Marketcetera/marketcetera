require 'bigdecimal'
#require 'bigdecimal/math'
#require 'trades_helper'


class TradesController < ApplicationController
  include ApplicationHelper
  include SubAccountsHelper
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
    @trade_pages, @trades = paginate :trades, :per_page => 10
  end

  def show
    @trade = Trade.find(params[:id])
  end

  def new
    @trade = Trade.new
  end

  def create
    @trade = Trade.new(params[:trade])
    
    create_equity_trade(@trade, params[:m_symbol][:root],
     BigDecimal(params[:per_share_price]), BigDecimal(params[:per_share_commission]),
     params[:currency][:alpha_code], params[:account][:nickname])
    
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
  
  
  def create_equity_trade(trade, symbol, per_share_price, 
        per_share_commission, currency_alpha_code, account_nickname)
    notional = trade.quantity * per_share_price
    total_commission = trade.quantity.abs() * per_share_commission
    trade.asset_type=AssetTypeEquity
    trade.asset_id = get_equity(symbol).id

    trade.account = get_account_by_nickname(account_nickname)

    sub_accounts = trade.account.sub_accounts
    short_term_investment_sub_account = sub_accounts.select {|a| a.sub_account_type.description == ShortTermInvestmentDescription}[0]
    cash_sub_account = sub_accounts.select {|a| a.sub_account_type.description == CashDescription}[0]
    commission_sub_account = sub_accounts.select {|a| a.sub_account_type.description == CommissionsDescription}[0]
    
    
    trade.journal = Journal.new( :post_date => Date.today )
    base_currency = get_currency(currency_alpha_code)
    short_term_investment_posting = Posting.new(:journal=>trade.journal, :currency=>base_currency, :quantity=>notional, :sub_account=>short_term_investment_sub_account)
    cash_notional_posting = Posting.new(:journal=>trade.journal, :currency=>base_currency, :quantity=>(-1*notional), :sub_account=>cash_sub_account)
    commission_posting = Posting.new(:journal=>trade.journal, :currency=>base_currency, :quantity=>total_commission, :sub_account=>commission_sub_account)
    cash_commission_posting = Posting.new(:journal=>trade.journal, :currency=>base_currency, :quantity=>(-1*total_commission), :sub_account=>cash_sub_account)

    trade.journal.postings.push(short_term_investment_posting)
    trade.journal.postings.push(cash_notional_posting)
    trade.journal.postings.push(commission_posting)
    trade.journal.postings.push(cash_commission_posting)
  end
  
  
end
