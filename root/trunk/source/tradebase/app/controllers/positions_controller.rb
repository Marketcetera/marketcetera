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
    lookup_positions(Date.today)    
  end

  def positions_as_of
    as_of_date = get_date_from_params(params, :position, "as_of", "as_of_date")
    if(as_of_date.blank?) 
      as_of_date = Date.today
    end
    lookup_positions(as_of_date)
    render :template => '/positions/list'
  end

  private 
  # Here's the SQL
  # 'select sum(trades.position_qty) as position, tradeable_id, tradeable_type, account_id, journal_id from trades'+
  # ' LEFT JOIN journals on trades.journal_id=journals.id '+
  # ' WHERE journals.post_date< ? GROUP BY tradeable_id, account_id, tradeable_type',
  #  date])
  def lookup_positions(date)
     @date = date
     @position_pages, @positions, @num_positions = get_positions_as_of_date(date)
  end
end
