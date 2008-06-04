#######################################################################
# tc_chomp.rb
#
# Test case for the String#chomp and String#chomp! instance methods.
#######################################################################
require "test/unit"

class TC_String_Chomp_Instance < Test::Unit::TestCase
   def setup
      @str1 = "hello"
      @str2 = "hello\n"
      @str3 = "hello\r"
      @str4 = "hello\r\n"
      @str5 = "hello\n\n"
      @rs = $/.dup
   end

   def test_chomp_basic
      assert_respond_to(@str1, :chomp)
      assert_respond_to(@str1, :chomp!)
      assert_nothing_raised{ @str1.chomp }
      assert_nothing_raised{ @str1.chomp! }
      assert_nothing_raised{ @str1.chomp("llo") }
      assert_nothing_raised{ @str1.chomp!("ll0") }
   end

   def test_chomp
      assert_equal("hello", @str1.chomp)
      assert_equal("hello", @str2.chomp)
      assert_equal("hello", @str3.chomp)
      assert_equal("hello", @str4.chomp)
      assert_equal("hello\n", @str5.chomp)
   end

   def test_chomp_bang
      assert_equal(nil, @str1.chomp!)
      assert_equal("hello", @str2.chomp!)
      assert_equal("hello", @str3.chomp!)
      assert_equal("hello", @str4.chomp!)
      assert_equal("hello\n", @str5.chomp!)
   end

   def test_chomp_with_arg
      assert_equal("he", @str1.chomp("llo"))
      assert_equal("hello", @str1.chomp("z"))
      assert_equal("he", @str2.chomp("llo\n"))
      assert_equal("hello\n", @str2.chomp("llo"))
   end

   def test_chomp_bang_with_arg
      assert_equal("he", @str1.chomp!("llo"))
      assert_equal(nil, @str1.chomp!("z"))
      assert_equal("he", @str2.chomp!("llo\n"))
      assert_equal(nil, @str2.chomp!("llo"))
   end

   def test_chomp_rs_altered
      $/ = "llo"
      assert_equal("he", @str1.chomp)

      $/ = nil
      assert_equal("hello", @str1.chomp)

      $/ = "\n"
      assert_equal("hello", @str4.chomp)

      $/ = ""
      assert_equal("hello", @str5.chomp)
   end

   def test_chomp_bang_rs_altered
      $/ = "llo"
      assert_equal("he", @str1.chomp!)

      $/ = nil
      assert_equal(nil, @str1.chomp!)

      $/ = "\n"
      assert_equal("hello", @str4.chomp!)

      $/ = ""
      assert_equal("hello", @str5.chomp!)
   end

   def test_chomp_expected_errors
      assert_raises(ArgumentError){ @str1.chomp("a","b") }
      assert_raises(TypeError){ @str1.chomp(1) }
   end

   def teardown
      @str1 = nil
      @str2 = nil
      @str3 = nil
      @str4 = nil
      @str5 = nil
      $/ = @rs
   end
end
