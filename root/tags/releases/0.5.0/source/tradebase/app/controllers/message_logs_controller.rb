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
      @exec_report_pages, @failed_msg= paginate :message_log, :per_page => MaxPerPage,
              :conditions => ['failed_parsing = 1']

    logger.debug("found #{@failed_msg.length} unparseable execReports")

    if(@failed_msg.empty?)
      flash.now[:error] = "No matching messages found"
    end

  end

  def show_unparseable_msg
    @message = MessageLog.find(params[:id])
  end
end
