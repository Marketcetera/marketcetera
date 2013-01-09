class EquityOptionUnderlying < ActiveRecord::Base
  belongs_to :equity_option_series
  belongs_to :m_symbol, :foreign_key => "underlying_m_symbol_id"
end
