class CreateTradesController < ApplicationController
  require 'bigdecimal'
  
  include MessageLogsHelper
  include TradesHelper

  def create_trades
    all_exec_reports = params[:trades]
    
    chosen_exec_reports = all_exec_reports.select { |id, value| value == "1"}
    
    chosen_exec_reports.each {|id, value| create_one_trade(id) }
    
    num_created = chosen_exec_reports.length
    flash[:notice] = "Created "+num_created.to_s + " trades"
    redirect_to :action => 'list', :controller => 'trades'
  end
  
  # Given a particular message ID, creates one trade for it
  # Creates currency, equity and other info if needed
  def create_one_trade(id)
    dbMessage = MessageLog.find(id)
    
    if(dbMessage.processed) 
      logger.debug("skipping message ["+dbMessage.id.to_s+"], already processed")
      return nil
    end
    
    qfMessage = Quickfix::Message.new(dbMessage.text)
    logger.debug("creating a trade for msg: "+ qfMessage.to_s)
    theTrade = Trade.new
    theTrade.side = getStringFieldValueIfPresent(qfMessage, Quickfix::Side.new)
    quantity = getStringFieldValueIfPresent(qfMessage, Quickfix::LastShares.new)
    currency = getStringFieldValueIfPresent(qfMessage, Quickfix::Currency.new)
    symbol =   getStringFieldValueIfPresent(qfMessage, Quickfix::Symbol.new)
    price = getStringFieldValueIfPresent(qfMessage, Quickfix::LastPx.new)
    account = getStringFieldValueIfPresent(qfMessage, Quickfix::Account.new)
    sendingTime = getHeaderStringFieldValueIfPresent(qfMessage, Quickfix::SendingTime.new)
    commission = getStringFieldValueIfPresent(qfMessage, Quickfix::Commission.new)
    
    logger.debug("creating trade for "+Side.get_human_side(@side) + " " + quantity + " " + 
                  symbol + " @ "+price + "/["+commission+"] in <"+account+">")
    
    theTrade.create_equity_trade(quantity, symbol, BigDecimal(price), BigDecimal(commission),  currency, account, sendingTime)
    theTrade.trade_type = TradeTypeTrade
    theTrade.save
    
    logger.debug("created trade: "+theTrade.to_s)
    
    dbMessage.processed = true;
    dbMessage.save
    
    return theTrade
  end
  
end
