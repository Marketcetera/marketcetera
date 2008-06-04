require 'test/minirunit'
test_check "Test hash:"

h = {1=>2,3=>4,5=>6}
h.replace({1=>100})
test_equal({1=>100}, h)

h = {1=>2,3=>4}
h2 = {3=>300, 4=>400}
h.update(h2)
test_equal(2, h[1])
test_equal(300, h[3])
test_equal(400, h[4])

num = 0
h1 = { "a" => 100, "b" => 200 }
h2 = { "b" => 254, "c" => 300 }
h1.merge!(h2) {|k,o,n| num += 1; o+n }
test_equal(h1,{"a"=>100, "b"=>454, "c"=>300})
test_equal(num, 1)

h = {1=>2,3=>4}
test_exception(IndexError) { h.fetch(10) }
test_equal(2, h.fetch(1))
test_equal("hello", h.fetch(10, "hello"))
test_equal("hello 10", h.fetch(10) { |e| "hello #{e}" })

h = {}
k1 = [1]
h[k1] = 1
k1[0] = 100
test_equal(nil, h[k1])
h.rehash
test_equal(1, h[k1])

h = {1=>2,3=>4,5=>6}
test_equal([2, 6], h.values_at(1, 5))

h = {1=>2,3=>4}
test_equal(1, h.index(2))
test_equal(nil, h.index(10))
test_equal(nil, h.default_proc)
h.default = :hello
test_equal(nil, h.default_proc)
test_equal(1, h.index(2))
test_equal(nil, h.index(10))

h = Hash.new {|h,k| h[k] = k.to_i*10 }

test_ok(!nil, h.default_proc)
test_equal(100, h[10])
test_equal(20, h.default(2))

#behavior change in 1.8.5 led to this:
test_equal(nil, h.default)

h.default = 5
test_equal(5,h.default)
test_equal(nil, h.default_proc)

test_equal(5, h[12])

class << h
 def default(k); 2; end
end

test_equal(nil, h.default_proc)
test_equal(2, h[30])

# test that extensions of the base classes are typed correctly
class HashExt < Hash
end
test_equal(HashExt, HashExt.new.class)
test_equal(HashExt, HashExt[:foo => :bar].class)

# make sure hash yields look as expected (copied from MRI iterator test)

class H
  def each
    yield [:key, :value]
  end
end

[{:key=>:value}, H.new].each {|h|
  h.each{|a| test_equal([:key, :value], a)}
  h.each{|*a| test_equal([[:key, :value]], a)}
  h.each{|k,v| test_equal([:key, :value], [k,v])}
}

# each_pair should splat args correctly
{:a=>:b}.each_pair do |*x|
        test_equal(:a,x[0])
        test_equal(:b,x[1])
end

# Test hash coercion
class MyHash
  def initialize(hash)
    @hash = hash
  end
  def to_hash
    @hash
  end
end

class SubHash < Hash
end

x = {:a => 1, :b => 2}
x.update(MyHash.new({:a => 10, :b => 20}))
test_equal(10, x[:a])
test_equal(20, x[:b])
test_exception(TypeError) { x.update(MyHash.new(4)) }

x = {:a => 1, :b => 2}
sub2 = SubHash.new()
sub2[:a] = 10
sub2[:b] = 20
x.update(MyHash.new(sub2))
test_equal(10, x[:a])
test_equal(20, x[:b])

x = {:a => 1, :b => 2}
x.replace(MyHash.new({:a => 10, :b => 20}))
test_equal(10, x[:a])
test_equal(20, x[:b])
test_exception(TypeError) { x.replace(MyHash.new(4)) }

x = {:a => 1, :b => 2}
x.replace(MyHash.new(sub2))
test_equal(10, x[:a])
test_equal(20, x[:b])

class H1 < Hash
end

test_no_exception{ H1.new.clone }
