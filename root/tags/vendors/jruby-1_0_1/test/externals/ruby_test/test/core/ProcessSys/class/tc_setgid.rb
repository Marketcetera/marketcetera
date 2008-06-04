######################################################################
# tc_setgid.rb
#
# Test case for the Process::Sys.setgid module method.
#
# Most of these tests will only run on Unix systems, and then only
# as root.
######################################################################
require 'test/unit'
require 'test/helper'

class TC_ProcessSys_Setgid_ModuleMethod < Test::Unit::TestCase
   include Test::Helper

   def setup
      @nobody_gid = Etc.getgrnam('nobody').gid
      @login_gid  = Etc.getpwnam(Etc.getlogin).gid
   end

   def test_setgid_basic
      assert_respond_to(Process::Sys, :setgid)
   end

   if ROOT
      def test_setgid
         assert_nothing_raised{ Process::Sys.setgid(@nobody_gid) }
         assert_equal(@nobody_gid, Process.gid)
         assert_nothing_raised{ Process::Sys.setgid(@login_gid) }
         assert_equal(@login_gid, Process.gid)
      end
   end

   def test_gid_expected_errors
      assert_raises(TypeError){ Process::Sys.setgid('bogus') }
      if WINDOWS
         assert_raises(NotImplementedError){ Process::Sys.setgid(@nobody_gid) }
      end
   end

   def teardown
      @nobody_gid = nil
      @login_gid  = nil
   end
end
