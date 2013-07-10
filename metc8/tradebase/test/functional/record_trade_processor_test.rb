require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'
require File.dirname(__FILE__) + '/../../vendor/plugins/activemessaging/lib/activemessaging/test_helper'
require File.dirname(__FILE__) + '/../../app/processors/application'

# Override the timezones to be Europe/Minsk (EET)
module ApplicationHelper
  TZ_ID = 'Europe/Minsk'
  LOCAL_TZ = TZInfo::Timezone.get(TZ_ID)
  ENV['TZ'] = 'EET'
end

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

    assert_equal n_logs+1, MessageLog.count, "should still create a MessageLog object"
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
    # we store the date in UTC. assumption is that environment.rb has PST timezone set 
    assert_equal "Tue Dec 18 03:48:02 UTC 2007", t.journal_post_date.to_s, "trade date not set correctly"
    # Timezone Tue Dec 18 03:48:02 UTC 2007 is Tuesday, December 18, 2007 at 5:48:02 AM EET
    assert_equal "18-Dec-07 05:48:02 EET", t.journal_post_date_local_TZ, "post date not created correctly in MSK"
    if(StrategyAsAccount)
      assert_equal "strat3", t.strategy, "strategy was not set"
      assert_equal "strat3", t.account_nickname, "account was not set"
    else
      assert_equal "", t.strategy
      assert_equal "strat3", t.account_nickname, "account was not set"
    end
  end
end
