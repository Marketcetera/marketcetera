require 'acts_as_versioned'

class Mark < ActiveRecord::Base

  belongs_to :equity
 
  acts_as_versioned
  
  MARK_TYPES = [ ['Close', 'C'], ['Intra-Day', 'I'] ]

  validates_uniqueness_of :equity_id, :scope => :mark_date, 
    :message => "Already have a mark on that date. Please update an existing mark."

  validates_numericality_of(:mark_value, :message => "should be a number.")
  
  def validate
    errors.add(:mark_value, "should be a zero or positive value.") unless (!mark_value.blank? && mark_value >= 0)
    errors.add(:symbol, "cannot be empty") unless !equity_m_symbol_root.blank?
    errors.add(:mark_date, "should not be in the future") unless (!mark_date.blank? && (mark_date <= Date.today))
  end

  def before_create()
    if (self.mark_date.nil?)
      self.mark_date = Date::today()
    end
  end

  def equity_m_symbol_root
      (self.equity.nil? || self.equity.m_symbol.nil?) ? nil : self.equity.m_symbol.root
  end

end
