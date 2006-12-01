class WelcomeController < ApplicationController
  include ApplicationHelper, PositionsHelper, TradesHelper

  def index
    position_pages, positions, @num_positions = get_positions_as_of_date(Date.today)
    @num_trades_today = number_trades_on_day(Date.today)
    @top_positioned_accounts = get_top_positioned_accounts(3)
    render :action => 'welcome'
  end
  
  def welcome
    index
  end
end
