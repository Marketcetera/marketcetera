require 'test/unit'

class TestParsing < Test::Unit::TestCase
  # JRUBY-376
  def test_parse_empty_parens
    assert_raises(TypeError) { n = 3 * () }
   
    # JRUBY-755
    assert(nil == ())
    assert(().nil?)
  end
  
  def test_parse_literal_char
    assert_equal(96, ?a-1)
  end
end
