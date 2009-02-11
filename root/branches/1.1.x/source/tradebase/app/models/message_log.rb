class MessageLog < ActiveRecord::Base
  set_table_name "messages_log" 
  include MessageLogsHelper
  
  # returns true if this is a successful trade that needs to be shown
  # ie is an executionReport that's either FILLED or PartiallyFilled
  def executed_trade?
    return ((getHeaderStringFieldValueIfPresent(qf_message, Quickfix::MsgType.new) == Quickfix::MsgType_ExecutionReport()) &&
           (getStringFieldValueIfPresent(qf_message, Quickfix::OrdStatus.new) == Quickfix::OrdStatus_FILLED() || 
            getStringFieldValueIfPresent(qf_message, Quickfix::OrdStatus.new) == Quickfix::OrdStatus_PARTIALLY_FILLED()))
  end
  
  def qf_message
    Quickfix::Message.new(self.text)
  end
  
  def sending_time
     Date.parse(qf_message.getHeader.getField(Quickfix::SendingTime.new).getString())
  end
  
end
