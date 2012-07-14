class SubAccountsController < ApplicationController
  auto_complete_for :sub_account_type, :description, {}

  def index
    list
    render :action => 'list'
  end

  def list
    @sub_account_pages, @sub_accounts = paginate :sub_accounts, :per_page => MaxPerPage
  end

  def show
    @sub_account = SubAccount.find(params[:id])
  end

end
