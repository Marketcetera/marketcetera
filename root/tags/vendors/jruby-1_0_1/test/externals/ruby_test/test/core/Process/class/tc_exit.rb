#####################################################################
# tc_exit.rb
#
# Test case for the Process.exit module method.
#####################################################################
require 'test/unit'

class TC_Process_Abort_ModuleMethod < Test::Unit::TestCase
   def test_exit_basic
      assert_respond_to(Process, :exit)
   end

   # The default exit status is 0, not 1. The documentation in Programming
   # Ruby, 2nd ed, is incorrect.
   #
   def test_exit
      fork{ Process.exit }
      pid, status = Process.wait2
      assert_equal(0, status.exitstatus)
   end

   def test_exit_with_true_status
      fork{ Process.exit(true) }
      pid, status = Process.wait2

      assert_equal(0, status.exitstatus)
   end

   def test_exit_with_false_status
      fork{ Process.exit(false) }
      pid, status = Process.wait2

      assert_equal(1, status.exitstatus)
   end

   def test_exit_with_numeric
      fork{ Process.exit(99) }
      pid, status = Process.wait2

      assert_equal(99, status.exitstatus)
   end

   def teardown
      Process.waitall
   end
end
