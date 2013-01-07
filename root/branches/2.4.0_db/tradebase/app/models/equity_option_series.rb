class EquityOptionSeries < ActiveRecord::Base
  belongs_to :m_symbol
  belongs_to :currency, :foreign_key=>"cash_deliverable_currency_id"
  has_many :equity_options
  has_many :equity_option_underlyings
end
