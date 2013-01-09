class PositionsQueriesController < ApplicationController
  auto_complete_for :m_symbol, :root, {}
  auto_complete_for :account, :nickname, {}
  include ApplicationHelper, PositionsHelper

  def index
    positions
  end

  def positions
    # display positions index page
    render :template => '/queries/positions_queries'
  end

  def positions_as_of
    @report = ReportOnDate.new(params)
    if(!@report.valid?)
        render :template => '/queries/positions_queries'
        return
    end
    as_of_date = @report.on_date.as_date
    @position_pages, @positions, @num_positions = get_positions_as_of_date(as_of_date, false)
    @query_type = "as of Open on"
    if(@positions.empty?)
      flash.now[:error] = "No Matching positions were found as of open on: [#{as_of_date}]"
    end
    @param_name = "as_of_date"
    @param_value = as_of_date
    render :template => '/positions/positions_search_output'
  end

  def positions_by_account
    nickname = get_non_empty_string_from_two(params, :account, :nickname, "nickname")
    if(nickname.nil? || nickname.empty?)
      query_nickname = '%'
    else
      query_nickname = '%'+nickname+'%'
    end
      @position_pages, @positions, @num_positions = paginate_by_sql(Position,
            [ 'SELECT sum(trades.position_qty) as position, tradeable_id, tradeable_type, account_id, journal_id '+
              ' FROM trades, accounts '+
              ' WHERE trades.account_id=accounts.id AND ' +
              '       accounts.nickname like ? GROUP BY account_id, tradeable_id, tradeable_type'+
              ' HAVING position != 0 ',
              query_nickname], MaxPerPage)

    if(@positions.empty?)
      flash.now[:error] = "No Matching positions were found for account: [#{nickname}]"
    end
    @param_name = "nickname"
    @param_value = nickname
    @query_type = "by Account"

    render :template => 'positions/positions_search_output'
  end
end
