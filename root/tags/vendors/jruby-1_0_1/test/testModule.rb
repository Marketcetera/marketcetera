require 'test/minirunit'
test_check "module"

# MRI 1.7-style self-replacement for define_method's blocks

class TestModule_Foo
  define_method(:foo) { self }
end
# MRI 1.6 returns Class, 1.7 returns Foo.
#test_equal(Class, TestModule_Foo.new.foo.class)
#test_equal(TestModule_Foo, TestModule_Foo.new.foo.class)

test_equal("TestModule_Foo", TestModule_Foo.new.foo.class.name)

testmodule_local_variable = 123

TestModule_Foo.module_eval {||
  def abc(x)
    2 * x
  end
  XYZ = 10
  ABC = self
  LOCAL1 = testmodule_local_variable
}
test_ok(! defined? abc)
test_equal(4, TestModule_Foo.new.abc(2))
test_equal(10, XYZ)
test_equal(TestModule_Foo, ABC)
test_equal(testmodule_local_variable, LOCAL1)

class TestModule2
end
TestModule2.module_eval("def abc(x); 3 * x; end; XYZ = 12; ABC = self; LOCAL2 = testmodule_local_variable")
test_equal(6, TestModule2.new.abc(2))
test_equal(12, TestModule2::XYZ)
test_equal(TestModule2, TestModule2::ABC)
test_equal(testmodule_local_variable, TestModule2::LOCAL2)

module A
  module B
    module C
      test_equal([A::B::C, A::B, A], Module.nesting)
      $nest = Module.nesting
    end
    module D
      test_equal([A::B::D, A::B, A], Module.nesting)
    end
    test_equal([A::B, A], Module.nesting)
  end
end
test_equal([], Module.nesting)
test_equal([A::B::C, A::B, A], $nest)

OUTER_CONSTANT = 4711

module TestModule_A
  A_CONSTANT = 123
  class TestModule_B
    attr_reader :a
    attr_reader :b
    def initialize
      @a = A_CONSTANT
      @b = OUTER_CONSTANT
    end
  end
end
test_equal(123, TestModule_A::TestModule_B.new.a)
test_equal(4711, TestModule_A::TestModule_B.new.b)

class TestModule_C_1 < Array
  def a_defined_method
    :ok
  end
end
class TestModule_C_1
end
test_equal(:ok, TestModule_C_1.new.a_defined_method)


#test_exception(TypeError) {
#  module Object; end
#}
test_exception(TypeError) {
  class Kernel; end
}


################# test external reference to constant from included module
module M1
  CONST = 7
end
class C1
  include M1
  x = CONST
end

test_equal(7, C1::CONST)

################ test define_method

class C2
  define_method( 'methodName', proc { 1 })
  e = test_exception(TypeError) {
    define_method( 'methodNameX', 'badParameter')
  }
  test_equal('wrong argument type String (expected Proc/Method)', e.message)
end
class C3 < C2
  define_method( 'methodName2', instance_method(:methodName))
end

############### test caching system when including a module

class D1
  def foo; "foo"; end
end

class D2 < D1
  def bar; foo; end
end

class D3 < D2
  def bar; foo; end
end

# Call methods once to force D1.foo to cache
b = D2.new
b.bar
c = D3.new
c.bar

module Foo
  def foo; "fooFoo"; end
end

class D2
  include Foo
end

test_equal("fooFoo", b.bar)
test_equal("fooFoo", c.bar)

###### included
$included = false
module I1
  def I1.included(m)
    test_equal(I2, m)
    $included = true
  end
end

class I2
  include I1
end

test_ok($included)

############### test 'super' within a module method
module A3
  module B3
    def self.extend_object(obj)
      super
    end
  end
end

x = []
x.extend(A::B)

test_ok(x.kind_of?(A::B))

############## test multiple layers of includes
module ModA
 def methodA; true; end
end

module ModB
 include ModA
 def methodB; methodA; end
end

module ModC
 include ModB
 def methodC; methodB; end
end

class ModTest
 include ModC
 def test; methodC; end
end

test_ok(ModTest.new.test)

############# test same included modules from multiple parents
module ModHello
  def hello; "hello"; end
end
module IncludedFromMultipleParents
end

module ParentMod
  include ModHello
  include IncludedFromMultipleParents
end

class ParentClass
  include IncludedFromMultipleParents
end

class Victim < ParentClass
  include ParentMod
end

v = Victim.new
test_no_exception { v.hello }

###### instance_methods + undef_method

class InstanceMethodsUndefBase
  def foo; end
end

class InstanceMethodsUndefDerived < InstanceMethodsUndefBase
  test_equal(true, instance_methods.include?("foo"))
  undef_method "foo"
  test_equal(false, instance_methods.include?("foo"))
end

class InstanceMethodsUndefBase
  test_equal(true, instance_methods.include?("foo"))
end
  
###### attr_reader ######

class AttrReaderTest
  attr_reader :foo
  def initialize(a); @foo = a; end
end

a = AttrReaderTest.new(9)
test_equal(9, a.foo)
test_exception(ArgumentError) { a.foo 1 }
test_exception(ArgumentError) { a.foo 1, 2 }

##### test include order when specifying multiple modules ###
class Base
attr_reader :last_called
def initialize
super
end
end

module Mod1
def initialize
super
  @last_called = :Mod1
end
end

module Mod2
def initialize
super
  @last_called = :Mod2
end
end

class Child < Base
include Mod1, Mod2

def initialize
super
end
end

test_equal(:Mod1, Child.new.last_called)

##### JRUBY-104: test super called from within a module-defined initialize #####
module FooNew
def initialize(); @inits ||= []; @inits << FooNew; super(); end
end

class ClassB
def initialize(); @inits ||= []; @inits << ClassB; end
end

class ClassA < ClassB
include FooNew
def inits; @inits; end
end

test_equal([FooNew, ClassB], ClassA.new().inits)

module Foo
  Bar = Class.new
end

test_equal("Foo::Bar",Foo::Bar.name)

Fred = Module.new do
  def meth1
     "hello" 
  end
end

a = "my string"
a.extend(Fred)
test_equal("hello", a.meth1)

# Chain of includes deals with method cache flush
module MT_A
  def foo
  end
end
module MT_B
  include MT_A
  alias :foo_x :foo
end
class MT_C
  include MT_B
end

# Make sure that the self-object inside a block to new instance of Module evals correctly.

x = Module.new do
  def self.foo
    "1"
  end
end

test_ok x.methods.include?("foo")

# Make sure that the self object will fire correctly with super, when using define_method
Aaaa = Class.new(Dir) { 
  define_method(:initialize) do |*args| 
    super(*args) 
  end 
}

test_no_exception { Aaaa.new("/") }

class Froom < Module; end
test_equal Froom, Froom.new.class

# Dup/Clon'ing of modules
module M
    def self.initialize_copy original
       raise Exception.new
    end
    
    def self.meth;end
end

test_no_exception do
    M.dup
end

test_exception do
    M.clone
end


module M2
    def self.meth;end
end

test_no_exception do
    M2.clone.instance_eval{meth}
    M2.dup.instance_eval{meth}
end

test_ok(9.class.include?(Precision))
test_ok(9.class.include?(Kernel))
test_equal(false, Precision.include?(Precision))

class ModuleForTestingIfMethodsAreDefined
  def a_public_method; end
  protected
  def a_protected_method; end
  private
  def a_private_method; end
end
test_ok(ModuleForTestingIfMethodsAreDefined.method_defined?(:a_public_method))
test_ok(ModuleForTestingIfMethodsAreDefined.method_defined?(:a_protected_method))
test_ok(! ModuleForTestingIfMethodsAreDefined.method_defined?(:a_private_method))

test_ok(ModuleForTestingIfMethodsAreDefined.public_method_defined?(:a_public_method))
test_ok(! ModuleForTestingIfMethodsAreDefined.public_method_defined?(:a_protected_method))
test_ok(! ModuleForTestingIfMethodsAreDefined.public_method_defined?(:a_private_method))

test_ok(! ModuleForTestingIfMethodsAreDefined.protected_method_defined?(:a_public_method))
test_ok(ModuleForTestingIfMethodsAreDefined.protected_method_defined?(:a_protected_method))
test_ok(! ModuleForTestingIfMethodsAreDefined.protected_method_defined?(:a_private_method))

test_ok(! ModuleForTestingIfMethodsAreDefined.private_method_defined?(:a_public_method))
test_ok(! ModuleForTestingIfMethodsAreDefined.private_method_defined?(:a_protected_method))
test_ok(ModuleForTestingIfMethodsAreDefined.private_method_defined?(:a_private_method))