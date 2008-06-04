require 'test/unit'
require 'tempfile'
require 'rbconfig'

#
# Find the name of the interpreter.
#  FIXME: This should be moved to a common location.

$interpreter = File.join(Config::CONFIG["bindir"], 
			 Config::CONFIG["RUBY_INSTALL_NAME"])

class TestObjectSpace < Test::Unit::TestCase

    #
    # Check that two arrays contain the same "bag" of elements.
    # A mathematical bag differs from a "set" by counting the
    # occurences of each element. So as a bag [1,2,1] differs from
    # [2,1] (but is equal to [1,1,2]).
    #
    # The method only relies on the == operator to match objects
    # from the two arrays. The elements of the arrays may contain
    # objects that are not "Comparable".
    # 
    # FIXME: This should be moved to common location.
    def assert_bag_equal(expected, actual)
      # For each object in "actual" we remove an equal object
      # from "expected". If we can match objects pairwise from the
      # two arrays we have two equal "bags". The method Array#index
      # uses == internally. We operate on a copy of "expected" to
      # avoid destructively changing the argument.
      #
      expected_left = expected.dup
      actual.each do |x|
        if j = expected_left.index(x)
          expected_left.slice!(j)
        end
      end
      assert( expected.length == actual.length && expected_left.length == 0,
             "Expected: #{expected.inspect}, Actual: #{actual.inspect}")
    end

  def test_s__id2ref
    s = "hello"
    t = ObjectSpace._id2ref(s.__id__)
    assert_equal(s, t)
    assert_equal(s.__id__, t.__id__)
  end


  # finalizer manipulation
  def test_s_finalizers
    tf = Tempfile.new("tf")
    begin
      tf.puts %{
	a = "string"
	ObjectSpace.define_finalizer(a) { puts "OK" }
      }
      tf.close
      if ENV['OS'] =~ /\AWin/
        command = %{"#$interpreter" "#{tf.path}"}
      else
        command = %{#$interpreter #{tf.path}}
      end
      IO.popen(command) do |p|
	assert_equal("OK", p.gets.chomp)
      end
    ensure
      tf.close(true)
    end
  end

  class A;      end
  class B < A;  end
  class C < A;  end
  class D < C;  end

  def test_s_each_object
    a = A.new
    b = B.new
    c = C.new
    d = D.new

    res = []
    ObjectSpace.each_object(TestObjectSpace::A) { |o| res << o }
    assert_bag_equal([a, b, c, d], res)

    res = []
    ObjectSpace.each_object(B) { |o| res << o }
    assert_bag_equal([b], res)

    res = []
    ObjectSpace.each_object(C) { |o| res << o }
    assert_bag_equal([c, d], res)
  end

end
