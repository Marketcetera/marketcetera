include_class "org.marketcetera.strategy.ruby.Strategy"
include_class "java.lang.Long"
class Part1 < Strategy
  def on_start
      callbackCounter = get_property("onCallback")
      if(callbackCounter == nil)
          set_property("onCallback",
                       "0")
      end
      @helper = Helper.new
      @helper.do_something_common
      @helper.do_something_part1      
  end
  def on_ask(ask)
      set_property("ask" + self.to_s,
                   "part1")
  end
  def on_callback(data)
      callbackCounter = Long.parseLong(get_property("onCallback"))
      callbackCounter += 1
      set_property("onCallback",
                   Long.toString(callbackCounter))
      set_property("callback" + Long.toString(callbackCounter),
                   self.to_s)
  end
  def on_stop
      @helper.do_something_common
      @helper.do_something_part1
      @helper.do_something_part2
  end
end

class Helper
    def do_something_common
    end  
  def do_something_part1
  end
end
