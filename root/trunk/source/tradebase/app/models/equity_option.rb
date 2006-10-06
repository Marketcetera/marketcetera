class EquityOption < ActiveRecord::Base
  belongs_to :equity_option_series
  belongs_to :currency, :foreign_key => "strike_price_currency_id"
  belongs_to :m_symbol

  has_many :trades, :as => :tradeable
end
