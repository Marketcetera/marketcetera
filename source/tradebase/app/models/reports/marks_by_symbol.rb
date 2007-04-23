# for the marks by symbol report
class MarksBySymbol < ReportWithToFromDates
  
  attr_reader :symbol

  validates_presence_of :symbol

  def initialize(symbol, params, suffix)
    super(params, suffix)
    @symbol = symbol
  end
end