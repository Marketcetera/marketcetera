class EquitiesController < ApplicationController
  include ApplicationHelper
  
  auto_complete_for :m_symbol, :root

  def index
    list
    render :action => 'list'
  end

  # GETs should be safe (see http://www.w3.org/2001/tag/doc/whenToUseGet.html)
  verify :method => :post, :only => [ :destroy, :create, :update ],
         :redirect_to => { :action => :list }

  def list
    @equity_pages, @equities = paginate :equities, :per_page => MaxPerPage
  end

  def show
    @equity = Equity.find(params[:id])
  end

  def new
    @equity = Equity.new
  end

  def create
    @equity = Equity.new(params[:equity])
    handle_equity_creation(params, @equity)    

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
    @equity.update_attributes(params[:equity])
    handle_equity_creation(params, @equity)
    if @equity.save
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
  
  private 
  def handle_equity_creation(params, theEquity)
    forceCreate = params[:create_new]
    ref_symbol = get_non_empty_string_from_two(params, :m_symbol, :root, nil)
    found = Equity.get_equity(ref_symbol, forceCreate)
    logger.debug("updating equity to "+((ref_symbol.nil?) ? 'nil' : ref_symbol) + " and found it: "+(!found.nil?).to_s)
#    logger.debug("found equity: "+((found.nil?) ? '' : found.to_s) + " with create: "+forceCreate.to_s)
    # so this is a bit of a hack - the symbol may exist even though the equity may not so we need to re-look it up again
    theEquity.m_symbol = (found.nil?) ? MSymbol.find_by_root(ref_symbol) : found.m_symbol
    
    logger.debug(" returning equity with root: "+((theEquity.nil? || theEquity.m_symbol.nil? || theEquity.m_symbol_root.nil?) ? 'nil' : theEquity.m_symbol_root))
  end
  
end
