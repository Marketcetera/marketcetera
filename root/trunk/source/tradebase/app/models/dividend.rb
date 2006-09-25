class Dividend < ActiveRecord::Base
  belongs_to :equity
  belongs_to :currency
end
