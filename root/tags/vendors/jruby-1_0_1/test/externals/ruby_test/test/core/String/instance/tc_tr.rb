#####################################################################
# tc_tr.rb
#
# Test case for the String#tr and String#tr! instance methods.
#####################################################################
require 'test/unit'

class TC_String_Tr_Instance < Test::Unit::TestCase
   def setup
      @string1 = "hello"                    # Simple
      @string2 = "C:\\Program Files\\Test"  # Backslashes
      @string3 = "\221\222\223\224\225" # Accented characters
   end

   def test_tr_basic
      assert_respond_to(@string1, :tr)
      assert_nothing_raised{ @string1.tr('h', '*') }
      assert_kind_of(String, @string1.tr('h', '*'))
   end

   def test_tr_bang_basic
      assert_respond_to(@string1, :tr!)
      assert_nothing_raised{ @string1.tr!('h', '*') }
      assert_equal(nil, @string1.tr!('h', '*'))
   end

   def test_tr_single_character
      assert_equal('h*ll*', @string1.tr('aeiou', '*'))
      assert_equal('C:/Program Files/Test', @string2.tr("\\", '/'))
      assert_equal("\220\222\223\224\225", @string3.tr("\221", "\220"))
   end

   def test_tr_multiple_characters
      assert_equal('hippo', @string1.tr('el', 'ip'))
      assert_equal("C:\\Program Filsh\\Tsht", @string2.tr('es', 'sh'))
      assert_equal("\226\227\223\224\225", @string3.tr("\221\222", "\226\227"))
   end

   def test_tr_negation
      assert_equal('*e**o', @string1.tr('^aeiou', '*'))
   end

   def test_tr_with_range
      assert_equal('ifmmp', @string1.tr('a-y', 'b-z'))
   end
   
   def test_tr_edge_cases
      assert_equal('helli', @string1.tr('o', 'icopter')) # To longer than From
      assert_equal('hexxo', @string1.tr('ll', 'x'))      # From longer than To
      assert_equal('hello', @string1.tr('x', 'y'))       # From not found
   end
   
   def test_tr_expected_failures
      assert_raises(TypeError){ @string1.tr('l', nil) }
      assert_raises(ArgumentError){ @string1.tr('l') }
   end

   def teardown
      @string1 = nil
   end
end
