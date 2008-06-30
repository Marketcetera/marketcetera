# Superclass for reports that have one date
# Can read the date from params hash
class ReportOnDate < Tableless
  
  attr_reader :on_date, :suffix

  # takes an incoming params hash
  def initialize(params)
    @on_date = VDate.get_date_from_params(params, 'date', "on", "on_date")
  end
  
  def validate
    errors.add(:on_date, "is not a valid date #{@on_date.to_s}") unless (!on_date.nil? && on_date.valid?)
  end
end