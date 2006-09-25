class Equity < ActiveRecord::Base
  has_many :dividends
  belongs_to :m_symbol
end
