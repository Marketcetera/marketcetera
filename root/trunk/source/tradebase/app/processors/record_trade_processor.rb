class RecordTradeProcessor < ApplicationProcessor
  require 'message_logs_helper'
  require 'trade_creator'
  
  subscribes_to :record_trade
  include MessageLogsHelper
  include TradeCreator

  def on_message(message_text)
    logger.debug "RecordTradeProcessor received: " + message_text

    begin
      qfMsg = Quickfix::Message.new(message_text)
      message_log = MessageLog.create(
              :time => Time.now.to_s,
              :beginstring => qfMsg.getHeader().getField(Quickfix::BeginString.new).getString(),
              :sendercompid => qfMsg.getHeader().getField(Quickfix::SenderCompID.new).getString(),
              :targetcompid => qfMsg.getHeader().getField(Quickfix::TargetCompID.new).getString(),
              :text => qfMsg.to_s)

      logger.debug("saved incoming message: "+ message_log.text)
      theTrade = create_one_trade(message_log.id)
    rescue => ex
      logger.debug("record_trade.on_message encountered error: "+ex.message + ":\n"+ex.backtrace.join("\n"))
    end
  end
end