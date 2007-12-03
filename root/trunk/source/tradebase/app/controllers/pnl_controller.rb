class PnlController < ApplicationController
  auto_complete_for :m_symbol, :root, {}
  auto_complete_for :account, :nickname, {}
  include ApplicationHelper
  
  def index
    # display the index page   
  end

  # entry point into the search
  def report
      nickname_str = get_non_empty_string_from_two(params, :account, :nickname, "nickname")
      suffix = (params[:suffix].nil?) ? '' : params[:suffix]
      @report = ReportWithToFromDates.new(params, suffix)
      if(!@report.valid?)
        render :action => :index
        return
      end
      @from_date = @report.from_date.as_date
      @to_date = @report.to_date.as_date
      if(nickname_str.blank?)
        aggregate
      else
        by_account(nickname_str, params, suffix)
      end
  end

  private
  def by_account(nickname_str, params, suffix)
    @report = PnlByAccount.new(nickname_str, params, suffix)
    if(!@report.valid?)
      render :action => :index
      return
    end
    theAcct = @report.account
            
    begin
      byAcctCashflows = CashFlow.get_cashflows_from_to_in_acct(theAcct, @from_date, @to_date)
      byAcctCashflows.keys.each {|key|
        logger.debug("got cashflow from #{@from_date.to_s} to #{@to_date.to_s} of $" + byAcctCashflows[key].values.to_s)
      }
      # Check to make sure we get some cashflow back
      cashflows = (byAcctCashflows.length != 1) ? [] \
                                                : byAcctCashflows[theAcct.nickname].values.sort { |x,y| x.symbol <=> y.symbol}
      @nickname = theAcct.nickname
    rescue Exception => ex
      logger.debug("Error generating cashflow for #{theAcct.nickname}: " + ex);
      flash.now[:error] = "Error generating cashflow for #{theAcct.nickname}: "+ ex.to_s
      cashflows = []
    end
    @cashflow_pages, @cashflows = paginate_collection(cashflows, params)
    render :template => 'pnl/pnl_by_account'
  end

  def aggregate
    begin
      missing_marks = ProfitAndLoss.get_missing_equity_marks(@from_date)
      missing_marks = missing_marks + ProfitAndLoss.get_missing_equity_marks(@to_date)
      if (missing_marks.length > 0)
        @missing_mark_pages, @missing_marks = paginate_collection(cashflows, params)
        render :template => 'pnl/missing_marks'
      else
        
      end
      pnls = ProfitAndLoss.get_equity_pnl(@from_date, @to_date)
      pnls = pnls + ProfitAndLoss.get_forex_pnl(@from_date, @to_date)

      @pnl_pages, @pnls = paginate_collection(pnls, params)
      render :template => 'pnl/pnl_aggregate'

    rescue Exception => ex
      logger.debug("Error generating aggregate cashflow: " + ex);
      logger.debug(ex.backtrace)
      flash.now[:error] = "Error generating aggregate cashflow: " + ex.to_s
      cashflows = []
    end

  end

end
