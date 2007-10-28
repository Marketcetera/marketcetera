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
    @failed_msg, @exec_reports, read_messages = [], []
    if(fSubsetSearch)
        suffix = (params[:suffix].nil?) ? '' : params[:suffix]
        @report = ReportWithToFromDates.new(params, suffix)
        @report.validate()
        if(!@report.valid?)
          render :action => :list
          return
        end
      @startDate, @endDate = @report.from_date.as_date, @report.to_date.as_date + 1
      @exec_report_pages, read_messages = paginate :message_log, :per_page => MaxPerPage,
              :conditions => ['time >= ? AND time <= ?', @startDate, @endDate ]
    else
      @exec_report_pages, read_messages = paginate :message_log, :per_page => MaxPerPage
    end

    # pre-parse all the messages to populate the failed msg list
    read_messages.each {|msg|
      begin
        @exec_reports << Quickfix::Message.new(msg.text)
      rescue => ex
        @failed_msg << msg
      end
    }

    logger.debug("found #{read_messages.length} in SQL and got #{@exec_reports.length} execReports")

    if(@exec_reports.empty?)
      flash.now[:error] = "No matching messages found"
    end
    if(!@failed_msg.empty?)
      flash.now[:error] = "Failed to parse " + @failed_msg.size.to_s + " message(s)."
    end

  end

  def show
    @message_log = MessageLog.find(params[:id])
    @qf_message = @message_log.qf_message
      if(!@message_log.executed_trade?) 
        flash[:notice] = 'Specified message is not a valid executed trade.'
        redirect_to :action => 'list'      
      end
  end
  
  def show_unparseable_msg
    @message = MessageLog.find(params[:id])
  end
end
