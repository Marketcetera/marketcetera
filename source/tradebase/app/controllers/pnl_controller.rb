class PnlController < ApplicationController
  auto_complete_for :m_symbol, :root, {}
  auto_complete_for :account, :nickname, {}
  include ApplicationHelper
  
  def index
    # display the index page   
  end

  def by_account
    nickname = get_non_empty_string_from_two(params, :account, :nickname, "nickname")
    theAcct = Account.find_by_nickname(nickname)
    @from_date = VDate.get_date_from_params(params, :date, "from", "from_date").as_date
    @to_date = VDate.get_date_from_params(params, :date, "to", "to_date").as_date
    if(@to_date.blank?) 
      @to_date = Date.today 
    end
    
    begin
      @cashflows = CashFlow.get_cashflows_from_to_in_acct(theAcct, @from_date, @to_date)
    rescue Exception => ex
      logger.debug("Error generating cashflow for #{theAcct.nickname}: " + ex);
      flash.now[:error] = ex.to_s
      @cashflows = []
    end
    @param_name = "nickname"
    @param_value = theAcct.nickname
    @query_type = "Account"
    
    render :template => 'pnl/pnl_output'
  end

  def by_date
    suffix = (params[:suffix].nil?) ? '' : params[:suffix]
    @report = ReportWithToFromDates.new(params, suffix)
    if(!@report.valid?)
      render :action => :index
      return
    end
    @from_date = @report.from_date.as_date
    @to_date = @report.to_date.as_date
    
    @cashflows = CashFlow.get_cashflows_from_to_in_acct(nil, @from_date, @to_date)
    
    @query_type = "Date"
    
    render :template => 'pnl/pnl_output'
  end

end
