module DividendsHelper
  DividendStatusEstimated = 'E'
  DividendStatusAnnounced = 'A'

  def get_human_dividend_status(dividend_status)
    case (dividend_status)
    when DividendStatusAnnounced
        return "Announced"
    when DividendStatusEstimated
        return "Estimated"
    else
        return "Unknown: "+dividend_status
    end
  end

end
