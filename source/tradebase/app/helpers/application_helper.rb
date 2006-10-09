# Methods added to this helper will be available to all templates in the application.
module ApplicationHelper
include QF_BuyHelper

  def get_equity(ref_symbol)
    symbol = MSymbol.find(:first, :conditions=>["root = ?", ref_symbol])
    if(symbol == nil)
      symbol = MSymbol.new(:root => ref_symbol)
      symbol.save
      equity = Equity.new(:m_symbol => symbol)
      equity.save
      return equity
    else
      return Equity.find(symbol.id)
    end
  end

  # Lookup the specified currency. If incoming currency string is empty, returns USD by default
  def get_currency(cur_string)
    if(cur_string == nil || cur_string == '') 
      cur_string = 'USD'
      p("No currency specified, defaulting to USD")
    end
    currency = Currency.find(:first, :conditions=>["alpha_code = ?", cur_string])
    return currency
  end
  
  def get_account_by_nickname(nickname)
    return Account.find(:first, :conditions=>["nickname = ?", nickname])
  end

  def auto_complete_for_currency_alpha_code
    auto_complete_responder_for_currency_alpha_code params[:currency][:alpha_code]
  end
  
  def get_debit_or_credit(cash_flow, accounting_type)
    case (accounting_type) 
    when 'A'
      return cash_flow < 0 ? :credit : :debit
    when 'L'
      return cash_flow < 0 ? :debit : :credit 
    when 'R'
      return cash_flow < 0 ? :debit : :credit
    when 'E'
      return cash_flow < 0 ? :credit : :debit
    else
      return nil
    end
  
  end

  private
  def auto_complete_responder_for_currency_alpha_code(value)
    @currencies = Currency.find(:all, 
      :conditions => [ 'LOWER(alpha_code) LIKE ? AND obsolete=\'N\'',
      '%' + value.downcase + '%'], 
      :order => 'alpha_code ASC',
      :limit => 10)
    render :partial => 'shared/currencies_auto_complete'
  end

end
