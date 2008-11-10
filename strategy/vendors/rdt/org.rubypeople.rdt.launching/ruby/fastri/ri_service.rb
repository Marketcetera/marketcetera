# Copyright (C) 2006  Mauricio Fernandez <mfp@acm.org>
#
# Inspired by ri-emacs.rb by Kristof Bastiaensen <kristof@vleeuwen.org>
 
require 'rdoc/ri/ri_paths'
require 'rdoc/ri/ri_util'
require 'rdoc/ri/ri_formatter'
require 'rdoc/ri/ri_display'

require 'fastri/ri_index.rb'
require 'fastri/name_descriptor'


module FastRI

class ::DefaultDisplay
  def full_params(method)
    method.params.split(/\n/).each do |p|
      p.sub!(/^#{method.name}\(/o,'(')
      unless p =~ /\b\.\b/
        p = method.full_name + p
      end
      @formatter.wrap(p) 
      @formatter.break_to_newline
    end
  end
end

class StringRedirectedDisplay < ::DefaultDisplay
  attr_reader :stringio, :formatter
  def initialize(*args)
    super(*args)
    reset_stringio
  end

  def puts(*a)
    @stringio.puts(*a)
  end

  def print(*a)
    @stringio.print(*a)
  end

  def reset_stringio
    @stringio = StringIO.new("")
    @formatter.stringio = @stringio
  end
end

class ::RI::TextFormatter
  def puts(*a); @stringio.puts(*a) end
  def print(*a); @stringio.print(*a) end
end

module FormatterRedirection
  attr_accessor :stringio
  def initialize(*options)
    @stringio = StringIO.new("")
    super
  end
end

class RedirectedAnsiFormatter < RI::AnsiFormatter
  include FormatterRedirection
end

class RedirectedTextFormatter < RI::TextFormatter
  include FormatterRedirection
end

class RiService

  class MatchFinder
    def self.new
      ret = super
      yield ret if block_given?
      ret
    end

    def initialize
      @matchers = {}
    end

    def add_matcher(name, &block)
      @matchers[name] = block
    end

    def get_matches(methods)
      catch(:MatchFinder_return) do
        methods.each do |name|
          matcher = @matchers[name]
          matcher.call(self) if matcher
        end
        []
      end
    end

    def yield(matches)
      case matches
      when nil, []; nil
      when Array
        throw :MatchFinder_return, matches
      else
        throw :MatchFinder_return, [matches]
      end
    end
  end # MatchFinder


  Options = Struct.new(:formatter, :use_stdout, :width)

  def initialize(ri_reader)
    @ri_reader = ri_reader
  end

  DEFAULT_OBTAIN_ENTRIES_OPTIONS = {
    :lookup_order => [
              :exact, :exact_ci, :nested, :nested_ci, :partial, :partial_ci, 
              :nested_partial, :nested_partial_ci,
    ],
  }
  def obtain_entries(descriptor, options = {})
    options = DEFAULT_OBTAIN_ENTRIES_OPTIONS.merge(options)
    if descriptor.class_names.empty?
      seps = separators(descriptor.is_class_method)
      return obtain_unqualified_method_entries(descriptor.method_name, seps,
                                               options[:lookup_order])
    end

    # if we're here, some namespace was given
    full_ns_name = descriptor.class_names.join("::")
    if descriptor.method_name == nil
      return obtain_namespace_entries(full_ns_name, options[:lookup_order])
    else  # both namespace and method
      seps = separators(descriptor.is_class_method)
      return obtain_qualified_method_entries(full_ns_name, descriptor.method_name, 
                                             seps, options[:lookup_order])
    end
  end

  def completion_list(keyw)
    return @ri_reader.full_class_names if keyw == ""

    descriptor = NameDescriptor.new(keyw)
  
    if descriptor.class_names.empty?
      # try partial matches
      meths = @ri_reader.methods_under_matching("", /(#|\.)#{descriptor.method_name}/, true)
      ret = meths.map{|x| x.name}.uniq.sort
      return ret.empty? ? nil : ret
    end

    # if we're here, some namespace was given
    full_ns_name = descriptor.class_names.join("::")
    if descriptor.method_name == nil && ! [?#, ?:, ?.].include?(keyw[-1])
      # partial match
      namespaces = @ri_reader.namespaces_under_matching("", /^#{full_ns_name}/, false)
      ret = namespaces.map{|x| x.full_name}.uniq.sort
      return ret.empty? ? nil : ret
    else
      if [?#, ?:, ?.].include?(keyw[-1])
        seps = case keyw[-1]
          when ?#; %w[#]
          when ?:; %w[.]
          when ?.; %w[. #]
        end
      else  # both namespace and method
        seps = separators(descriptor.is_class_method)
      end
      sep_re = "(" + seps.map{|x| Regexp.escape(x)}.join("|") + ")"
      # partial
      methods = @ri_reader.methods_under_matching(full_ns_name, /#{sep_re}#{descriptor.method_name}/, false)
      ret = methods.map{|x| x.full_name}.uniq.sort
      return ret.empty? ? nil : ret
    end
  rescue RiError
    return nil
  end

  DEFAULT_INFO_OPTIONS = {
    :formatter => :ansi,
    :width     => 72,
    :extended  => false,
  }

  def matches(keyword, options = {})
    options = DEFAULT_INFO_OPTIONS.merge(options)
    return nil if keyword.strip.empty?
    descriptor = NameDescriptor.new(keyword)
    ret = obtain_entries(descriptor, options).map{|x| x.full_name}
    ret ? ret : nil
  rescue RiError
    return nil
  end

  def info(keyw, options = {})
    options = DEFAULT_INFO_OPTIONS.merge(options)
    return nil if keyw.strip.empty?
    descriptor = NameDescriptor.new(keyw)
    entries = obtain_entries(descriptor, options)

    case entries.size
    when 0; nil
    when 1
      case entries[0].type
      when :namespace
        capture_stdout(display(options)) do |display|
          display.display_class_info(@ri_reader.get_class(entries[0]), @ri_reader)
          if options[:extended]
            methods = @ri_reader.methods_under(entries[0], true)
            methods.each do |meth_entry|
              display.display_method_info(@ri_reader.get_method(meth_entry))
            end
          end
        end
      when :method
        capture_stdout(display(options)) do |display|
          display.display_method_info(@ri_reader.get_method(entries[0]))
        end
      end
    else
      capture_stdout(display(options)) do |display|
        formatter = display.formatter
        formatter.draw_line("Multiple choices:")
        formatter.blankline
        formatter.wrap(entries.map{|x| x.full_name}.join(", "))
      end
    end
  rescue RiError
    return nil
  end

  def args(keyword, options = {})
    options = DEFAULT_INFO_OPTIONS.merge(options)
    return nil if keyword.strip.empty?
    descriptor = NameDescriptor.new(keyword)
    entries = obtain_entries(descriptor, options)
    return nil if entries.empty? || RiIndex::ClassEntry === entries[0]

    params_text = ""
    entries.each do |entry|
      desc = @ri_reader.get_method(entry)
      params_text << capture_stdout(display(options)) do |display|
        display.full_params(desc)
      end
    end
    params_text
  rescue RiError
    return nil
  end

  # Returns a list with the names of the modules/classes that define the given
  # method, or +nil+.
  def class_list(keyword)
    _class_list(keyword, '\1')
  end
  
  # Returns a list with the names of the modules/classes that define the given
  # method, followed by a flag (#|::), or +nil+.
  # e.g. ["Array#", "IO#", "IO::", ... ]
  def class_list_with_flag(keyword)
    r = _class_list(keyword, '\1\2')
    r ? r.map{|x| x.gsub(/\./, "::")} : nil
  end

  # Return array of strings with the names of all known methods.
  def all_methods
    @ri_reader.full_method_names
  end

  # Return array of strings with the names of all known classes.
  def all_classes
    @ri_reader.full_class_names
  end

  private

  def obtain_unqualified_method_entries(name, separators, order)
    name = Regexp.escape(name)
    sep_re = "(" + separators.map{|x| Regexp.escape(x)}.join("|") + ")"
    matcher = MatchFinder.new do |m|
      m.add_matcher(:exact) do
        m.yield @ri_reader.methods_under_matching("", /#{sep_re}#{name}$/, true)
      end
      m.add_matcher(:exact_ci) do
        m.yield @ri_reader.methods_under_matching("", /#{sep_re}#{name}$/i, true)
      end
      m.add_matcher(:partial) do
        m.yield @ri_reader.methods_under_matching("", /#{sep_re}#{name}/, true)
      end
      m.add_matcher(:partial_ci) do
        m.yield @ri_reader.methods_under_matching("", /#{sep_re}#{name}/i, true)
      end
      m.add_matcher(:anywhere) do
        m.yield @ri_reader.methods_under_matching("", /#{sep_re}.*#{name}/, true)
      end
      m.add_matcher(:anywhere_ci) do
        m.yield @ri_reader.methods_under_matching("", /#{sep_re}.*#{name}/i, true)
      end
    end
    matcher.get_matches(order)
  end

  def obtain_qualified_method_entries(namespace, method, separators, order)
    namespace, unescaped_namespace = Regexp.escape(namespace), namespace
    method = Regexp.escape(method)
    matcher = MatchFinder.new do |m|
      m.add_matcher(:exact) do
        separators.each do |sep|
          m.yield @ri_reader.get_method_entry("#{namespace}#{sep}#{method}")
        end
      end
      sep_re = "(" + separators.map{|x| Regexp.escape(x)}.join("|") + ")"
      m.add_matcher(:exact_ci) do
        m.yield @ri_reader.methods_under_matching("", /^#{namespace}#{sep_re}#{method}$/i, true)
      end
      m.add_matcher(:nested) do
        m.yield @ri_reader.methods_under_matching("", /::#{namespace}#{sep_re}#{method}$/, true)
      end
      m.add_matcher(:nested_ci) do
        m.yield @ri_reader.methods_under_matching("", /::#{namespace}#{sep_re}#{method}$/i, true)
      end
      m.add_matcher(:partial) do
        m.yield @ri_reader.methods_under_matching(unescaped_namespace, /#{sep_re}#{method}/, false)
      end
      m.add_matcher(:partial_ci) do
        m.yield @ri_reader.methods_under_matching("", /^#{namespace}#{sep_re}#{method}/i, true)
      end
      m.add_matcher(:nested_partial) do
        m.yield @ri_reader.methods_under_matching("", /::#{namespace}#{sep_re}#{method}/, true)
      end
      m.add_matcher(:nested_partial_ci) do
        m.yield @ri_reader.methods_under_matching("", /::#{namespace}#{sep_re}#{method}/i, true)
      end
      m.add_matcher(:namespace_partial) do
        m.yield @ri_reader.methods_under_matching("", /^#{namespace}[^:]*#{sep_re}#{method}$/, true)
      end
      m.add_matcher(:namespace_partial_ci) do
        m.yield @ri_reader.methods_under_matching("", /^#{namespace}[^:]*#{sep_re}#{method}$/i, true)
      end
      m.add_matcher(:full_partial) do
        m.yield @ri_reader.methods_under_matching("", /^#{namespace}[^:]*#{sep_re}#{method}/, true)
      end
      m.add_matcher(:full_partial_ci) do
        m.yield @ri_reader.methods_under_matching("", /^#{namespace}[^:]*#{sep_re}#{method}/i, true)
      end
    end
    matcher.get_matches(order)
  end

  def obtain_namespace_entries(name, order)
    name = Regexp.escape(name)
    matcher = MatchFinder.new do |m|
      m.add_matcher(:exact){ m.yield @ri_reader.get_class_entry(name) }
      m.add_matcher(:exact_ci) do 
        m.yield @ri_reader.namespaces_under_matching("", /^#{name}$/i, true)
      end
      m.add_matcher(:nested) do 
        m.yield @ri_reader.namespaces_under_matching("", /::#{name}$/, true)
      end
      m.add_matcher(:nested_ci) do 
        m.yield @ri_reader.namespaces_under_matching("", /::#{name}$/i, true)
      end
      m.add_matcher(:partial) do
        m.yield @ri_reader.namespaces_under_matching("", /^#{name}/, true)
      end
      m.add_matcher(:partial_ci) do
        m.yield @ri_reader.namespaces_under_matching("", /^#{name}/i, true)
      end
      m.add_matcher(:nested_partial) do
        m.yield @ri_reader.namespaces_under_matching("", /::#{name}[^:]*$/, true)
      end
      m.add_matcher(:nested_partial_ci) do
        m.yield @ri_reader.namespaces_under_matching("", /::#{name}[^:]*$/i, true)
      end
    end
    matcher.get_matches(order)
  end

  def _class_list(keyword, rep)
    return nil if keyword.strip.empty?
    entries = @ri_reader.methods_under_matching("", /#{keyword}$/, true)
    return nil if entries.empty?

    entries.map{|entry| entry.full_name.sub(/(.*)(#|\.).*/, rep) }.uniq
  rescue RiError
    return nil
  end


  def separators(is_class_method)
    case is_class_method
    when true;  ["."]
    when false; ["#"]
    when nil;   [".","#"]
    end
  end

  DEFAULT_DISPLAY_OPTIONS = {
    :formatter => :ansi,
    :width     => 72,
  }
  def display(opt = {})
    opt = DEFAULT_DISPLAY_OPTIONS.merge(opt)
    options = Options.new
    options.use_stdout = true
    case opt[:formatter].to_sym
    when :ansi
      options.formatter = RedirectedAnsiFormatter
    else
      options.formatter = RedirectedTextFormatter
    end
    options.width = opt[:width]
    StringRedirectedDisplay.new(options)
  end

  def capture_stdout(display)
    yield display
    display.stringio.string
  end
end

end # module FastRI
