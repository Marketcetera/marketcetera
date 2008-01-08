class UpdateOmsController < ApplicationController

  # GETs should be safe (see http://www.w3.org/2001/tag/doc/whenToUseGet.html)
  verify :method => :post, :only => [ :update_sender ],
         :redirect_to => { :action => :index }

  def index

  end

  def update_sender
    @report = OMSUpdate.new(params)
    if(!@report.valid?)
      render :action => :index
      return
    end

    @file = "/opt/marketcetera/platform/conf/oms.xml"
    if(!File.file?(@file))
      @report.errors.add(:config_file, "Unable to find OMS config file at #{@file}")
      render :action => :update_sender
      return
    end

    %x{sed -i "" s/MKTC-OMS/"#{@report.sender_id}"/ #{@file}}
    @post_sub = %x{grep -2 "#{@report.sender_id}" #{@file}}
  end
end
