require 'bigdecimal'
#require_dependency "big_decimal_formatter"

BigDecimal.class_eval do
  alias :_original_to_s :to_s

  def to_s(format="F")
    _original_to_s(format)
  end
end
