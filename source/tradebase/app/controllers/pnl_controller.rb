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
    @from_date = get_date_from_params(params, :date, "from", "from_date")
    @to_date = get_date_from_params(params, :date, "to", "to_date")
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
    @from_date = get_date_from_params(params, :date, "from", "from_date")
    @to_date = get_date_from_params(params, :date, "to", "to_date")
    if(@to_date.blank?) 
      @to_date = Date.today 
    end
    
    @cashflows = CashFlow.get_cashflows_from_to_in_acct(nil, @from_date, @to_date)
    
    @query_type = "Date"
    
    render :template => 'pnl/pnl_output'
  end

end
