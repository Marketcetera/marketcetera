module PnlHelper

  # returns the TD that's either red or black in color, depending
  # on whether the P&L is positive or negative
  def pnl_number_class(value)
    classStr = (value < 0) ? ApplicationHelper::RJUST_NUMBER_CLASS_NEG_STR : ApplicationHelper::RJUST_NUMBER_CLASS_STR
  end

  def get_base_currency_pnl_text(pnl_amount, currency)
    if (BaseCurrency == currency.alpha_code)
      return fn(pnl_amount, 2)
    else
      if @base_currency_marks[currency.id].nil? 
      return nil
      else
        return fn(pnl_amount * @base_currency_marks[currency.id], 2)
      end
    end
    
  end
end
