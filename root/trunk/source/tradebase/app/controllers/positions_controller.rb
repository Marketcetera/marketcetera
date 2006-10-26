class PositionsController < ApplicationController
  include ApplicationHelper
  
  def index
    list
    render :action => 'list'
  end

  # GETs should be safe (see http://www.w3.org/2001/tag/doc/whenToUseGet.html)
  verify :method => :post, :only => [ :destroy, :create, :update ],
         :redirect_to => { :action => :list }

  def list
    positions = Position.find_by_sql([
     'select sum(trades.quantity) as position, tradeable_id, tradeable_type, account_id, journal_id from trades'+
     ' LEFT JOIN journals on trades.journal_id=journals.id '+
     ' WHERE journals.post_date<? GROUP BY tradeable_id, account_id, tradeable_type',
      Time.now+(24*60*60)])
    @position_pages, @positions = paginate_collection(positions, :per_page => 10)
  end

  def paginate_collection(collection, options = {})
    default_options = {:per_page => 10, :page => 1}
    options = default_options.merge options
    
    pages = Paginator.new self, collection.size, options[:per_page], options[:page]
    first = pages.current.offset
    last = [first + options[:per_page], collection.size].min
    slice = collection[first...last]
    return [pages, slice]
  end
end
