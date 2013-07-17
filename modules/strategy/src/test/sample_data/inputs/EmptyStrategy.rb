require 'java'
java_import org.marketcetera.strategy.ruby.Strategy

class EmptyStrategy < Strategy
  def on_ask(ask)
        set_property("onAsk",
                     ask.toString());
  end  
end
