class TestScript
  def on_message(message)
    $quote_count = $quote_count + 1
  end
end
