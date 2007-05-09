module Subdir
  class TestScript
    def on_message(message)
      $quote_count = $quote_count + 81
    end
  end
end