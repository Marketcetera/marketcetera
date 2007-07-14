require File.dirname(__FILE__) + '/../test_helper'
require File.dirname(__FILE__) + '/marketcetera_test_base'
require 'stringio'

class MarksImportTest < MarketceteraTestBase
  include ApplicationHelper

  def test_validate
    mi2 = MarksImport.new(nil)
    assert !mi2.valid?
    assert_equal 1, mi2.errors.length

    mi3 = MarksImport.new(StringIO.new("bob"))
    assert mi3.valid?
  end

  def test_add_line_errors
    mi = MarksImport.new("some data")
    mi.add_error_for_line("line", ["some error"])
    assert_not_nil mi.line_errors
    assert_equal 1, mi.line_errors.length
    assert_equal "line", mi.line_errors[0][:line]
    assert_equal ["some error"], mi.line_errors[0][:error]
  end

  # try something that's not incoming StringIO class
  def test_not_string_content
    data = BigDecimal.new("234")
    mi = MarksImport.new(data)
    assert !mi.valid?
  end

end