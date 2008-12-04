# Library to create trades
require 'bigdecimal'

module TradeCreator
  include MessageLogsHelper
  include TradesHelper

  # Given a particular message ID, creates one trade for it
  # Creates currency, equity and other info if needed
  def create_one_trade(id)
    dbMessage = MessageLog.find(id)

    if (dbMessage.processed)
      logger.debug("skipping message ["+dbMessage.id.to_s+"], already processed")
      return nil
    end

    qfMessage = Quickfix::Message.new(dbMessage.text)
    logger.debug("creating a trade for msg: "+ qfMessage.to_s)
    ordType = getStringFieldValueIfPresent(qfMessage, Quickfix::OrdType.new)
    if(ordType == Quickfix::OrdType_FOREX_LIMIT() || ordType == Quickfix::OrdType_FOREX_MARKET())
        theTrade = ForexTrade.new
    else
      theTrade = Trade.new
    end

    theTrade.side = getStringFieldValueIfPresent(qfMessage, Quickfix::Side.new)
    quantity = getStringFieldValueIfPresent(qfMessage, Quickfix::LastShares.new)
    currency = getStringFieldValueIfPresent(qfMessage, Quickfix::Currency.new)
    symbol =   getStringFieldValueIfPresent(qfMessage, Quickfix::Symbol.new)
    price = getStringFieldValueIfPresent(qfMessage, Quickfix::LastPx.new)
    account = getStringFieldValueIfPresent(qfMessage, Quickfix::Account.new)
    sendingTime_UTC = getHeaderStringFieldValueIfPresent(qfMessage, Quickfix::SendingTime.new)
    commission = getStringFieldValueIfPresent(qfMessage, Quickfix::Commission.new)

    logger.debug("creating trade for "+Side.get_human_side(@side) + " " + quantity + " " +
            symbol + " @ "+price + "/["+commission+"] in <"+account+">")

    theTrade.create_trade(quantity, symbol, BigDecimal(price), BigDecimal(commission),  currency, account, sendingTime_UTC)
    theTrade.trade_type = TradeTypeTrade
    theTrade.imported_fix_msg = dbMessage.text
    theTrade.save

    logger.debug("created trade: "+theTrade.to_s)

    dbMessage.processed = true;
    dbMessage.save

    return theTrade
  end
end
