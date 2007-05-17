module Subdir
	class TestScript
	  def on_market_data_message(message)
	    $quote_count = $quote_count + 81
	  end
	  
	  def on_fix_message(message)
	    $fix_count = $fix_count + 81
	  end 
	end
end