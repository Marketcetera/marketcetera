######################################################################
# tc_rid.rb
#
# Test case for the Process.rid module method. Most tests skipped
# on MS Windows.
######################################################################
require 'test/unit'
require 'test/helper'

class TC_ProcessUID_Rid_ModuleMethod < Test::Unit::TestCase
   include Test::Helper

   def setup
      unless WINDOWS
         @gid = Etc.getpwnam(Etc.getlogin).gid
      end
   end

   def test_rid_basic
      assert_respond_to(Process::UID, :rid)
      assert_nothing_raised{ Process::UID.rid }
   end

   unless WINDOWS
      def test_rid
         assert_equal(Process.gid, Process::UID.rid)
         if ROOT
            assert_equal(0, Process::UID.rid)
         else
            assert_equal(@gid, Process::UID.rid)
         end
      end

      def test_rid_expected_errors
         assert_raises(ArgumentError){ Process::UID.rid(0) }
      end
   end

   def teardown
      unless WINDOWS
         @gid = nil
      end
   end
end
