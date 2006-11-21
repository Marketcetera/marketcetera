class WelcomeController < ApplicationController
  include ApplicationHelper, PositionsHelper

  def index
    position_pages, positions, @num_positions = get_positions_as_of_date(Date.today)
    render :action => 'welcome'
  end
  
  def welcome
  end
end
