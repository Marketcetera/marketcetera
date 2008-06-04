######################################################################
# tc_readable_real.rb
#
# Test case for the FileStat#readable_real? instance method.
######################################################################
require 'test/unit'

class TC_FileStat_ReadableReal_Instance < Test::Unit::TestCase
   WINDOWS = RUBY_PLATFORM.match('mswin')
   
   def setup
      @stat = File::Stat.new(__FILE__)
   end

   def test_readable_basic
      assert_respond_to(@stat, :readable_real?)
   end

   def test_readable
      assert_equal(true, @stat.readable_real?)
   end

   def test_readable_expected_errors
      assert_raises(ArgumentError){ @stat.readable_real?(1) }
   end

   def teardown
      @stat = nil
   end
end
