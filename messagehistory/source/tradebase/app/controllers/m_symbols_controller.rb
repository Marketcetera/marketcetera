class MSymbolsController < ApplicationController

  def index
    list
    render :action => 'list'
  end

  # GETs should be safe (see http://www.w3.org/2001/tag/doc/whenToUseGet.html)
  verify :method => :post, :only => [ :destroy, :create, :update ],
         :redirect_to => { :action => :list }

  def list
    @m_symbol_pages, @m_symbols = paginate :m_symbols, :per_page => MaxPerPage
  end

  def show
    @m_symbol = MSymbol.find(params[:id])
  end

  def new
    @m_symbol = MSymbol.new
  end

  def create
    @m_symbol = MSymbol.new(params[:m_symbol])
    if @m_symbol.save
      flash[:notice] = 'Symbol was successfully created.'
      redirect_to :action => 'list'
    else
      render :action => 'new'
    end
  end

  def edit
    @m_symbol = MSymbol.find(params[:id])
  end

  def update
    @m_symbol = MSymbol.find(params[:id])
    if @m_symbol.update_attributes(params[:m_symbol])
      flash[:notice] = 'Symbol was successfully updated.'
      redirect_to :action => 'show', :id => @m_symbol
    else
      render :action => 'edit'
    end
  end

  def destroy
    MSymbol.find(params[:id]).destroy
    redirect_to :action => 'list'
  end
end
