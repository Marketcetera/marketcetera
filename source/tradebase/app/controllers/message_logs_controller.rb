require 'quickfix_ruby'
	
class MessageLogsController < ApplicationController
  include QF_BuyHelper

  def index
    list
    render :action => 'list'
  end

  # GETs should be safe (see http://www.w3.org/2001/tag/doc/whenToUseGet.html)
  verify :method => :post, :only => [ :destroy, :create, :update ],
         :redirect_to => { :action => :list }

  def list
    all_messages = MessageLog.find(:all)
    @all_exec_reports = []
    msgTypeField = Quickfix::MsgType.new
    for oneMessageLog in all_messages
      qfMessage = Quickfix::Message.new(oneMessageLog.text)
      if(qfMessage.getHeader().getField(msgTypeField).getString() == Quickfix::MsgType_ExecutionReport())
         logger.error("adding execReport: "+qfMessage.toString())
         @all_exec_reports << oneMessageLog
      end
    end
  
  # deal with pagination later
   # @message_log_pages, @message_logs = paginate :message_logs, :per_page => 10
  end

  def show
    @message_log = MessageLog.find(params[:id])
  end

end
