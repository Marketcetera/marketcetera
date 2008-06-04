######################################################################
# tc_rdev.rb
#
# Test case for the FileStat#rdev instance method.
######################################################################
require 'test/unit'

class TC_FileStat_Rdev_Instance < Test::Unit::TestCase
   def setup
      @stat = File::Stat.new(__FILE__)
   end

   def test_rdev_basic
      assert_respond_to(@stat, :rdev)
      assert_kind_of(Fixnum, @stat.rdev)
   end

   def test_rdev
      unless RUBY_PLATFORM.match('mswin')
         assert_equal(true, @stat.rdev == 0)
         assert_equal(true, File::Stat.new('/dev/stdin').rdev > 0)
      end
   end

   def test_rdev_expected_errors
      assert_raises(ArgumentError){ @stat.rdev(1) }
   end

   def teardown
      @stat = nil
   end
end
