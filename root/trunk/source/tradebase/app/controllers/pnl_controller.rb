class PnlController < ApplicationController
  auto_complete_for :m_symbol, :root, {}
  auto_complete_for :account, :nickname, {}
  include ApplicationHelper
  
  def index
    # display the index page   
  end

  def by_account
    nickname = get_non_empty_string_from_two(params, :account, :nickname, "nickname")
    start_date = 
    end_date = 
    positions_for_acct = 

    @param_name = "nickname"
    @param_value = nickname
    @query_type = "Account"
    
    render :template => 'pnl/pnl_output'
  end
end
