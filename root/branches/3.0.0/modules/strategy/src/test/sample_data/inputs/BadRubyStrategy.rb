module Marketcetera
  include_class "org.marketcetera.strategy.ruby.Strategy"
end
class BadRubyStrategy < Marketcetera::Strategy
  this wont compile!
end