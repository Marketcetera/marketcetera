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
    errors.add(:from_date, "Invalid from date") unless (!from_date.nil? && from_date.valid?)
    errors.add(:to_date, "Invalid to date") unless (!to_date.nil? && to_date.valid?)
  end
   
end