require 'quickfix_ruby'
#include QF_BuyHelper
	
class MessageLogsController < ApplicationController

  def index
    list
    render :action => 'list'
  end

  # GETs should be safe (see http://www.w3.org/2001/tag/doc/whenToUseGet.html)
  verify :method => :post, :only => [ :destroy, :create, :update ],
         :redirect_to => { :action => :list }

  # NOTE: pagination is grossly inefficient right now. We get all messages, select all valid 
  # execReports, and only then paginate them - and we do this every time b/c we can't just 
  # get a slice of data from DB since not every message is going to be displayed.
  def list
    fSubsetSearch = params[:search_type] == 's'
    if(fSubsetSearch)
      @startDate = Date.new(params[:dates]['start_date(1i)'].to_i, params[:dates]['start_date(2i)'].to_i, params[:dates]['start_date(3i)'].to_i)
      @endDate = Date.new(params[:dates]['end_date(1i)'].to_i, params[:dates]['end_date(2i)'].to_i, params[:dates]['end_date(3i)'].to_i)
    end
    all_exec_reports = []
    msgTypeField = Quickfix::MsgType.new
    ordStatusField = Quickfix::OrdStatus.new
    sendingTimeField = Quickfix::SendingTime.new
    all_messages = MessageLog.find(:all, :conditions => [ 'processed = false' ])
    for oneMessageLog in all_messages
      qfMessage = Quickfix::Message.new(oneMessageLog.text)
      if(isTradeToBeShown(qfMessage, msgTypeField, ordStatusField))
        msg_send_time = Date.parse(qfMessage.getHeader.getField(sendingTimeField).getString())
        if(fSubsetSearch) 
           logger.error("checking "+msg_send_time.to_s + " against " + @startDate.to_s + " and "+@endDate.to_s)
           if((msg_send_time >= @startDate) && (msg_send_time <= @endDate))
             logger.error("adding execReport: "+qfMessage.toString())
             all_exec_reports << oneMessageLog
           else 
            logger.error("discarding date "+msg_send_time.to_s)
           end
        else 
           all_exec_reports << oneMessageLog
        end
      end
    end
  
    # deal with pagination later
    @paginator = Paginator.new(self, all_exec_reports.length, 10, params[:page])
    @exec_report_pages = all_exec_reports[@paginator.current.offset .. @paginator.current.offset + 9] 
  end

  def show
    @message_log = MessageLog.find(params[:id])
    @qf_message = Quickfix::Message.new(@message_log.text)
      if(!isTradeToBeShown(@qf_message, Quickfix::MsgType.new, Quickfix::OrdStatus.new)) 
        flash[:notice] = 'Specified message is not a valid executed trade.'
        redirect_to :action => 'list'      
      end
  end

  private 
  # returns true if this is a successful trade that needs to be shown
  # ie is an executionReport that's either FILLED or PartiallyFilled
  def isTradeToBeShown(qfMessage, msgTypeField, ordStatusField)
    return ((qfMessage.getHeader().getField(msgTypeField).getString() == Quickfix::MsgType_ExecutionReport()) &&
           (qfMessage.getField(ordStatusField).getString() == Quickfix::OrdStatus_FILLED() || 
            qfMessage.getField(ordStatusField).getString() == Quickfix::OrdStatus_PARTIALLY_FILLED()))
  end

end
