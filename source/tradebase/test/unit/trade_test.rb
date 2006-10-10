require File.dirname(__FILE__) + '/../test_helper'
require 'bigdecimal'
require 'logger'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class TradeTest < MarketceteraTestBase
  fixtures :trades, :messages_log
  include TradesHelper
    
  # Replace this with your real tests.
  def test_create_equity_trade
    theTrade = Trade.new
    theTrade.quantity = 20
    symbol = "TOLI"
    currency = "USD"
    price = 420.23
    commission = 19.99
    tradeDate = Date.civil(2006, 7, 8)
    account = "beer money-"+Date.new.to_s
    create_equity_trade(theTrade, symbol, price, commission,  currency, account, tradeDate)
    theTrade.save
    
    # now start verifying everything
    assert_not_nil Account.find_by_nickname(account), "account doesn't exist"
    assert_equal Date.civil(2006, 7,8), theTrade.journal_post_date
    assert_equal 420.23, theTrade.price_per_share
    assert_equal 19.99, theTrade.total_commission
    assert_equal price * theTrade.quantity, theTrade.total_price

  end
end
