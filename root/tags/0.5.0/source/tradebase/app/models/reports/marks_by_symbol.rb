# for the marks by symbol report
class MarksBySymbol < ReportWithToFromDates
  
  attr_reader :symbol

  validates_presence_of :symbol

  def validate
    super
    errors.add(:currency_pair, "[#{@symbol}] is unknown.") if @unknown_currency_pair
  end

  def initialize(symbol, params, suffix)
    super(params, suffix)
    @symbol = symbol
  end

  # Set the flag if the CurrencyPair is unknown
  def unknown_currency_pair=(unknown)
    @unknown_currency_pair = unknown
  end

end