class EquitiesController < ApplicationController
  def index
    list
    render :action => 'list'
  end

  # GETs should be safe (see http://www.w3.org/2001/tag/doc/whenToUseGet.html)
  verify :method => :post, :only => [ :destroy, :create, :update ],
         :redirect_to => { :action => :list }

  def list
    @equity_pages, @equities = paginate :equities, :per_page => 10
  end

  def show
    @equity = Equity.find(params[:id])
  end

  def new
    @equity = Equity.new
  end

  def create
    @equity = Equity.new(params[:equity])
    if @equity.save
      flash[:notice] = 'Equity was successfully created.'
      redirect_to :action => 'list'
    else
      render :action => 'new'
    end
  end

  def edit
    @equity = Equity.find(params[:id])
  end

  def update
    @equity = Equity.find(params[:id])
    if @equity.update_attributes(params[:equity])
      flash[:notice] = 'Equity was successfully updated.'
      redirect_to :action => 'show', :id => @equity
    else
      render :action => 'edit'
    end
  end

  def destroy
    Equity.find(params[:id]).destroy
    redirect_to :action => 'list'
  end
end
