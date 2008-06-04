##############################################################################
# tc_pack.rb
#
# Test suite for the Array#pack instance method.  Note that there is some
# extra handling to deal with big endian versus little endian architectures.
#
# TODO: This test case could use some more robust tests, especially for
# the "U" directive.
##############################################################################
require "test/unit"
require "test/helper"

class TC_Array_Pack_Instance < Test::Unit::TestCase
   include Test::Helper

   def setup
      @char_array  = %w/alpha beta gamma/
      @int_array   = [-1, 0, 1, 128]
      @float_array = [-1.5, 0.0, 1.5, 128.5]
      @bignum1      = 2**63
      @bignum2      = 2**64
   end

   # Helper method for dealing with endian issues.  The +data+ argument can
   # be an array or a string. The +n+ argument specifies on what byte boundary
   # the data should be reversed. The default is 4.
   #
   def endian(data, n=4)
      BIG_ENDIAN ? data.gsub(/.{#{n}}/){ |s| s.reverse } : data
   end

   def test_pack_basic
      assert_respond_to(@char_array, :pack)
      assert_nothing_raised{ @char_array.pack("a") }
   end

   def test_pack_at
      assert_equal("\000", @char_array.pack("@"))
      assert_equal("", @char_array.pack("@0"))
      assert_equal("\000", @char_array.pack("@1"))
      assert_equal("\000\000", @char_array.pack("@2"))
   end

   def test_pack_A
      assert_nothing_raised{ @char_array.pack("A") }
      assert_nothing_raised{ @char_array.pack("A" * @char_array.length) }
      assert_equal("a", @char_array.pack("A"))
      assert_equal("abg", @char_array.pack("AAA"))
      assert_equal("alpha", @char_array.pack("A5"))
      assert_equal("alpha  ", @char_array.pack("A7"))
      assert_equal("alpbetgam", @char_array.pack("A3a3A3"))
      assert_equal("alpha", @char_array.pack("A*"))
   end

   def test_pack_a
      assert_nothing_raised{ @char_array.pack("a") }
      assert_nothing_raised{ @char_array.pack("a" * @char_array.length) }
      assert_equal("a", @char_array.pack("a"))
      assert_equal("abg", @char_array.pack("aaa"))
      assert_equal("alpha", @char_array.pack("a5"))
      assert_equal("alpha\000\000", @char_array.pack("a7"))
      assert_equal("alpbetgam", @char_array.pack("a3a3a3"))
      assert_equal("alpha", @char_array.pack("a*"))
   end

   def test_pack_B
      assert_equal("\200", @char_array.pack("B"))
      assert_equal("\200\000", @char_array.pack("BB"))
      assert_equal("\200\000\200", @char_array.pack("BBB"))
      assert_equal("\210", @char_array.pack("B*"))

      assert_equal("\x61", ["01100001"].pack("B8"))
      assert_equal("\x61", ["01100001"].pack("B*"))
      assert_equal("\x61", ["0110000100110111"].pack("B8"))
      assert_equal("\x61\x37", ["0110000100110111"].pack("B16"))
      assert_equal("\x61\x37", ["01100001", "00110111"].pack("B8B8"))
      assert_equal("\x60", ["01100001"].pack("B4"))
      assert_equal("\x40", ["01100001"].pack("B2")) 
   end

   def test_pack_b
      assert_equal("\001", @char_array.pack("b"))
      assert_equal("\001\000", @char_array.pack("bb"))
      assert_equal("\001\000\001", @char_array.pack("bbb"))
      assert_equal("\021", @char_array.pack("b*"))

      assert_equal "\x86",     ["01100001"].pack("b8")
      assert_equal "\x86",     ["01100001"].pack("b*")
      assert_equal "\x86",     ["0110000100110111"].pack("b8")
      assert_equal "\x86\xec", ["0110000100110111"].pack("b16")
      assert_equal "\x86\xec", ["01100001", "00110111"].pack("b8b8")
      assert_equal "\x06",     ["01100001"].pack("b4")
      assert_equal "\x02",     ["01100001"].pack("b2")
   end

   def test_pack_C
      assert_equal "ABC",      [ 65, 66, 67 ].pack("C3")
      assert_equal "\377BC",   [ -1, 66, 67 ].pack("C*")
   end

   def test_pack_C_expected_errors
      assert_raises(TypeError){ ['test'].pack("C") }
   end

   def test_pack_c
     assert_equal "ABC",      [ 65, 66, 67 ].pack("c3")
     assert_equal "\377BC",   [ -1, 66, 67 ].pack("c*")
   end

   def test_pack_c_expected_errors
      assert_raises(TypeError){ ['test'].pack("c") }
   end

   def test_pack_D
      assert_equal endian("\000\000\000\000\000\000\000\000", 8), [0].pack('D') 
      assert_equal endian("\000\000\000\000\000\000\360?", 8),    [1].pack('D') 
      assert_equal endian("\000\000\000\000\000\000\360\277", 8), [-1].pack('D') 
      assert_equal endian("\000\000\000\000\000\000\340C", 8),    [@bignum1].pack('D')
   end

   # TODO: Should this be a TypeError?
   def test_pack_D_expected_errors
      assert_raises(ArgumentError){ ['test'].pack("D") }
   end

   def test_pack_d
      assert_equal endian("\000\000\000\000\000\000\000\000", 8), [0].pack('d')
      assert_equal endian("\000\000\000\000\000\000\360?", 8),    [1].pack('d')
      assert_equal endian("\000\000\000\000\000\000\360\277", 8), [-1].pack('d')
      assert_equal endian("\000\000\000\000\000\000\340C", 8),    [@bignum1].pack('d')
   end

   # TODO: Should this be a TypeError?
   def test_pack_d_expected_errors
      assert_raises(ArgumentError){ ['test'].pack("d") }
   end

   def test_pack_E
      assert_equal "\000\000\000\000\000\000\000\000", [0].pack('E')
      assert_equal "\000\000\000\000\000\000\360?",    [1].pack('E')
      assert_equal "\000\000\000\000\000\000\360\277", [-1].pack('E')
      assert_equal "\000\000\000\000\000\000\340C",    [@bignum1].pack('E')
   end

   def test_pack_e
      assert_equal "\000\000\000\000", [0].pack('e')
      assert_equal "\000\000\200?",    [1].pack('e')
      assert_equal "\000\000\200\277", [-1].pack('e')
      assert_equal "\000\000\000_",    [@bignum1].pack('e')
   end

   def test_pack_F
      assert_equal endian("\000\000\000\000"), [0].pack('F')
      assert_equal endian("\000\000\200?"),    [1].pack('F')
      assert_equal endian("\000\000\200\277"), [-1].pack('F')
      assert_equal endian("\000\000\000_"),    [@bignum1].pack('F')
   end

   def test_pack_f
      assert_equal endian("\000\000\000\000"), [0].pack('f')
      assert_equal endian("\000\000\200?"),    [1].pack('f')
      assert_equal endian("\000\000\200\277"), [-1].pack('f')
      assert_equal endian("\000\000\000_"),    [@bignum1].pack('f')
   end

   def test_pack_G
      assert_equal "\000\000\000\000\000\000\000\000", [0].pack('G')
      assert_equal "?\360\000\000\000\000\000\000",    [1].pack('G')
      assert_equal "\277\360\000\000\000\000\000\000", [-1].pack('G')
      assert_equal "C\340\000\000\000\000\000\000",    [@bignum1].pack('G')
   end

   def test_pack_g
      assert_equal "\000\000\000\000", [0].pack('g')
      assert_equal "?\200\000\000",    [1].pack('g')
      assert_equal "\277\200\000\000", [-1].pack('g')
      assert_equal "_\000\000\000",    [@bignum1].pack('g')
   end

   def test_pack_H
      assert_equal "\320", ["test"].pack("H")
      assert_equal "\240", @char_array.pack("H")
      assert_equal "\245\221\240", @char_array.pack("H*")
   end

   def test_pack_H_expected_errors
      assert_raises(TypeError){ [0].pack("H") }
   end

   def test_pack_h
      assert_equal "\r", ["test"].pack("h")
      assert_equal "\n", @char_array.pack("h")
      assert_equal "Z\031\n", @char_array.pack("h*")
   end

   def test_pack_h_expected_errors
      assert_raises(TypeError){ [0].pack("h") }
   end

   def test_pack_I
      assert_equal endian("\000\000\000\000"), [0].pack("I")
      assert_equal endian("\377\377\377\377"), @int_array.pack("I")
      assert_equal endian("\377\377\377\377"), @float_array.pack("I")
   end

   def test_pack_i
      assert_equal endian("\000\000\000\000"), [0].pack("i")
      assert_equal endian("\377\377\377\377"), @int_array.pack("i")
      assert_equal endian("\377\377\377\377"), @float_array.pack("i")
   end

   def test_pack_I_expected_errors
      assert_raises(TypeError){ @char_array.pack("I") }
      assert_raises(RangeError){ [@bignum2].pack("I") }
   end

   def test_pack_L
      assert_equal endian("\000\000\000\000"), [0].pack("L")
      assert_equal endian("\377\377\377\377"), @int_array.pack("L")
      assert_equal endian("\377\377\377\377\000\000\000\000"), @int_array.pack("L2")
      assert_equal endian("\377\377\377\377"), @float_array.pack("L")
      assert_equal endian("\377\377\377\377\000\000\000\000"), @float_array.pack("L2")
   end

   def test_pack_l
      assert_equal endian("\000\000\000\000"), [0].pack("l")
      assert_equal endian("\377\377\377\377"), @int_array.pack("l")
      assert_equal endian("\377\377\377\377\000\000\000\000"), @int_array.pack("l2")
      assert_equal endian("\377\377\377\377"), @float_array.pack("l")
      assert_equal endian("\377\377\377\377\000\000\000\000"), @float_array.pack("l2")
   end

   def test_pack_L_expected_errors
      assert_raises(TypeError){ @char_array.pack("L") }
      assert_raises(RangeError){ [@bignum2].pack("L") }
   end

   # TODO: Need some better tests here
   def test_pack_M
      assert_equal("alpha=\n", @char_array.pack("M"))
      assert_equal("1=\n", [1].pack("M"))
      assert_equal("-1=\n", @int_array.pack("M"))
      assert_equal("-1.5=\n", @float_array.pack("M"))
      assert_equal("9223372036854775808=\n", [@bignum1].pack("M"))
      assert_equal("abc=02def=\ncat=\n=01=\n", ["abc\002def", "cat", "\001"].pack("M9M3M4"))
   end

   def test_pack_m
      assert_equal("YWxwaGE=\n", @char_array.pack("m"))
   end

   def test_pack_m_expected_errors
      assert_raises(TypeError){ @int_array.pack("m") }
      assert_raises(TypeError){ @float_array.pack("m") }
      assert_raises(TypeError){ [@bignum1].pack("m") }
   end

   def test_pack_N
      assert_equal endian("\000\000\000\000"), [0].pack("N")
      assert_equal endian("\377\377\377\377"), @int_array.pack("N")
      assert_equal endian("\377\377\377\377"), @float_array.pack("N")
   end

   def test_pack_n
      assert_equal endian("\000\000"), [0].pack("n")
      assert_equal endian("\377\377"), @int_array.pack("n")
      assert_equal endian("\377\377"), @float_array.pack("n")
   end

   def test_pack_N_expected_errors
      assert_raises(TypeError){ @char_array.pack("N") }
   end

   unless JRUBY
      # The exact return value for 'P' is unpredictable, so we do some
      # general assertions instead.
      def test_pack_P
         assert_nothing_raised{ @char_array.pack("P") }
         assert_nothing_raised{ @char_array.pack("P*") }
         assert_kind_of(String, @char_array.pack("P"))
         assert_kind_of(String, @char_array.pack("P*"))
      end

      def test_pack_P_expected_errors
         assert_raises(ArgumentError){ @char_array.pack("P10000") }
      end

      # The exact return value for 'p' is unpredictable, so we do some
      # general assertions instead.
      def test_pack_p
         assert_nothing_raised{ @char_array.pack("p") }
         assert_nothing_raised{ @char_array.pack("p*") }
         assert_kind_of(String, @char_array.pack("p"))
         assert_kind_of(String, @char_array.pack("p*"))
      end

      def test_pack_p_expected_errors
         assert_raises(ArgumentError){ @char_array.pack("p10000") }
      end
   end

   def test_pack_q
      assert_equal(endian("\000\000\000\000\000\000\000\000", 8), [0].pack("q"))
      assert_equal(endian("\001\000\000\000\000\000\000\000", 8), [1].pack("q"))
      assert_equal(endian("\377\377\377\377\377\377\377\377", 8), [-1].pack("q"))
      assert_equal(endian("\377\377\377\377\377\377\377\377", 8), @int_array.pack("q"))
      assert_equal(endian("\377\377\377\377\377\377\377\377", 8), @float_array.pack("q"))
      assert_equal(endian("\000\000\000\000\000\000\000\200", 8), [@bignum1].pack("q"))
   end

   def test_pack_q_expected_errors
      assert_raises(TypeError){ @char_array.pack("q") }
      assert_raises(RangeError){ [(2**128)].pack("q") }
   end

   def test_pack_Q
      assert_equal(endian("\000\000\000\000\000\000\000\000", 8), [0].pack("Q"))
      assert_equal(endian("\001\000\000\000\000\000\000\000", 8), [1].pack("Q"))
      assert_equal(endian("\377\377\377\377\377\377\377\377", 8), [-1].pack("Q"))
      assert_equal(endian("\377\377\377\377\377\377\377\377", 8), @int_array.pack("Q"))
      assert_equal(endian("\377\377\377\377\377\377\377\377", 8), @float_array.pack("Q"))
      assert_equal(endian("\000\000\000\000\000\000\000\200", 8), [@bignum1].pack("Q"))
   end

   def test_pack_Q_expected_errors
      assert_raises(TypeError){ @char_array.pack("Q") }
      assert_raises(RangeError){ [(2**128)].pack("Q") }
   end

   def test_pack_s
      assert_equal(endian("\000\000", 2), [0].pack("s"))
      assert_equal(endian("\001\000", 2), [1].pack("s"))
      assert_equal(endian("\377\377", 2), [-1].pack("s"))
      assert_equal(endian("c\000", 2), [99].pack("s"))
      assert_equal(endian("\377\377", 2), @int_array.pack("s"))
      assert_equal(endian("\377\377\000\000\001\000\200\000", 2), @int_array.pack("s*"))
      assert_equal(endian("\377\377\000\000", 2), @int_array.pack("s2"))
      assert_equal(endian("\377\377", 2), @float_array.pack("s"))
      assert_equal(endian("\377\377\000\000\001\000\200\000", 2), @float_array.pack("s*"))
      assert_equal(endian("\377\377\000\000", 2), @float_array.pack("s2"))
   end

   def test_pack_s_expected_errors
      assert_raises(TypeError){ @char_array.pack("s") }
      assert_raises(RangeError){ [@bignum2].pack("s") }
      assert_raises(ArgumentError){ @int_array.pack("s100") }
   end

   # TODO: find a test that produces different results from 's'
   def test_pack_S
      assert_equal(endian("\000\000", 2), [0].pack("s"))
      assert_equal(endian("\001\000", 2), [1].pack("s"))
      assert_equal(endian("\377\377", 2), [-1].pack("s"))
      assert_equal(endian("c\000", 2), [99].pack("s"))
      assert_equal(endian("\377\377", 2), @int_array.pack("s"))
      assert_equal(endian("\377\377\000\000\001\000\200\000", 2), @int_array.pack("s*"))
      assert_equal(endian("\377\377\000\000", 2), @int_array.pack("s2"))
      assert_equal(endian("\377\377", 2), @float_array.pack("s"))
      assert_equal(endian("\377\377\000\000\001\000\200\000", 2), @float_array.pack("s*"))
      assert_equal(endian("\377\377\000\000", 2), @float_array.pack("s2"))
   end

   def test_pack_S_expected_errors
      assert_raises(TypeError){ @char_array.pack("S") }
      assert_raises(RangeError){ [@bignum2].pack("S") }
      assert_raises(ArgumentError){ @int_array.pack("S100") }
   end

   def test_pack_U
      assert_equal("\000", [0].pack("U"))
      assert_equal("d", [100, 200, 300].pack("U"))
      assert_equal("d\303\210\304\254", [100, 200, 300].pack("U*"))
   end

   def test_pack_U_expected_errors
      assert_raises(RangeError){ [-1].pack("U") }
      assert_raises(TypeError){ ['a'].pack("U") }
   end

   def test_pack_V
      assert_equal("\000\000\000\000", [0].pack("V"))
      assert_equal("\001\000\000\000", [1].pack("V"))
      assert_equal("\377\377\377\377", @int_array.pack("V"))
      assert_equal("\377\377\377\377\000\000\000\000", @int_array.pack("V2"))
      assert_equal("\377\377\377\377", @float_array.pack("V"))
      assert_equal("\377\377\377\377\000\000\000\000", @float_array.pack("V2"))
   end

   def test_pack_V_expected_errors
      assert_raises(TypeError){ @char_array.pack('V') }
      assert_raises(RangeError){ [@bignum2].pack('V') }
      assert_raises(ArgumentError){ @int_array.pack('V100') }
   end

   def test_pack_v
      assert_equal("\000\000", [0].pack("v"))
      assert_equal("\001\000", [1].pack("v"))
      assert_equal("\377\377", [-1].pack("v"))
      assert_equal("c\000", [99].pack("v"))
      assert_equal("\377\377", @int_array.pack("v"))
      assert_equal("\377\377\000\000\001\000\200\000", @int_array.pack("v*"))
      assert_equal("\377\377\000\000", @int_array.pack("v2"))
      assert_equal("\377\377", @float_array.pack("v"))
      assert_equal("\377\377\000\000\001\000\200\000", @float_array.pack("v*"))
      assert_equal("\377\377\000\000", @float_array.pack("v2"))
   end

   def test_pack_v_expected_errors
      assert_raises(TypeError){ @char_array.pack('v') }
      assert_raises(RangeError){ [@bignum2].pack('v') }
      assert_raises(ArgumentError){ @int_array.pack('v100') }
   end

   def test_pack_w
      assert_equal("\000", [0].pack("w"))
      assert_equal("d", [100].pack("w"))
      assert_equal("\202\200\200\200\200\200\200\200\200\000", [@bignum2].pack("w"))
      assert_equal("\217\377\377\377\177", [0xffffffff].pack("w"))
   end

   def test_pack_w_expected_errors
      assert_raises(ArgumentError){ [-1].pack("w") }
      assert_raises(TypeError){ [nil].pack("w") }
      assert_raises(TypeError){ ['a'].pack("w") }
   end

   def teardown
      @char_array  = nil
      @int_array   = nil
      @float_array = nil
      @bignum1     = nil
      @bignum2     = nil
   end
end
