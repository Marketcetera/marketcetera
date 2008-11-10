class Object
  def singleton_class
    class << self; self; end
  end
end

class ParameterMetadata
  attr_reader :required, :name, :type, :completions
  
  def initialize(name)
    @name = name
    @required = true
  end
  
  def type(name)
    @type = name
  end
  
  def optional
    @required = false
  end
  
  def completions(array)
    @completions = array
  end
end

class MethodMetadata
  attr_reader :name, :parameters, :return_type
  def initialize(name)
    @name = name
    @parameters = []
  end
  
  def parameter(name)
    p = ParameterMetadata.new(name)
    p.instance_eval do
      yield self
    end
    @parameters << p
  end
  
  def return_type(name)
    @return_type = name
  end
end

class TypeMetadata  
  attr_reader :methods, :name
  
  def initialize(name)
    @name = name
    @methods = []
  end
  
  def method(method_name)
    m = MethodMetadata.new(method_name)
    m.instance_eval do
      yield self
    end
    @methods << m
  end
end

@types = []
def type(type_name)
  t = TypeMetadata.new(type_name)
  t.instance_eval do
    yield self
  end
  @types << t
end

def find_type(name)
  @types.each {|t| return t if t.name == name }
end

# define_variable "name", callback_code
type "ActionController::Base" do |t|
  t.method "blah" do |m|
    m.parameter "name" do |p|
      p.type ""
      p.optional
      p.completions ["nil", "true", "false"]
    end
  end
end

p @types.first
