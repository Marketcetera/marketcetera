# Methods added to this helper will be available to all templates in the application.
module ApplicationHelper
include QF_BuyHelper

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
