class SubAccountsController < ApplicationController
  auto_complete_for :sub_account_type, :description, {}

  def index
    list
    render :action => 'list'
  end

  # GETs should be safe (see http://www.w3.org/2001/tag/doc/whenToUseGet.html)
  verify :method => :post, :only => [ :destroy, :create, :update ],
         :redirect_to => { :action => :list }

  def list
    @sub_account_pages, @sub_accounts = paginate :sub_accounts, :per_page => 10
  end

  def show
    @sub_account = SubAccount.find(params[:id])
  end

  def new
    @sub_account = SubAccount.new
    account_id = params[:account_id]
    if(account_id.blank?)
      @sub_account.errors.add(:account, "Please specify the parent account. <br/>" +
          "Create a new sub-account from a specific <a href=\"" + 
          url_for(:controller => 'accounts', :action => 'list') + "\">account</a> listing")
    else 
      @account = Account.find(account_id)  
    end
    
  end

  def create
    @sub_account = SubAccount.new(params[:sub_account])
    @sub_account.sub_account_type = SubAccountType.find(:first, 
      :conditions=>['description=?', params[:sub_account_type][:description]])
    @sub_account.account_id = params[:account_id]
    if @sub_account.save
      flash[:notice] = 'SubAccount was successfully created.'
      redirect_to :action => 'list'
    else
      render :action => 'new'
    end
  end

  def edit
    @sub_account = SubAccount.find(params[:id])
  end

  def update
    @sub_account = SubAccount.find(params[:id])
    if @sub_account.update_attributes(params[:sub_account])
      flash[:notice] = 'SubAccount was successfully updated.'
      redirect_to :action => 'show', :id => @sub_account
    else
      render :action => 'edit'
    end
  end

  def destroy
    SubAccount.find(params[:id]).destroy
    redirect_to :action => 'list'
  end
end
