######################################################################
# tc_socket.rb
#
# Test case for the FileStat#socket instance method.
######################################################################
require 'test/unit'

class TC_FileStat_Socket_Instance < Test::Unit::TestCase
   WINDOWS = RUBY_PLATFORM.match('mswin')
   
   def setup
      @stat = File::Stat.new(__FILE__)
      @file = WINDOWS ? 'NUL' : '/dev/stdin'
   end

   def test_socket_basic
      assert_respond_to(@stat, :socket?)
   end

   def test_socket
      assert_equal(false, @stat.socket?)
      assert_equal(false, File::Stat.new(@file).socket?)
   end

   def test_socket_expected_errors
      assert_raises(ArgumentError){ @stat.socket?(1) }
   end

   def teardown
      @stat = nil
      @file = nil
   end
end
