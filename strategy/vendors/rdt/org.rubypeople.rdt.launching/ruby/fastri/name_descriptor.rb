# Copyright (C) 2006  Mauricio Fernandez <mfp@acm.org>
#

module FastRI

# Alternative NameDescriptor implementation which doesn't require class/module
# names to be properly capitalized.
#
# Rules:
# * <tt>#foo</tt>: instance method +foo+
# * <tt>.foo</tt>: method +foo+ (either singleton or instance)
# * <tt>::foo</tt>: singleton method +foo+
# * <tt>foo::bar#bar<tt>: instance method +bar+ under <tt>foo::bar</tt>
# * <tt>foo::bar.bar<tt>: either singleton or instance method +bar+ under
#   <tt>foo::bar</tt>
# * <tt>foo::bar::Baz<tt>: module/class foo:bar::Baz
# * <tt>foo::bar::baz</tt>: singleton method +baz+ from <tt>foo::bar</tt>
# * other: raise RiError
class NameDescriptor
  attr_reader :class_names
  attr_reader :method_name

  # true and false have the obvious meaning. nil means we don't care
  attr_reader :is_class_method

  def initialize(arg)
    @class_names = []
    @method_name = nil
    @is_class_method = nil

    case arg
    when /((?:[^:]*::)*[^:]*)(#|::|\.)(.*)$/
      ns, sep, meth_or_class = $~.captures
      # optimization attempt: try to guess the real capitalization,
      # so we get a direct hit
      @class_names = ns.split(/::/).map{|x|  x[0,1] = x[0,1].upcase; x }
      if %w[# .].include? sep
        @method_name = meth_or_class
        @is_class_method = 
          case sep
          when "#";  false
          when ".";  nil
          end
      else
        if ("A".."Z").include? meth_or_class[0,1] # 1.9 compatibility
          @class_names << meth_or_class
        else
          @method_name     = meth_or_class
          @is_class_method = true
        end
      end
    when /^[^#:.]+/
      if ("A".."Z").include? arg[0,1]
        @class_names = [arg]
      else
        @method_name     = arg.dup
        @is_class_method = nil
      end
    else
      raise RiError, "Cannot create NameDescriptor from #{arg}"
    end
  end

  # Return the full class name (with '::' between the components)
  # or "" if there's no class name
  def full_class_name
    @class_names.join("::")
  end
end

end #module FastRI
