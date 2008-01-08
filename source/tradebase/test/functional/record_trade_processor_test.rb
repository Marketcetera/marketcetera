require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require File.dirname(__FILE__) + '/../../vendor/plugins/activemessaging/lib/activemessaging/test_helper'
require File.dirname(__FILE__) + '/../../app/processors/application'

class RecordTradeProcessorTest < MarketceteraTestBase
  include ActiveMessaging::TestHelper
  directory = File.join(File.dirname(__FILE__), "../fixtures")
    Fixtures.create_fixtures(directory, [:messages_log])

  load File.dirname(__FILE__) + "/../../app/processors/record_trade_processor.rb"
  def setup
    @@processor = RecordTradeProcessor.new
  end
  
  def teardown
    @@processor = nil
  end  

  def test_record_trade_processor
    n_logs = MessageLog.count
    n_trades = Trade.count

    msg_text = MessageLog.find(20).text

    @@processor.on_message(msg_text)

    assert_equal n_logs +1, MessageLog.count
    assert_equal n_trades +1, Trade.count
  end

  def test_record_trade_processor_exception
    n_logs = MessageLog.count
    n_trades = Trade.count

    @@processor.on_message("this is not a FIX message")

    assert_equal n_logs, MessageLog.count
    assert_equal n_trades, Trade.count
  end

  def test_record_forex_trade
    # get an equity trade and change type/symbol
    log = MessageLog.find(21)
    msg = Quickfix::Message.new(log.text)
    msg.setField(Quickfix::OrdType.new.getField, Quickfix::OrdType_FOREX_MARKET())
    msg.setField(Quickfix::Symbol.new.getField, "ZAI/USD")
    msg.setField(Quickfix::Account.new.getField, "strat3")
    # set the datetime to be UTC
    msg.getHeader().setField(Quickfix::SendingTime.new.getField, "20071218-03:48:02.149")

    assert_equal 0, Trade.count
    @@processor.on_message(msg.to_s)
    assert_equal 1, Trade.count
    t = Trade.find(:all)[0]
    assert_equal "CurrencyPair", t.tradeable_type
    assert_equal "Mon Dec 17 19:48:02 -0800 2007", t.journal_post_date.to_s, "trade date not set correctly"
    
    if(StrategyAsAccount)
      assert_equal "strat3", t.strategy, "strategy was not set"
      assert_equal "strat3", t.account_nickname, "account was not set"
    else
      assert_equal "", t.strategy
      assert_equal "strat3", t.account_nickname, "account was not set"
    end
  end
end
