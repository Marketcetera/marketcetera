#
# Add your destination definitions here
# can also be used to configure filters, and processor groups
#
# Speicfy the topic that is used to broadcast orders that need to be recorded
ActiveMessaging::Gateway.define do |s|
  #s.destination :orders, '/queue/Orders'
  #s.filter :incoming, MyFilter.new
  #s.processor_group :group1, :order_processor
  
  s.destination :record_trade, '/queue/trade-recorder'
  
end