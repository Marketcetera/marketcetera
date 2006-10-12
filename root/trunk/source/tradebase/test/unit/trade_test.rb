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
    tradeDate = Date.civil(2006, 7, 8)
    account = "beer money-"+Date.new.to_s
    theTrade.create_equity_trade(20, "TOLI", 420.23, 19.99,  "USD", account, tradeDate)
    theTrade.save
    
    # now start verifying everything
    assert_not_nil Account.find_by_nickname(account), "account doesn't exist"
    assert_equal Date.civil(2006, 7,8), theTrade.journal_post_date
    assert_equal 420.23, theTrade.price_per_share
    assert_equal 19.99, theTrade.total_commission
    assert_equal 420.23 * theTrade.quantity, theTrade.total_price
  end
  
  def test_update_price_per_share
    t = Trade.new
    t.create_equity_trade(10, "TOLI", 4.99, 7.50, "USD", "some-account", Date.civil(2006, 10,10))
    assert_equal 4.99, t.price_per_share
    assert_equal Float(4.99*10).to_s, t.total_price.to_s
    
    # now update the pps - we expect the underlying total price to update too
    t.price_per_share = 37
    assert_equal 37, t.price_per_share
    assert_equal Float(37*10), t.total_price
    # lookup the posting too
    verify_trade_prices(t, Float(37*10), 7.50)
  end 
  
  def test_update_qty
    t = Trade.new
    t.create_equity_trade(10, "TOLI", 4.99, 7.50, "USD", "some-account", Date.civil(2006, 10,10))
    assert_equal 10, t.quantity
    assert_equal Float(4.99*10).to_s, t.total_price.to_s
    
    # now update the qty - we expect the underlying total price to update too
    t.quantity = 37
    assert_equal 37, t.quantity
    assert_equal Float(37*4.99), t.total_price
    # lookup the posting too
    verify_trade_prices(t, Float(37*4.99), 7.50)
  end 
  
  def test_update_commission
    t = Trade.new
    t.create_equity_trade(11, "TOLI", 4.99, 7.50, "USD", "some-account", Date.civil(2006, 10,10))
    assert_equal 11, t.quantity
    assert_equal Float(4.99*11), t.total_price
    
    # now update the qty - we expect the underlying total price to update too
    t.total_commission = 14.99
    assert_equal 14.99, t.total_commission
    # lookup the posting too
    verify_trade_prices(t, Float(11*4.99), 14.99)
  end 
  
  def test_update_price_and_qty_and_commission
    t = Trade.new
    t.create_equity_trade(10, "TOLI", 4.99, 7.50, "USD", "some-account", Date.civil(2006, 10,10))
    assert_equal 10, t.quantity
    assert_equal Float(4.99*10).to_s, t.total_price.to_s
    
    # now update the qty - we expect the underlying total price to update too
    t.total_commission = 14.99
    t.price_per_share = 25
    t.quantity = 200
    assert_equal 14.99, t.total_commission
    assert_equal 200, t.quantity
    assert_equal 200*25, t.total_price
    assert_equal 25, t.price_per_share
    # lookup the posting too
    verify_trade_prices(t, Float(25*200), 14.99)
  end 
  
   
  ##### Helpers ######

  # verifies trade has the right total price + commissions
  def verify_trade_prices(trade, total_price, total_commission)
    sti = trade.journal.find_posting_by_sat(SubAccountType::DESCRIPTIONS[:sti])
    assert_equal total_price, sti.quantity
    assert_equal -sti.quantity, 
        trade.journal.find_posting_by_sat_and_pair_id(SubAccountType::DESCRIPTIONS[:cash], sti.pair_id).quantity, 
        "cash portion of STI is incorrect"
    
    comm = trade.journal.find_posting_by_sat(SubAccountType::DESCRIPTIONS[:commissions])
    assert_equal total_commission, comm.quantity
    assert_equal -comm.quantity, 
        trade.journal.find_posting_by_sat_and_pair_id(SubAccountType::DESCRIPTIONS[:cash], comm.pair_id).quantity, 
        "cash portion of commission is incorrect"   
  end
end
