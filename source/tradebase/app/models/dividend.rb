class Dividend < ActiveRecord::Base
  belongs_to :equity
  belongs_to :currency
  validates_numericality_of :amount
  def validate
    errors.add(:amount, "should be positive") unless amount.nil? || amount >= 0
  end
end
