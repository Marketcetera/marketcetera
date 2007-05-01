module PnlHelper

  # returns the TD that's either red or black in color, depending
  # on whether the P&L is positive or negative
  def pnl_number_class(value)
    classStr = (value < 0) ? ApplicationHelper::RJUST_NUMBER_CLASS_NEG_STR : ApplicationHelper::RJUST_NUMBER_CLASS_STR
  end
end
