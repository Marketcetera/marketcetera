class CreateTradesController < ApplicationController
  require 'quickfix'
  require 'bigdecimal'
  
  include MessageLogsHelper
  include TradesHelper

  def create_trades
    all_exec_reports = params[:trades]
    
    chosen_exec_reports = all_exec_reports.select { |id, value| value == "1"}
    
    chosen_exec_reports.each {|id, value| create_one_trade(id) }
    
    @num_created = chosen_exec_reports.length
    logger.error("numCreated is "+@num_created.to_s)
    #redirect_to :action => 'list', :controller => 'trades'
  end
  
  # Given a particular message ID, creates one trade for it
  # Creates currency, equity and other info if needed
  def create_one_trade(id)
    dbMessage = MessageLog.find(id)
    qfMessage = Quickfix::Message.new(dbMessage.text)
    logger.error("creating a trade for msg: "+ qfMessage.to_s)
    theTrade = Trade.new
    quantity = getStringFieldValueIfPresent(qfMessage, Quickfix::LastShares.new)
    currency = getStringFieldValueIfPresent(qfMessage, Quickfix::Currency.new)
    symbol =   getStringFieldValueIfPresent(qfMessage, Quickfix::Symbol.new)
    price = getStringFieldValueIfPresent(qfMessage, Quickfix::LastPx.new)
    account = getStringFieldValueIfPresent(qfMessage, Quickfix::Account.new)
    sendingTime = getHeaderStringFieldValueIfPresent(qfMessage, Quickfix::SendingTime.new)
    theTrade.create_equity_trade(quantity, symbol, BigDecimal(price), BigDecimal("0"),  currency, account, sendingTime)
    theTrade.trade_type = TradeTypeTrade
    theTrade.side = getStringFieldValueIfPresent(qfMessage, Quickfix::Side.new)
    theTrade.save
    
    dbMessage.processed = true;
    dbMessage.save
  end
  
end
