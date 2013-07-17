class WrongClass
  def on_ask(ask)
    puts "received " + ask;
  end  
  def on_bid(bid)
    puts "received " + bid;
  end  
  def on_callback(data)
    puts "received " + data;
  end  
  def on_execution_report(execution_report)
    puts "received " + execution_report;
  end  
  def on_trade(trade)
    puts "received " + trade;
  end  
  def on_other(data)
    puts "received " + data;
  end  
end
