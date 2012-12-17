class Mark < ActiveRecord::Base
  include TradesHelper

  belongs_to :tradeable, :polymorphic => true
 
  MarkTypeClose='C'
  MarkTypeIntraDay='I'
  MARK_TYPES = [ ['Close', MarkTypeClose], ['Intra-Day', MarkTypeIntraDay] ]

  validates_uniqueness_of :mark_date, :scope => [:tradeable_id, :tradeable_type], 
    :message => "Already have a mark on that date. Please update an existing mark instead."

  validates_numericality_of(:mark_value, :message => "should be a number.")
  
  def validate
    errors.add(:mark_value, "should be a zero or positive value.") unless (!mark_value.blank? && mark_value >= 0)
    errors.add(:symbol, "cannot be empty.") unless !tradeable_m_symbol_root.blank?
    errors.add(:mark_date, "should not be in the future.") unless (!mark_date.blank? && (mark_date <= Date.today))
  end

  def before_create()
    if (self.mark_date.nil?)
      self.mark_date = Date::today()
    end
  end

  def tradeable_m_symbol_root
    (self.tradeable.nil? || self.tradeable.m_symbol.nil?) ? nil : self.tradeable.m_symbol.root
  end

  def security_type
    TradesHelper::SecurityTypeEquity
  end

  # deal with Equities here, deal with others in subclasses
  def tradeable_m_symbol_root=(inSymbol)
    self.tradeable = Equity.get_equity(inSymbol)
  end

  # pretty-print the errors
  def pretty_print_errors
    error_str = "{"
    if(errors.length > 0)
      if(!errors[:symbol].blank?)
        error_str += "Symbol #{errors[:symbol]} "
      end

      # mark_value can have 2 errors if it's not a number, so check if the erros[:mark_value] returns an Array 
      if(!errors[:mark_value].blank?)
        if(errors[:mark_value].instance_of?(Array))
          error_str += "Value #{errors[:mark_value][0]} "
        else
          error_str += "Value #{errors[:mark_value]} "
        end
      end
      if(!errors[:mark_date].blank?)
        error_str += "Date #{errors[:mark_date]} "
      end
    end
    error_str += "}"
  end
end
