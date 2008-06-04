######################################################################
# tc_rdev_major.rb
#
# Test case for the FileStat#rdev_major instance method.
######################################################################
require 'test/unit'

class TC_FileStat_RdevMajor_Instance < Test::Unit::TestCase
   WINDOWS = RUBY_PLATFORM.match('mswin')
   
   def setup
      @stat = File::Stat.new(__FILE__)
   end

   def test_rdev_major_basic
      assert_respond_to(@stat, :rdev_major)
      
      if WINDOWS
         assert_nil(@stat.rdev_major)
      else
         assert_kind_of(Fixnum, @stat.rdev_major)
      end
   end

   def test_rdev_major
      unless RUBY_PLATFORM.match('mswin')
         assert_equal(true, @stat.rdev_major == 0)
         assert_equal(true, File::Stat.new('/dev/stdin').rdev_major > 0)
      end
   end

   def test_rdev_major_expected_errors
      assert_raises(ArgumentError){ @stat.rdev_major(1) }
   end

   def teardown
      @stat = nil
   end
end
