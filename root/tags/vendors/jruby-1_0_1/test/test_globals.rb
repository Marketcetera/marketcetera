require 'test/unit'

class TestGlobals < Test::Unit::TestCase
  def check_global_variable
    assert_equal "a global variable.", $output
  end
  def test_global_scope
    $output = "a global variable."
    check_global_variable
  end

  def test_global_alias
    $bar = 5
    alias $foo $bar
    assert_equal 5, $foo

    $bar = 10
    assert_equal 10, $foo

    $foo = 5
    assert_equal 5, $bar
  end

  # Make sure $@ == nil if $! is not nil and $!.backtrace is an array
  class MyWobblyError < StandardError
    def initialize(backtrace) ; @backtrace = backtrace ; end
    def backtrace ; @backtrace ; end
  end

  def test_global_error_vars
    begin
      raise MyWobblyError.new(nil)
    rescue
      assert_equal nil, $@
    end

    begin
      raise MyWobblyError.new("abc")
    rescue
      assert_equal nil, $@
    end

    #inconsistent with set_backtrace but it's what MRI does
    begin
      raise MyWobblyError.new(["abc", 123])
    rescue
      assert $@ != nil
    end

    begin
      raise MyWobblyError.new(["abc", "123"])
    rescue
      assert $@ != nil
    end

    begin
      raise MyWobblyError.new([])
    rescue
      assert $@ != nil
    end
  end
  
  def test_program_name
    assert_equal $0, $PROGRAM_NAME
    old, $0 = $0, "abc"
    $0 = old
  end
  
  def test_locally_scoped_globals
    assert_nothing_raised { print }
    assert_nothing_raised { $_.to_s }
    assert_nothing_raised { $~.to_s }
    $_ = 'one'
    'one' =~ /one/
    second_call
    assert_equal('one', $_)
    assert_equal('one', $~[0])
  end
  
  def second_call
    $_ = 'two'
    'two' =~ /two/
  end

  def test_aliases_backref_globals
    alias $POSTMATCH $'
    alias $PREMATCH $`
    alias $MATCH $&
    alias $LAST_MATCH_INFO $~

    /^is/ =~ "isFubared"

    assert_not_nil($LAST_MATCH_INFO)
    assert_equal($~, $LAST_MATCH_INFO)
	assert_equal "Fubared", $'
    assert_equal $', $POSTMATCH
    assert_equal "", $`
    assert_equal $`, $PREMATCH
    assert_equal "is", $&
    assert_equal $&, $MATCH
  end
  
  def test_english_ignore_case
      alias $IGNORECASE $=
      assert_not_nil($IGNORECASE)
      assert_equal($=, $IGNORECASE)
      assert_nil("fOo" =~ /foo/)
      assert("fOo" =~ /foo/i)
      $= = true
      assert("fOo" =~ /foo/)
      $= = false
   end
end
