require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'

class NumberFormatHelperTest < MarketceteraTestBase
  include NumberFormatHelper

  def test_format_function
    assert_equal "23.59", format_number(BigDecimal.new("23.59"), 2)
    assert_equal "23.50", format_number(BigDecimal.new("23.5"), 2)
    assert_equal "23.50", format_number(BigDecimal.new("23.499"), 2)
    assert_equal "23.00", format_number(BigDecimal.new("23.0"), 2)
    assert_equal "23.00", format_number(BigDecimal.new("23"), 2)
    assert_equal "23.456", format_number(BigDecimal.new("23.456"), 3)
    assert_equal "23.456", format_number(BigDecimal.new("23.4563478"), 3)
    assert_equal "23.46", format_number(BigDecimal.new("23.456"), 2)
  
    # test string not BigDecimal
    assert_equal "23.46", format_number("23.456", 2)

    # test fixnum
    assert_equal "1.00", format_number(1, 2)
    assert_equal "1.22", format_number(1.22, 2)

    # test alias
    assert_equal "23.456", fn(BigDecimal.new("23.4563478"), 3)
    assert_equal "23.46", fn(BigDecimal.new("23.456"), 2)
    
    assert_equal "", fn("", 2)

  end
  
  def test_format_function_strip_fractions
    assert_equal "23.00", format_number(BigDecimal.new("23"), 2, false)
    assert_equal "23", format_number(BigDecimal.new("23"), 2, true)
    assert_equal "77.35", format_number(BigDecimal.new("77.35"), 2, true)
    assert_equal "-77.35", format_number(BigDecimal.new("-77.35"), 2, true)
  end
  
  def test_display_non_zero_value
    assert_equal("", display_non_zero_value(0))
    assert_equal("", display_non_zero_value(0.0))
    assert_equal("bob", display_non_zero_value("bob"))
    assert_equal(23.45, display_non_zero_value(23.45))
  end
end