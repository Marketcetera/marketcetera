include_class "org.marketcetera.photon.scripting.Strategy"

class TestScript < Strategy
  def on_market_data_message(message)
    $quote_count = $quote_count + 1
  end
  
  def on_fix_message(message)
    $fix_count = $fix_count + 1
  end
  
end
