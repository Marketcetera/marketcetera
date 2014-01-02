require 'java'
module Marketcetera
  java_import org.marketcetera.strategy.ruby.Strategy
end
class BadRubyStrategy < Marketcetera::Strategy
  this wont compile!
end
