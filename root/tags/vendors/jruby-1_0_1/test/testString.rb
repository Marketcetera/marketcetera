require 'test/minirunit'
test_check "Test string:"

# Generic class to test integer functions.
class IntClass
  def initialize(num); @num = num; end
  def to_int; @num; end; 
end

##### misc #####

test_equal("hihihi", "hi" * 3)
test_equal(9, "alphabetagamma" =~ /gamma$/)
test_equal(nil, "alphabetagamma" =~ /GAMMA$/)
test_equal(false, "foo" == :foo)

test_equal(11, "\v"[0])
test_equal(27, "\e"[0])
# Test round trip of each ASCII character
0.upto(255) do |ch|
  test_equal(ch,eval(ch.chr.inspect)[0])
end

##### [] (aref) ######
s = "hello there"

test_equal(101, s[1])
test_equal("ell", s[1, 3])
test_equal("l", s[3, 1])
test_equal("ell", s[1..3])
test_equal("el", s[1...3])
test_equal("er", s[-3, 2])
test_equal("her", s[-4..-2])
test_equal("", s[-2..-4])
test_equal("", s[6..2])
t = ""
test_equal(nil, t[6..2])
test_equal(nil, t[-2..-4])
test_equal("ell", s[/[aeiow](.)\1/])
test_equal("ell", s[/[aeiow](.)\1/, 0])
test_equal("l", s[/[aeiow](.)\1/, 1])
test_equal(nil, s[/[aeiow](.)\1/, 2])
# negative subscripts exercising rubicon test case
test_equal("o", s[/[aeiow](.)\1(.)/, -1])
test_equal("l", s[/[aeiow](.)\1(.)/, -2])
# zero subscript should capture whole matched pattern
test_equal("ello", s[/[aeiow](.)\1(.)/, 0])
test_equal("the", s[/(..)e/])
test_equal("th", s[/(..)e/, 1])
test_equal("lo", s["lo"])
test_equal(nil, s["bye"])

##### []= (aset) #######

s = "foobar"
s["foo"] = "baz"
test_equal("bazbar", s)
s[2] = 79
test_equal("baObar", s)

# ""[0,0]="foo" is valid
s = ""
s[0,0]="foo"

test_equal("foo", s)

# regexp, integer asets from rubicon

s="BarFoo"
test_equal("Foo", s[/([A-Z]..)([A-Z]..)/, 1] = "Foo")
test_equal("FooFoo", s)
test_equal("Bar", s[/([A-Z]..)([A-Z]..)/, 2] = "Bar")
test_equal("FooBar", s)
test_exception(IndexError) { s[/([A-Z]..)([A-Z]..)/, 3] = "None" }
test_equal("FooBar", s)
test_equal("Foo", s[/([A-Z]..)([A-Z]..)/, -1] = "Foo")
test_equal("FooFoo", s)
test_equal("Bar", s[/([A-Z]..)([A-Z]..)/, -2] = "Bar")
test_equal("BarFoo", s)
test_exception(IndexError) { s[/([A-Z]..)([A-Z]..)/, -3] = "None" }

##### capitalize/capitalize! ######

test_equal("Hello", "hello".capitalize)
test_equal("123abc", "123ABC".capitalize)

s ="hello"
s.capitalize!
test_equal("Hello", s)
test_equal(101, s[IntClass.new(1)])
test_equal(nil, s.capitalize!)
s = "123ABC"
s.capitalize!
test_equal("123abc", s)
test_equal(nil, s.capitalize!)

##### center ######

test_equal("hello", "hello".center(4))
test_equal("       hello        ", "hello".center(20))
test_equal("hello", "hello".center(4, "_-^-"))
test_equal("_-^-_-^helloe_-^-_-^", "helloe".center(20, "_-^-"))
test_equal("-------hello--------", "hello".center(20, "-"))
test_exception(ArgumentError) { "hello".center(11, "") }

##### chomp ######

# See test/testStringChomp.rb

##### chop/chop! ######

test_equal("", "".chop)
test_equal(nil, "".chop!)
test_equal("", "\n".chop)
s = "\n"
test_equal("", s.chop!)
test_equal("", s)

test_equal("string", "string\r\n".chop)
test_equal("string\n", "string\n\r".chop)
test_equal("string", "string\n".chop)
test_equal("strin", "string".chop)
test_equal("", "x".chop.chop)


##### <=> (cmp) #####

test_equal(-1, 'A' <=> 'B')
test_equal(0, 'A' <=> 'A')
test_equal(1, 'B' <=> 'A')
test_equal(nil, 'A' <=> 3)
test_equal(nil, 'A' <=> 3.to_f)


##### <</concat ######
s = "a"
test_equal("aa", s << "a")
test_equal("aaa", s.concat("a"))
test_equal("aaaa", s << 97)
test_equal("aaaaa", s.concat(97))
test_exception(TypeError) { s << 300 }
test_exception(TypeError) { s.concat(300) }

##### downcase/downcase! ######

test_equal("hello", "HELlo".downcase)
s = "HELlo"
test_equal("hello", s.downcase!)
test_equal(nil, s.downcase!)

##### each_byte #####

"\x80".each_byte {|c| test_equal(128, c) }

##### gsub #####
test_equal("h*ll*", "hello".gsub(/[aeiou]/, '*'))
test_equal("h<e>ll<o>", "hello".gsub(/([aeiou])/, '<\1>'))
test_equal("104 101 108 108 111 ", "hello".gsub(/./) {|s| s[0].to_s + ' '})
test_equal("a-b-c", "a+b+c".gsub("+", "-"))
test_equal("", "".gsub(/\r\n|\n/, "\n"))

##### index/rindex ######
testcase='toto'
test_ok(1 == idx = testcase.index('o'))
test_ok(3 == testcase.index('o',idx.succ))
test_ok(nil == "hello".index('', 100))
test_ok(3 == idx = testcase.rindex('o'))
test_ok(1 == testcase.rindex('o', idx-1))
test_ok(0 == testcase.rindex('', 0))

##### insert #####

s = "abcd"
test_equal("Xabcd", s.insert(0, 'X'))
test_equal("Xabcd", s)
test_equal("abcXd", "abcd".insert(3, 'X'))
test_equal("abcdX", "abcd".insert(4, 'X'))
test_equal("abXcd", "abcd".insert(-3, 'X'))
test_equal("abcdX", "abcd".insert(-1, 'X'))

test_exception(IndexError) { "".insert(-100, 'X') }
test_exception(IndexError) { "".insert(100, 'X') }
test_exception(TypeError) { "abcd".insert(1, nil) }


##### intern #####
for method in [:intern, :to_sym] do
    test_equal(:koala, "koala".send(method))
    test_ok(:koala != "Koala".send(method))
   
    for str in ["identifier", "with spaces", "9with_digits", "9and spaces"]
      sym = str.send(method)
      test_equal(Symbol, sym.class)
      test_equal(str, sym.to_s)
    end
   
    test_exception(ArgumentError) { "".send(method) }
    test_exception(ArgumentError) { "with\0null\0inside".send(method) }
end


##### ljust,rjust #####

test_equal("hello", "hello".ljust(4))
test_equal("hello      ", "hello".ljust(11))

# with explicit padding
test_equal("hi111111111", "hi".ljust(11, "1"))
test_equal("hi121212121", "hi".ljust(11, "12"))
test_equal("hi123412341", "hi".ljust(11, "1234"))

# zero width padding
test_exception(ArgumentError)  { "hello".ljust(11, "") }

# using a substring
test_equal("ho  ", "hiho"[2..3].ljust(4))

test_equal("hello", "hello".rjust(4))
test_equal("      hello", "hello".rjust(11))

# with explicit padding
test_equal("111111111hi", "hi".rjust(11, "1"))
test_equal("121212121hi", "hi".rjust(11, "12"))
test_equal("123412341hi", "hi".rjust(11, "1234"))

# zero width padding
test_exception(ArgumentError)  { "hi".rjust(11, "") }

##### oct #####
# oct should return zero in appropriate cases
test_equal(0, "b".oct)
test_equal(0, "".oct)

##### replace #####
t = "hello"
s = "world"
s.replace t
test_equal("hello", s)
s.chop!
test_equal("hello", t)

##### reverse/reverse! #####
s = "abc"
test_equal("cba", s.reverse)
test_equal("abc", s)
test_equal("cba", s.reverse!)
test_equal("cba", s)

##### rjust (see ljust) #####

##### scan

s = "cruel world"
test_equal(["cruel", "world"], s.scan(/\w+/))
test_equal(["cru", "el ", "wor"], s.scan(/.../))
test_equal([["cru"], ["el "], ["wor"]], s.scan(/(...)/))
test_equal([["cr", "ue"], ["l ", "wo"]], s.scan(/(..)(..)/))

l = []
s.scan(/\w+/) { |w| l << "<<#{w}>>" }
test_equal(["<<cruel>>", "<<world>>"], l)
l = ""
s.scan(/(.)(.)/) { |a,b|  l << b; l << a }
test_equal("rceu lowlr", l)

##### slice! ######

o = "FooBar"

s = o.dup
test_equal(?F, s.slice!(0))
test_equal("ooBar", s)
test_equal("FooBar", o)
s = o.dup
test_equal(?r, s.slice!(-1))
test_equal("FooBa", s)

s = o.dup
test_equal(nil, s.slice!(6))
test_equal("FooBar", s)
s = o.dup
test_equal(nil, s.slice!(-7))
test_equal("FooBar", s)

s = o.dup
test_equal("Foo", s.slice!(0,3))
test_equal("Bar", s)
s = o.dup
test_equal("Bar", s.slice!(-3,3))
test_equal("Foo", s)

s = o.dup
test_equal(nil, s.slice!(7,2))      # Maybe should be six?
test_equal("FooBar", s)
s = o.dup
test_equal(nil, s.slice!(-7,10))
test_equal("FooBar", s)

s = o.dup
test_equal("Foo", s.slice!(0..2))
test_equal("Bar", s)
s = o.dup
test_equal("Bar", s.slice!(-3..-1))
test_equal("Foo", s)

s = o.dup
test_equal("", s.slice!(6..2))
test_equal("FooBar", s)
s = o.dup
test_equal(nil, s.slice!(-10..-7))
test_equal("FooBar", s)

s = o.dup
test_equal("Foo", s.slice!(/^F../))
test_equal("Bar", s)
s = o.dup
test_equal("Bar", s.slice!(/..r$/))
test_equal("Foo", s)

s = o.dup
test_equal(nil, s.slice!(/xyzzy/))
test_equal("FooBar", s)

s = o.dup
test_equal("Foo", s.slice!("Foo"))
test_equal("Bar", s)
s = o.dup
test_equal("Bar", s.slice!("Bar"))
test_equal("Foo", s)

##### split ######

test_equal(["1", "2", "3"], "1x2x3".split('x'))
test_equal(["1", "2", "3"], "1   2     3".split(' '))
test_equal(["1", "2", "3"], "  1   2     3".split(' '))
# explicit Regex will not ignore leading whitespace
test_equal(["", "1", "2", "3"], "  1   2     3".split(/\s+/))
test_equal(["1", "2", "3"], "1 2 3".split())
test_equal(["1", "2", "3"], "1x2y3".split(/x|y/))
test_equal(["1", "x", "2", "y", "3"], "1x2y3".split(/(x|y)/))
test_equal(["1", "x", "a", "2", "x", "a", "3"], "1xta2xta3".split(/(x)t(.)/))
test_equal(["foo"], "foo".split("whatever", 1))
test_equal(["", "a", "b", "c"], "/a/b/c".split("/"))
test_equal(["a", "b", "c"], "abc".split(//))
test_equal(["/home", "/jruby"], "/home/jruby".split(%r<(?=/)>))
test_equal(["///home", "///jruby"], "///home///jruby".split(%r<(?=///)>))

##### sub #####

test_equal("h*llo", "hello".sub(/[aeiou]/, '*'))
test_equal("h<e>llo", "hello".sub(/([aeiou])/, '<\1>'))
test_equal("104 ello", "hello".sub(/./) {|s| s[0].to_s + ' ' })
special_chars = "{}(){}|*.\\?+^\$".split(//)
special_chars.each {|c| test_equal("H", c.sub(c, "H")) }
   
##### succ/succ! #####

def test_succ!(expected, start); start.succ!; test_equal(expected, start); end
test_equal("abce", "abcd".succ)
test_succ!("abce", "abcd")
test_equal("THX1139", "THX1138".succ)
test_succ!("THX1139", "THX1138")
test_equal("<<koalb>>", "<<koala>>".succ)
test_succ!("<<koalb>>", "<<koala>>")
test_equal("2000aaa", "1999zzz".succ)
test_succ!("2000aaa", "1999zzz")
test_equal("AAAA0000", "ZZZ9999".succ)
test_succ!("AAAA0000", "ZZZ9999")
test_equal("**+", "***".succ)
test_succ!("**+", "***")

##### sum #####

test_equal(2, "\001\001\000".sum)
test_equal(1408, "now is the time".sum)
test_equal(128, "now is the time".sum(8))
test_equal(128, "\x80".sum)
test_equal(414, "asdf".sum)
test_exception(ArgumentError) do
    "hello".sum(5, 6)
end
test_equal(414, "asdf".sum(-1))
test_equal(414, "asdf".sum(0))
test_equal(0, "asdf".sum(1))

def check_sum(str, bits=16)
  sum = 0
  str.each_byte {|c| sum += c}
  sum = sum & ((1 << bits) - 1) if bits != 0
  test_equal(sum, str.sum(bits))
end

0.upto(70) {|bits|
  check_sum("xyz", bits)
}


##### swapcase/swapcase! #####

s = "abC"
test_equal("ABc", s.swapcase)
test_equal("abC", s)
test_equal("ABc", s.swapcase!)
test_equal("ABc", s)
s = "111"
test_equal("111", s.swapcase)
test_equal(nil, s.swapcase!)

##### to_i #####

test_equal(12345, "12345".to_i)
test_equal(99, "99 red balloons".to_i)
test_equal(0, "0a".to_i)
test_equal(10, "0a".to_i(16))
test_equal(0, "0x10".to_i)
test_equal(16, "0x10".to_i(0))
test_equal(-16,"-0x10".to_i(0))
test_equal(0, "hello".to_i)
test_equal(14167554, "hello".to_i(30))
test_equal(101, "1100101".to_i(2))
test_equal(294977, "1100101".to_i(8))
test_equal(1100101, "1100101".to_i(10))
test_equal(17826049, "1100101".to_i(16))
test_equal(199066177, "1100101".to_i(24))


##### to_sym (see intern) #####

##### upcase/upcase! ######

test_equal("HELLO", "HELlo".upcase)
s = "HeLLo"
test_equal("HELLO", s.upcase!)
test_equal(nil, s.upcase!)

##### upto ######

UPTO_ANS = ["a8", "a9", "b0"]
s = "a8"
ans = []
s.upto("b0") { |e| ans << e }
test_equal(UPTO_ANS, ans)
test_equal("a8", s)

##### formatting with % and a string #####
test_equal(" 5", '%02s' % '5')
test_equal("05", '%02d' % '5')
test_equal("05", '%02g' % '5')
test_equal("05", '%02G' % '5')
test_equal("  ", '%2s' % nil)

# test that extensions of the base classes are typed correctly
class StringExt < String
  def [](arg)
    arg
  end
end
test_equal(StringExt, StringExt.new.class)
test_equal(StringExt, StringExt.new("test").class)

# test that methods are overridden correctly
test_equal(2, StringExt.new[2])

test_equal("foa3VCPbMb8XQ", "foobar".crypt("foo"))

test_exception(TypeError) { "this" =~ "that" }

# UTF behavior around inspect, to_s, and split
# NOTE: the "ffi" in the lines below is a single unicode character "ﬃ"; do not replace it with the normalized characters.

old_code = $KCODE
x = "eﬃcient"
test_equal("\"e\\357\\254\\203cient\"", x.inspect)
test_equal("eﬃcient", x.to_s)
test_equal(["e", "\357", "\254", "\203", "c", "i", "e", "n", "t"], x.split(//))

$KCODE = "UTF8"

test_equal("\"eﬃcient\"", x.inspect)
test_equal("eﬃcient", x.to_s)
test_equal(["e", "ﬃ", "c", "i", "e", "n", "t"], x.split(//))

# splitting by character should fall back on raw bytes when it's not valid unicode

x2 = "\270\236\b\210\245"

test_equal(["\270", "\236", "\b", "\210", "\245"], x2.split(//u))

$KCODE = old_code

# unpack("U*") should raise ArgumentError when the string is not valid UTF8
test_exception(ArgumentError) { x2.unpack("U*") }

# and just for kicks, make sure we're returning appropriate byte values for each_byte!

bytes = []
x2.each_byte { |b| bytes << b }
test_equal([184, 158, 8, 136, 165], bytes)

# JRUBY-280
test_equal("1234567890.51",("%01.2f" % 1234567890.506))

# test protocol conversion
class GooStr < String
end

f = GooStr.new("AAAA")
g= f.to_str
test_ok(f.object_id != g.object_id)
test_equal(String, g.class)
test_equal("AAAA", g)

class MyString
  include Comparable

  def to_str
    "foo"
  end

  def <=>(other)
    return "foo".<=>(other)
  end
end

# String#== should call other.==(str) when other respond_to "to_str"
test_equal("foo", MyString.new)

# ...but .eql? should still fail, since it only does a strict comparison between strings
test_ok(!"foo".eql?(MyString.new))

class FooStr < String
  # Should not get called
  def to_str
    123
  end
end

f = FooStr.new("AAAA")

test_equal("AAAA", [f].join(','))

# test coercion for multiple methods
class Foo
  def to_int
    3
  end
  def to_str
    "hello"
  end
end

class Five
  def to_str
    "5"
  end
end

class Unpack
  def to_str
    "A"
  end
end

test_equal("strstrstr", "str" * Foo.new)
test_equal("strhello", "str" + Foo.new)
test_equal("hello", "str".replace(Foo.new))
test_equal(0, "hello".casecmp(Foo.new))
test_equal("heXHujxX8gm/M", "str".crypt(Foo.new))
test_equal("shellor", "str".gsub("t", Foo.new))
test_equal("str", "shellor".gsub(Foo.new, "t"))
test_equal(108, "shellor"[Foo.new])
x = "sxxxxxr"
x[1, 5] = Foo.new
test_equal("shellor", x)
x = "str"
x[/t/, 0] = Foo.new
test_equal("shellor", x)
x = "str"
x[1] = Foo.new
test_equal("shellor", x)
x = "str"
x[/t/] = Foo.new
test_equal("shellor", x)
x = "str"
x["t"] = Foo.new
test_equal("shellor", x)
x = "shellor"
z = Foo.new
x[z] = "t"
test_equal("shetlor", x)
x = "str"
x[1..2] = Foo.new
test_equal("shello", x)
x = []
"1".upto(Five.new) {|y| x << y}
test_equal(["1", "2", "3", "4", "5"], x)
test_ok("shellor".include?(Foo.new))
test_equal(["s", "r"], "shellor".split(Foo.new))
test_equal(5, "shellor".count(Foo.new))
test_equal("sr", "shellor".delete(Foo.new))
test_equal("sr", "shellor".delete!(Foo.new))
test_equal("shelor", "shellor".squeeze(Foo.new))
test_equal("shelor", "shellor".squeeze!(Foo.new))
# JRUBY-734
test_equal("sgoddbr", "shellor".tr(Foo.new, "goodbye"))
test_equal("shlllooor", "sgoodbyer".tr("goodbye", Foo.new))
a = []
"shellor".each_line(Foo.new) { |x| a << x }
test_equal(["shello", "r"], a)
test_equal(["a"], s.unpack(Unpack.new))
test_equal(291, "123".to_i(IntClass.new(16)))
test_equal(345, "str".sum(IntClass.new(16)))

# JRUBY-816
test_exception(TypeError) { "s" << -1 }
test_exception(TypeError) { "s" << 256 }
test_equal("s\001", "s" << 1)
test_exception(NoMethodError) { +"s" }

