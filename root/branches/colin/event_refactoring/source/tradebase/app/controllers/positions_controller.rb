class PositionsController < ApplicationController
  include ApplicationHelper, PositionsHelper
  
  def index
    list
    render :action => 'list'
  end

  # GETs should be safe (see http://www.w3.org/2001/tag/doc/whenToUseGet.html)
  verify :method => :post, :only => [ :destroy, :create, :update ],
         :redirect_to => { :action => :list }

  def list
     @date = DateTime.now
     @position_pages, @positions, @num_positions = get_positions_as_of_date(@date, true)
  end
end
