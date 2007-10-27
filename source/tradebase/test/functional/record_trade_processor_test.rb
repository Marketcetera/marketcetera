require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../../vendor/plugins/activemessaging/lib/activemessaging/test_helper'
require File.dirname(__FILE__) + '/../../app/processors/application'

class RecordTradeProcessorTest < Test::Unit::TestCase
  include ActiveMessaging::TestHelper

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
end
