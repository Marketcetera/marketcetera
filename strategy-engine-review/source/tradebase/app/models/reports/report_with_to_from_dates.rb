# Superclass for reports that have To and From dates
# Also takes an optional form suffix for forms with 2 or more sets of to/from dates
# Can read the dates from params hash
class ReportWithToFromDates < Tableless
  
  attr_reader :from_date, :to_date, :suffix

  # takes an incoming params hash
  def initialize(params, suffix='')
    @from_date = VDate.get_date_from_params(params, 'date_'+suffix, "from", "from_date")
    @to_date = VDate.get_date_from_params(params, 'date_'+suffix, "to", "to_date")
    @suffix = suffix
  end
  
  def validate
    errors.add(:from_date, "is not a valid date #{@from_date.to_s}") unless (!from_date.nil? && from_date.valid?)
    errors.add(:to_date, "is not a valid date #{@to_date.to_s}") unless (!to_date.nil? && to_date.valid?)
    if ((!from_date.nil? && !to_date.nil? && from_date.valid? && to_date.valid?) && (from_date.as_date > to_date.as_date))
      errors.add(:from_date, "is later than 'to date'") 
    end
  end
   
end