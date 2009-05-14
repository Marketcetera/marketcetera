#
# $License$
#
# author:Toli Kuznets
# author:anshul@marketcetera.com
# since $Release$
# version: $Id$
#
#
include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "org.marketcetera.trade.Factory"
include_class "org.marketcetera.trade.OrderType"
include_class "org.marketcetera.trade.MSymbol"
include_class "org.marketcetera.trade.Side"
include_class "org.marketcetera.trade.TimeInForce"
include_class "java.math.BigDecimal"

class Array
  def shuffle!
    size.downto(1) { |n| push delete_at(rand(n)) }
    self
  end
end

###############################
#  Given parameters indicating
# symbol and size, slices
# the total size into smaller
# chunks and sends market
# orders off at random times
###############################
class OrderSlicer < Strategy

  ##########################################
  # Executed when the strategy is started. #
  #                                        #
  # Use this method to set up data flows   #
  #  and other initialization tasks.       #
  ##########################################
  def on_start
    @symbol = get_parameter("symbol")
    @quantity = get_parameter("quantity").to_i
    @num_sent = 0
    
    if(@symbol==nil || @quantity == nil)
      error "ERROR: Please specify the symbol and/or quantity parameters (right-click on registered Strategy and go to Properties)"
      notify_high "Strategy missing parameters", 
                  "Please specify the symbol and/or quantity parameters (right-click on registered Strategy and go to Properties)"
      return
    end
    
    if (!@symbol.nil? && !@quantity.nil?)
      info "Partitioning #{@quantity} #{@symbol}"

      #if it's more than 100 shares, do partitioning in "round lots"
      if (@quantity > 100)
        partition = generate_partition(@quantity/100).map { |x| x * 100}
      else
        partition = generate_partition(@quantity)
      end
      @num_partitions = partition.length

    end
    # output the partitioning
    info "Partitions: #{partition.inspect}"
    # generate order objects from the partition sizes
    orders = partition.map do |size|
      order = Factory.instance.createOrderSingle()
      order.order_type = OrderType::Market
      order.quantity = BigDecimal.new(Float(size).to_s)
      order.side = Side::Buy
      order.symbol = MSymbol.new(@symbol)
      order.time_in_force = TimeInForce::Day
      order
    end
    # request a callback for each order at a random time (up to 10 seconds)
    orders.each do |order|
      request_callback_after(1000*rand(10), order)
    end
  end
  
  # Generate a random partitioning of the given
  # quantity using a naive recursive algorithm
  # then shuffle the results
  def generate_partition(quantity)
    generate_partition_helper(quantity, []).shuffle!
  end
  
  def generate_partition_helper(quantity, partition)
    if (quantity <= 0)
      return partition
    end
    split = rand(quantity) + 1
    new_partition = partition << split
    generate_partition_helper(quantity - split, new_partition)
  end
  

  ############################################################
  # Executed when the strategy receives a callback requested #
  #  via request_callback_at or request_callback_after       #
  # all timer callbacks come with an order as an argument
  ############################################################
  def on_callback(data)
    send_order data
    @num_sent +=1
    info "sent order #{@num_sent}/#{@num_partitions}"
  end  

end
