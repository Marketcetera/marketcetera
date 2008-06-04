###########################################################
# tc_aset.rb
#
# Test suite for the Hash#[]= instance method.
###########################################################
require "test/unit"

class TC_Hash_Aset_Instance < Test::Unit::TestCase
   def setup
      @hash = {"foo", 1, :bar, 2, nil, 3, false, 4}
   end

   def test_aset_basic
      assert_respond_to(@hash, :[]=)
      assert_nothing_raised{ @hash["hello"] = "world" }
   end

   def test_aset
      assert_equal(5, @hash["baz"] = 5)
      assert_equal("test", @hash["baz"] = "test")
      assert_equal(nil, @hash[:test] = nil)
      assert_equal(false, @hash[:lala] = false)
   end

   def teardown
      @hash = nil
   end
end
