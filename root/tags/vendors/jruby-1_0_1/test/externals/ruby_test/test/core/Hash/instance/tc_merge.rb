####################################################################
# tc_merge.rb
#
# Test suite for the Hash#merge and Hash#merge! instance methods.
####################################################################
require "test/unit"

class TC_Hash_Merge_Instance < Test::Unit::TestCase
   def setup
      @hash1 = {"a", 1, "b", 2}
      @hash2 = {"c", 3, "d", 4}
   end

   def test_merge_basic
      assert_respond_to(@hash1, :merge)
      assert_respond_to(@hash1, :merge!)
      assert_nothing_raised{ @hash1.merge(@hash2) }
      assert_nothing_raised{ @hash1.merge!(@hash2) }
   end

   def test_merge
      assert_equal({"a",1,"b",2,"c",3,"d",4}, @hash1.merge(@hash2))
      assert_equal({"a",1,"b",2}, @hash1.merge({}))
      assert_equal({"b",2,"a",4}, @hash1.merge({"a",4}))
      assert_equal({"a",1,"b",2,nil,1}, @hash1.merge({nil,1}))
      assert_equal({"a", 1, "b", 2}, @hash1)
      assert_equal({"c", 3, "d", 4}, @hash2)
   end

   def test_merge_bang
      assert_equal({"a",1,"b",2,"c",3,"d",4}, @hash1.merge!(@hash2))
      assert_equal({"a",1,"b",2,"c",3,"d",4}, @hash1.merge!({}))
      assert_equal({"a",1,"b",2,"c",3,"d",4}, @hash1.merge!({"a",1}))
      assert_equal({"a", 1, "b", 2, "c", 3, "d", 4}, @hash1)
      assert_equal({"c", 3, "d", 4}, @hash2)
   end

   def test_merge_with_block
      assert_equal({"a",1,"b",2,"c",3,"d",4}, @hash1.merge(@hash2){|k,o,n| o })
      assert_equal({"a",4,"b",2}, @hash1.merge({"a",4}){ |k,o,n| n })
      assert_equal({"a",1,"b",2}, @hash1.merge({"a",4}){ |k,o,n| o })
      assert_equal({"a", 1, "b", 2}, @hash1)
      assert_equal({"c", 3, "d", 4}, @hash2)
   end

   def test_merge_bang_with_block
      assert_equal({"a",1,"b",2,"c",3,"d",4}, @hash1.merge!(@hash2){|k,o,n| o })
      assert_equal({"a",1,"b",2,"c",3,"d",4}, @hash1)
      assert_equal({"a",4,"b",2}, {"a",1,"b",2}.merge!({"a",4}){ |k,o,n| n })
      assert_equal({"a",1,"b",2}, {"a",1,"b",2}.merge!({"a",4}){ |k,o,n| o })
      assert_equal({"c", 3, "d", 4}, @hash2)
   end

   def test_merge_expected_errors
      assert_raises(TypeError){ @hash1.merge("foo") }
      assert_raises(TypeError){ @hash1.merge(1) }
      assert_raises(TypeError){ @hash1.merge([]) }
   end

   def teardown
      @hash1 = nil
      @hash2 = nil
   end
end
