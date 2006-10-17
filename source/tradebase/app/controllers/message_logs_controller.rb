class MessageLogsController < ApplicationController
  include ApplicationHelper
  
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
      @startDate = parse_date_from_params(params, :dates, "start_date")
      @endDate = parse_date_from_params(params, :dates, "end_date")
    end
    all_exec_reports = []
    msgTypeField = Quickfix::MsgType.new
    ordStatusField = Quickfix::OrdStatus.new
    sendingTimeField = Quickfix::SendingTime.new
    all_messages = MessageLog.find(:all, :conditions => [ 'processed = false' ])
    all_messages.each { |msg| 
      if(msg.executed_trade?)
        if(fSubsetSearch) 
           logger.error("checking "+msg.sending_time.to_s + " against [" + @startDate.to_s + " to "+@endDate.to_s+"]")
           if((msg.sending_time >= @startDate) && (msg.sending_time <= @endDate))
             all_exec_reports << msg
           else 
            logger.error("discarding date "+msg.sending_time.to_s)
           end
        else 
           all_exec_reports << msg
        end
      end
    }
  
    if(all_exec_reports.empty?)
      flash.now[:error] = "No matching messages found"
    end
    # paginate manually
    @paginator = Paginator.new(self, all_exec_reports.length, 10, params[:page])
    @exec_report_pages = all_exec_reports[@paginator.current.offset .. @paginator.current.offset + 9] 
  end

  def show
    @message_log = MessageLog.find(params[:id])
    @qf_message = @message_log.qf_message
      if(!@message_log.executed_trade?) 
        flash[:notice] = 'Specified message is not a valid executed trade.'
        redirect_to :action => 'list'      
      end
  end
end
