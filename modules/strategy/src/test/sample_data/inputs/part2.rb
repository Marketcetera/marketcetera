include_class "org.marketcetera.strategy.ruby.Strategy"
class Part2 < Strategy
  def on_start
      @helper = Helper.new
      @helper.do_something_common
      @helper.do_something_part1
      @helper.do_something_part2
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
  def do_something_part2
  end  
end