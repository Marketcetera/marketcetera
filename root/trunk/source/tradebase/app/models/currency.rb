class Currency < ActiveRecord::Base
  validates_uniqueness_of :alpha_code
  validates_uniqueness_of :numeric_code
end
