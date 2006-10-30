require 'bigdecimal'

# Patch bigDecimal to print it nicely formatted instead of default engineering notation
BigDecimal.class_eval do
  alias :_original_to_s :to_s

  def to_s(format="F")
    _original_to_s(format)
  end
end
