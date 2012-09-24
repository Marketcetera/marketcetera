require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/../unit/marketcetera_test_base'

class MessageLogsHelperTest < MarketceteraTestBase
  fixtures :messages_log
  include MessageLogsHelper

  def test_read_qf_message
    qfMessage = Quickfix::Message.new(MessageLog.find(20).text)
    assert_not_nil qfMessage

    assert_equal Quickfix::MsgType_ExecutionReport(), getHeaderStringFieldValueIfPresent(qfMessage, Quickfix::MsgType.new)
  end
  
  def test_getStringFieldValueIfPresent
    qfMessage = Quickfix::Message.new(MessageLog.find(20).text)
    
    assert_equal "GOOG", getStringFieldValueIfPresent(qfMessage, Quickfix::Symbol.new)   
    assert_equal "", getStringFieldValueIfPresent(qfMessage, Quickfix::LocationID.new)   
  end
  
  def test_getHeaderStringFieldValueIfPresent
    qfMessage = Quickfix::Message.new(MessageLog.find(20).text)
    assert_equal "MRKTC-EXCH", getHeaderStringFieldValueIfPresent(qfMessage, Quickfix::SenderCompID.new)
    assert_equal "", getHeaderStringFieldValueIfPresent(qfMessage, Quickfix::LocationID.new)   
  end
  
  # verify the qf_message field is populated correctly
  def test_qf_message_parsing
    mlog = MessageLog.find(20)
    assert_not_nil mlog.qf_message
    assert_equal Quickfix::Message.new(mlog.text).to_s, mlog.qf_message.to_s
  end

  def test_executed_trade_basic
    validTrade = MessageLog.find(20)
    notTrade = MessageLog.find(21)
    qfMsg = Quickfix::Message.new(notTrade.text)
    qfMsg.getHeader().setField(Quickfix::MsgType.new(Quickfix::MsgType_LOGON()))
    notTrade.text=qfMsg.to_s
    notTrade.save

    assert validTrade.executed_trade?
    assert !notTrade.executed_trade?
  end

  def test_executed_trade_edge_cases
    # modify the status to be partial filled
    partialFill = MessageLog.find(101)
    assert partialFill.executed_trade?

    # now make it rejected
    qfMsg = Quickfix::Message.new(partialFill.text)
    qfMsg.setField(Quickfix::OrdStatus.new(Quickfix::OrdStatus_REJECTED()))
    partialFill.text = qfMsg.to_s
    assert !partialFill.executed_trade?
  end

  def test_sending_time
    msg = MessageLog.find(20)
    assert_not_nil msg.sending_time
    assert_equal Date.civil(2006, 9, 28), msg.sending_time
  end
end
