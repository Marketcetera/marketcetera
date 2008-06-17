class UpdateOrsController < ApplicationController

  # GETs should be safe (see http://www.w3.org/2001/tag/doc/whenToUseGet.html)
  verify :method => :post, :only => [ :update_sender ],
         :redirect_to => { :action => :index }

  def index

  end

  def update_sender
    @report = ORSUpdate.new(params)
    if(!@report.valid?)
      render :action => :index
      return
    end

    if RUBY_PLATFORM =~ /mswin32/
      @file = "#{ENV['METC_HOME']}" + "\\ors\\conf\\ors.xml"
    else
      @file = "/opt/marketcetera/platform/conf/ors.xml"
    end
    if(!File.file?(@file))
      @report.errors.add(:config_file, "Unable to find ORS config file at #{@file}")
      render :action => :update_sender
      return
    end

    # on the Mac, you need to have -i "", otherwise youd don't
    %x{sed -i s/MKTC-ORS/"#{@report.sender_id}"/ #{@file}}
    @post_sub = %x{grep -2 "#{@report.sender_id}" #{@file}}
  end
end
