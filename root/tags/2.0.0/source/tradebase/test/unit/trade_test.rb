require File.dirname(__FILE__) + '/../test_helper'
require 'bigdecimal'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class TradeTest < MarketceteraTestBase
  fixtures :messages_log, :equities, :currencies
  include TradesHelper
  include ApplicationHelper
  
  def setup
    @equity = equities(:SUNW)
    assert @equity.m_symbol.root.length > 0
  end
    
  def test_validation 
    t = Trade.new(:quantity => 20, :price_per_share => 420.23, :side=> Side::QF_SIDE_CODE[:buy])
    t.tradeable = @equity
    assert t.valid?, "regular buy 20"
    
    # empty trade
    assert !Trade.new.valid?, "empty trade"
    assert !Trade.new(:quantity => 20).valid?, "only qty specified"
    assert !Trade.new(:quantity => 20, :price_per_share => 234.3).valid?, "no symbol"
    
    t = Trade.new(:quantity => 20)
    t.tradeable = @equity
    assert !t.valid?, "no price_per_share"
    assert_equal 4, t.errors.length, collect_errors_into_string(t.errors)

    t = Trade.new(:price_per_share => 20)
    t.tradeable = @equity
    assert !t.valid?, "no qty"
    assert_not_nil t.errors[:quantity], collect_errors_into_string(t.errors)
    assert_nil t.errors[:symbol]
    
    t = Trade.new(:quantity => -20, :price_per_share => 20, :side => Side::QF_SIDE_CODE[:buy])
    t.tradeable = @equity
    assert !t.valid?, "invalid qty"
    assert_not_nil t.errors[:quantity]
    assert_equal 1, t.errors.length
    
    t = Trade.new(:quantity => -20, :price_per_share => 20, :side => Side::QF_SIDE_CODE[:sell])
    t.tradeable = @equity
    assert !t.valid?, "invalid qty"
    assert_not_nil t.errors[:quantity]
    assert_equal 1, t.errors.length
    
    # blank equity
    t = Trade.new(:quantity => 20, :price_per_share => 420.23, :side=> Side::QF_SIDE_CODE[:buy])
    t.tradeable = Equity.get_equity('')
    assert !t.valid?, "empty/blank root symbol in equity"
    assert_not_nil t.errors[:symbol]
    assert_equal 1, t.errors.length
    
    # sell
    t = Trade.new(:quantity => 10, :price_per_share => 4.99, :side => Side::QF_SIDE_CODE[:sell])
    t.tradeable = @equity
    assert t.valid?, "sell 10 does not validate: " + collect_errors_into_string(t.errors)
    
    t = Trade.new(:quantity => 10, :price_per_share => 4.99, :side => Side::QF_SIDE_CODE[:sellShort])
    t.tradeable = @equity
    assert t.valid?, "sellShort 10 does not validate: " + collect_errors_into_string(t.errors)
  end
  
  # this simulates behaviour when new trade is created in controller
  def test_validation_no_side
    t = Trade.new(:quantity => 10, :price_per_share => 4.99)
    t.tradeable = @equity
    assert !t.valid?, "trade with no side 10 does not validate: " + collect_errors_into_string(t.errors)

    t = Trade.new(:quantity => -10, :price_per_share => 4.99)
    t.tradeable = @equity
    assert !t.valid?, "trade with no side 10 does not validate: " + collect_errors_into_string(t.errors)
  end
    
  # Replace this with your real tests.
  def test_create_equity_trade
    theTrade = Trade.new(:quantity => 20, :price_per_share => 420.23, :side => Side::QF_SIDE_CODE[:buy])
    theTrade.tradeable = @equity
    tradeDate = DateTime.civil(2006, 7, 8)
    account = "beer money-"+Date.new.to_s
    assert theTrade.create_trade(theTrade.quantity, "TOLI", theTrade.price_per_share, 19.99,  "USD", account, tradeDate)
    theTrade.save
    
    # now start verifying everything
    assert_not_nil Account.find_by_nickname(account), "account doesn't exist"
    assert_equal 20, theTrade.quantity
    assert_equal Date.civil(2006, 7,8).to_s(:db), theTrade.journal_post_date.strftime("%Y-%m-%d")
    assert_nums_equal 420.23, theTrade.price_per_share
    assert_nums_equal 19.99, theTrade.total_commission
    assert_nums_equal 420.23 * theTrade.quantity, theTrade.total_price
    assert_equal "BUY 20.0 TOLI 420.23", theTrade.journal.description

    assert_equal "07-Jul-06 17:00:00 PDT", theTrade.journal_post_date_local_TZ

    assert_equal SubAccountType::DESCRIPTIONS.length, theTrade.account.sub_accounts.length
  end
  
  def test_create_bogus_params
    nTrades = Trade.count
    theTrade = Trade.new(:quantity => -20, :price_per_share => 420.23, :side => Side::QF_SIDE_CODE[:buy])
    theTrade.tradeable = @equity
    tradeDate = Date.civil(2006, 7, 8)
    account = "beer money-"+Date.new.to_s
    assert theTrade.create_trade(theTrade.quantity, "TOLI", theTrade.price_per_share, 19.99,  "USD", account, tradeDate),
          "did not accept negative incoming qty - we should do an abs on the incoming qty"
    assert nTrades, Trade.count
  
    theTrade = Trade.new(:quantity => 20, :price_per_share => 420.23, :side => Side::QF_SIDE_CODE[:buy])
    tradeDate = Date.civil(2006, 7, 8)
    account = "beer money-"+Date.new.to_s
    assert !theTrade.create_trade(theTrade.quantity, '', theTrade.price_per_share, 19.99,  "USD", account, tradeDate),
      "accepted empty symbol"
    assert !theTrade.save, "no symbol: " + theTrade.tradeable_m_symbol_root
    assert_equal nTrades, Trade.count
  end
  
  def test_update_equity
    nTrades = Trade.count
    test_create_equity_trade
    assert_equal nTrades+1, Trade.count, "new trade wasn't created"
    trade = Trade.find(:all)[Trade.count-1]
    oldEquity = Equity.find(trade.tradeable.id)
    
    nEquities = Equity.count    
    trade.tradeable_m_symbol_root = "rama"
    trade.save
    
    assert_not_equal oldEquity.m_symbol.root, trade.tradeable_m_symbol_root, "didn't switch symbols"
    assert_not_equal oldEquity.id, trade.tradeable.id, "didn't switch equities"
    assert_equal nEquities +1, Equity.count, "didn't create new equity"
    assert_not_equal oldEquity, trade.tradeable, "didn't switch equities"
  end
  
  def test_update_price_per_share
    t = Trade.new(:quantity => 10, :price_per_share => 4.99, :side => Side::QF_SIDE_CODE[:buy])
    assert t.create_trade(t.quantity, "TOLI", t.price_per_share, 7.50, "USD", "some-account", Date.civil(2006, 10,10))
    assert_nums_equal 4.99, t.price_per_share
    assert_nums_equal 4.99*10, t.total_price
    
    # now update the pps - we expect the underlying total price to update too
    t.price_per_share = 37
    assert_nums_equal 37, t.price_per_share
    assert_nums_equal 37*10, t.total_price
    # lookup the posting too
    verify_trade_prices(t, 37*10, 7.50)
  end 
  
  def test_update_qty
    t = Trade.new(:quantity => 10, :price_per_share => 4.99, :side => Side::QF_SIDE_CODE[:buy])
    assert t.create_trade(t.quantity, "TOLI", t.price_per_share, 7.50, "USD", "some-account", Date.civil(2006, 10,10))
    assert_nums_equal 10, t.quantity
    assert_nums_equal 4.99*10, t.total_price
    
    # now update the qty - we expect the underlying total price to update too
    t.quantity = 37
    assert_nums_equal 37, t.quantity
    assert_nums_equal 37*4.99, t.total_price
    # lookup the posting too
    verify_trade_prices(t, 37*4.99, 7.50)
  end 
  
  def test_update_commission
    t = Trade.new(:quantity => 11, :price_per_share => 4.99, :side => Side::QF_SIDE_CODE[:buy])
    assert t.create_trade(t.quantity, "TOLI", t.price_per_share, 7.50, "USD", "some-account", Date.civil(2006, 10,10))
    assert_nums_equal 11, t.quantity
    assert_nums_equal 4.99*11, t.total_price
    
    # now update the qty - we expect the underlying total price to update too
    t.total_commission = 14.99
    assert_nums_equal 14.99, t.total_commission
    # lookup the posting too
    verify_trade_prices(t, 11*4.99, 14.99)
    
    # set an empty and then nil commission and make sure it comes back as 0
    t.total_commission = nil
    assert_nums_equal 0, t.total_commission
    # lookup the posting too
    verify_trade_prices(t, 11*4.99, 0)

    t.total_commission = 5    
    assert_nums_equal 5, t.total_commission
    verify_trade_prices(t, 11*4.99, 5)
    t.total_commission = ''
    assert_nums_equal 0, t.total_commission
    verify_trade_prices(t, 11*4.99, 0)
  end 
  
  def test_update_price_and_qty_and_commission
    t = Trade.new(:quantity => 10, :price_per_share => 4.99, :side => Side::QF_SIDE_CODE[:buy])
    t.create_trade(t.quantity, "TOLI", t.price_per_share, 7.50, "USD", "some-account", Date.civil(2006, 10,10))
    assert_nums_equal 10, t.quantity
    assert_nums_equal 4.99*10, t.total_price
    
    # now update the qty - we expect the underlying total price to update too
    t.total_commission = 14.99
    t.price_per_share = 25
    t.quantity = 200
    assert_equal BigDecimal.new("14.99"), t.total_commission
    assert_equal 200, t.quantity
    assert_equal 200*25, t.total_price
    assert_equal 25, t.price_per_share
    # lookup the posting too
    verify_trade_prices(t, 25*200, 14.99)
  end 
  
  # verify that position qty is set to neg of the qty for Sell situations
  def test_position_qty_set_correctly
    t = Trade.new(:quantity => 10, :price_per_share => 4.99, :side => Side::QF_SIDE_CODE[:buy], :tradeable => @equity)
    t.create_trade(t.quantity, "TOLI", t.price_per_share, 7.50, "USD", "some-account", Date.civil(2006, 10,10))
    assert t.save, "wasn't able tos save b 10 GOOG 4.99"
    assert_nums_equal 10, t.position_qty
    
    t = Trade.new(:quantity => 10, :price_per_share => 4.99, :side => Side::QF_SIDE_CODE[:sell], :tradeable => @equity)
    t.create_trade(t.quantity, "TOLI", t.price_per_share, 7.50, "USD", "some-account", Date.civil(2006, 10,10))
    assert t.save, "wasn't able tos save b 10 GOOG 4.99"
    assert_nums_equal -10, t.position_qty
    
    t = Trade.new(:quantity => 10, :price_per_share => 4.99, :side => Side::QF_SIDE_CODE[:sellShort], :tradeable => @equity)
    t.create_trade(t.quantity, "TOLI", t.price_per_share, 7.50, "USD", "some-account", Date.civil(2006, 10,10))
    assert t.save, "wasn't able tos save b 10 GOOG 4.99"
    assert_nums_equal -10, t.position_qty
  end
  
  def test_summary 
    t = create_test_trade(10, 4.99, Side::QF_SIDE_CODE[:sellShort], "some-acct", Date.civil(2006, 10,10), "TOLI", 
                          7.50, "USD")
    assert_equal "SS 10.0 TOLI 4.99", t.summary

    t = create_test_trade(340, 43.99, Side::QF_SIDE_CODE[:buy], "some-acct", Date.civil(2006, 10,10), "bob", 
                          7.50, "USD")
    assert_equal "B 340.0 bob 43.99", t.summary
  end
  
  def test_to_s
    t = Trade.new(:quantity => 10, :price_per_share => 4.99, :side => Side::QF_SIDE_CODE[:buy])
    assert t.create_trade(t.quantity, "TOLI", t.price_per_share, 7.50, "USD", "some-account", Date.civil(2006, 10,10))
    t.side = Side::QF_SIDE_CODE[:sellShort]
    assert_equal "[SELL SHORT] -10.0 <TOLI> @4.99 in acct [some-account]", t.to_s
  end
   
  def test_trading_qty
    assert_nums_equal 100.0, Trade.new(:quantity => 100, :side => Side::QF_SIDE_CODE[:buy]).quantity
    assert_nums_equal -100.0, Trade.new(:quantity => 100, :side => Side::QF_SIDE_CODE[:sell]).quantity
    assert_nums_equal -100.0, Trade.new(:quantity => 100, :side => Side::QF_SIDE_CODE[:sellShort]).quantity
  end
  
  def test_currency_alpha_code
   t = Trade.new(:quantity => 10, :price_per_share => 4.99, :side => Side::QF_SIDE_CODE[:buy])
   assert_nil t.currency_alpha_code
   assert t.create_trade(t.quantity, "TOLI", t.price_per_share, 7.50, "ZAI", "some-account", Date.civil(2006, 10,10))
   assert_equal currencies(:ZAI).alpha_code, t.currency_alpha_code 
  end
  
  def test_currency_update 
   t = Trade.new(:quantity => 10, :price_per_share => 4.99, :side => Side::QF_SIDE_CODE[:buy])
   assert_nil t.currency_alpha_code
   assert t.create_trade(t.quantity, "TOLI", t.price_per_share, 7.50, "USD", "some-account", Date.civil(2006, 10,10))
   assert t.save
   
   # now, update trade currency
   t.currency = currencies(:ZAI)   
   assert_equal currencies(:ZAI).alpha_code, t.currency_alpha_code
   t.journal.postings.each { |p| assert_equal currencies(:ZAI), p.currency, "not all sub-postings got updated" }
  end

  def test_assert_nums_equal
    assert_nums_equal 0, 0
    assert_nums_equal 10, 10
    assert_nums_equal -10.3, -10.3
    assert_nums_equal -10, -10
    assert_nums_equal "20", 20
    assert_nums_equal "-20", -20
    assert_nums_equal BigDecimal("23.454"), Float(23.454)
  end

  def test_destroy
    theTrade = Trade.new(:quantity => 20, :price_per_share => 420.23, :side => Side::QF_SIDE_CODE[:buy])
    theTrade.tradeable = @equity
    tradeDate = Date.civil(2006, 7, 8)
    account = "beer money-"+Date.new.to_s
    assert theTrade.create_trade(theTrade.quantity, "TOLI", theTrade.price_per_share, 19.99,  "USD", account, tradeDate)
    theTrade.save

    journal = theTrade.journal
    postings = theTrade.journal.postings
    theTrade.destroy

    assert_raise(ActiveRecord::RecordNotFound, "didn't delete trade") {
      Trade.find(theTrade)
    }
    assert_raise(ActiveRecord::RecordNotFound, "didn't delete underlying journal") {
      Journal.find(journal.id)
    }
    postings.each {|p| assert_raise(ActiveRecord::RecordNotFound, "didn't delete underlying postings") {Posting.find(p.id) } }

  end
end
