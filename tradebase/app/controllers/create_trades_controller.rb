class CreateTradesController < ApplicationController
  require 'trade_creator'

  include MessageLogsHelper
  include TradesHelper
  include TradeCreator
  
  def create_trades
    all_exec_reports = params[:trades]
    
    chosen_exec_reports = all_exec_reports.select { |id, value| value == "1"}
    
    num_created = 0
    chosen_exec_reports.each {|id, value| 
      if(!create_one_trade(id).nil?)  
        num_created += 1
      end
    }
    
    flash[:notice] = "Created "+num_created.to_s + " trades"
    redirect_to :action => 'list', :controller => 'trades'
  end
end
