require 'quickfix_ruby'

class MessageDisplayController < ApplicationController
  def index
    list
    render :action => 'list'
  end

  # GETs should be safe (see http://www.w3.org/2001/tag/doc/whenToUseGet.html)
  verify :method => :post, :only => [ :destroy, :create, :update ],
         :redirect_to => { :action => :list }

  def list
  
    @messages_log_pages, @messages_logs = paginate :messages_logs, :per_page => 10
  end

  def show
    @messages_log = MessagesLog.find(params[:id])
  end

  def new
    @messages_log = MessagesLog.new
  end

  def create
    @messages_log = MessagesLog.new(params[:messages_log])
    if @messages_log.save
      flash[:notice] = 'MessagesLog was successfully created.'
      redirect_to :action => 'list'
    else
      render :action => 'new'
    end
  end

  def edit
    @messages_log = MessagesLog.find(params[:id])
    #@quickfix_message = Quickfix::Message.new(@messages_log.text)

    #lengthField = Quickfix::BodyLength.new()
    #@length = @quickfix_message.getHeader().getField(lengthField).getString()
  end

  def update
    @messages_log = MessagesLog.find(params[:id])
    if @messages_log.update_attributes(params[:messages_log])
      flash[:notice] = 'MessagesLog was successfully updated.'
      redirect_to :action => 'show', :id => @messages_log
    else
      render :action => 'edit'
    end
  end

  def destroy
    MessagesLog.find(params[:id]).destroy
    redirect_to :action => 'list'
  end
end
