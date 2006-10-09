require File.dirname(__FILE__) + '/../test_helper'

class MessageLogsHelperTest < Test::Unit::TestCase
  fixtures :messages_log
  include MessageLogsHelper

  def test_read_qf_message
    qfMessage = Quickfix::Message.new(MessageLog.find(20).text)
    assert_not_nil qfMessage

    assert_equal Quickfix::MsgType_ExecutionReport(), getHeaderStringFieldValueIfPresent(qfMessage, Quickfix::MsgType.new)
  end
  
  # verifyt the qf_message field is populated correctly
  def test_qf_message_parsing
    mlog = MessageLog.find(20)
    assert_not_nil mlog.qf_message
    assert_equal Quickfix::Message.new(mlog.text).to_s, mlog.qf_message.to_s
  end
  
  def test_executed_trade_basic
    validTrade = MessageLog.find(20)
    notTrade = MessageLog.find(1)
    
    assert validTrade.executed_trade?
    assert !notTrade.executed_trade?
  end
  
  def test_executed_trade_edge_cases
    # modify the status to be partial filled
    partialFill = MessageLog.find(101)
    assert partialFill.executed_trade?
    
    # now shove non-filled. rejected
    assert !MessageLog.find(100).executed_trade?
    
    # reset and try non-exec-report
    assert !MessageLog.find(1).executed_trade?
  end
  
  def test_sending_time
    msg = MessageLog.find(20)
    assert_not_nil msg.sending_time
    assert_equal Date.civil(2006, 9, 28), msg.sending_time
  end
  
end
