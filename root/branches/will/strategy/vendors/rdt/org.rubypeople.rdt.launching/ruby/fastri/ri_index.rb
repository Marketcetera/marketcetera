# Copyright (C) 2006  Mauricio Fernandez <mfp@acm.org>
#

require 'rdoc/ri/ri_cache'
require 'rdoc/ri/ri_reader'
require 'rdoc/ri/ri_descriptions'
require 'fastri/version'


# This is taken straight from 1.8.5's rdoc/ri/ri_descriptions.rb.
# Older releases have a buggy #merge_in that crashes when old.comment is nil.
if RUBY_RELEASE_DATE < "2006-06-15"
  module ::RI # :nodoc:
    class ModuleDescription # :nodoc:
      remove_method :merge_in
      # merge in another class desscription into this one
      def merge_in(old)
        merge(@class_methods, old.class_methods)
        merge(@instance_methods, old.instance_methods)
        merge(@attributes, old.attributes)
        merge(@constants, old.constants)
        merge(@includes, old.includes)
        if @comment.nil? || @comment.empty?
          @comment = old.comment
        else
          unless old.comment.nil? or old.comment.empty? then
            @comment << SM::Flow::RULE.new
            @comment.concat old.comment
          end
        end
      end
    end
  end
end


module FastRI

# This class provides the same functionality as RiReader, with some
# improvements:
# * lower memory consumption
# * ability to handle information from different sources separately.
#
# Some operations can be restricted to a given "scope", that is, a
# "RI DB directory". This allows you to e.g. look for all the instance methods
# in String defined by a package.
#
# Such operations take a +scope+ argument, which is either an integer which
# indexes the source in #paths, or a name identifying the source (either
# "system" or a package name).  If <tt>scope == nil</tt>, the information from
# all sources is merged.
class RiIndex 
  # Redefine RI::MethodEntry#full_name to use the following notation:
  # Namespace::Foo.singleton_method (instead of ::). RiIndex depends on this to
  # tell singleton methods apart.
  class ::RI::MethodEntry # :nodoc:
    remove_method :full_name
    def full_name
      res = @in_class.full_name
      unless res.empty?
        if @is_class_method
          res << "."
        else
          res << "#"
        end
      end
      res << @name
    end
  end

  class MethodEntry
    attr_reader :full_name, :name, :index, :source_index

    def initialize(ri_index, fullname, index, source_index)
      # index is the index in ri_index' array
      # source_index either nil (all scopes) or the integer referencing the
      # path (-> we'll do @ri_index.paths[@source_index])
      @ri_index = ri_index
      @full_name = fullname
      @name = fullname[/[.#](.*)$/, 1]
      @index = index
      @source_index = source_index
    end

    # Returns the "fully resolved" file name of the yaml containing our
    # description.
    def path_name
      prefix = @full_name.split(/::|[#.]/)[0..-2]
      case @source_index
      when nil
        ## we'd like to do
        #@ri_index.source_paths_for(self).map do |path|
        #  File.join(File.join(path, *prefix), RI::RiWriter.internal_to_external(@name))
        #end
        # but RI doesn't support merging at the method-level, so
        path = @ri_index.source_paths_for(self).first
        File.join(File.join(path, *prefix), 
                  RI::RiWriter.internal_to_external(@name) + 
                  (singleton_method? ? "-c" : "-i" ) + ".yaml")
      else
        path = @ri_index.paths[@source_index]
        File.join(File.join(path, *prefix), 
                  RI::RiWriter.internal_to_external(@name) +
                  (singleton_method? ? "-c" : "-i" ) + ".yaml")
      end
    end

    def singleton_method?
      /\.[^:]+$/ =~ @full_name
    end

    def instance_method?
      !singleton_method?
    end

    # Returns the type of this entry (<tt>:method</tt>).
    def type
      :method
    end
  end

  class ClassEntry
    attr_reader :full_name, :name, :index, :source_index

    def initialize(ri_index, fullname, index, source_index)
      @ri_index = ri_index
      @full_name = fullname
      @name = fullname.split(/::/).last
      @index = index
      @source_index = source_index
    end

    # Returns an array of directory names holding the cdesc-Classname.yaml
    # files.
    def path_names
      prefix = @full_name.split(/::/)
      case @source_index
      when nil
        @ri_index.source_paths_for(self).map{|path| File.join(path, *prefix) }
      else
        [File.join(@ri_index.paths[@source_index], *prefix)]
      end
    end

    # Returns nested classes and modules matching name (non-recursive).
    def contained_modules_matching(name)
      @ri_index.namespaces_under(self, false, @source_index).select do |x|
        x.name[name]
      end
    end

    # Returns all nested classes and modules (non-recursive).
    def classes_and_modules
      @ri_index.namespaces_under(self, false, @source_index)
    end

    # Returns nested class or module named exactly +name+ (non-recursive).
    def contained_class_named(name)
      contained_modules_matching(name).find{|x| x.name == name}
    end

    # Returns instance or singleton methods matching name (non-recursive).
    def methods_matching(name, is_class_method)
      @ri_index.methods_under(self, false, @source_index).select do |meth|
        meth.name[name] &&
          (is_class_method ? meth.singleton_method? : meth.instance_method?)
      end
    end

    # Returns instance or singleton methods matching name (recursive).
    def recursively_find_methods_matching(name, is_class_method)
      @ri_index.methods_under(self, true, @source_index).select do |meth|
        meth.name[name] && 
          (is_class_method ? meth.singleton_method? : meth.instance_method?)
      end
    end

    # Returns all methods, both instance and singleton (non-recursive).
    def all_method_names
      @ri_index.methods_under(self, false, @source_index).map{|meth| meth.full_name}
    end

    # Returns the type of this entry (<tt>:namespace</tt>).
    def type
      :namespace
    end
  end

  class TopLevelEntry < ClassEntry
    def methods_matching(name, is_class_method)
      recursively_find_methods_matching(name, is_class_method)
    end

    def module_named(name)

    end
  end

  attr_reader :paths

  class << self; private :new end

  def self.new_from_paths(paths = nil)
    obj = new
    obj.rebuild_index(paths)
    obj
  end

  def self.new_from_IO(anIO)
    obj = new
    obj.load(anIO)
    obj
  end

  def rebuild_index(paths = nil)
    @paths = paths || RI::Paths::PATH
    @gem_names = paths.map do |p|
      fullp = File.expand_path(p)
      gemname = nil
      begin
        require 'rubygems'
        Gem.path.each do |gempath|
          re = %r!^#{Regexp.escape(File.expand_path(gempath))}/doc/!
          if re =~ fullp
            gemname = fullp.gsub(re,"")[%r{^[^/]+}]
            break
          end
        end
      rescue LoadError
        # no RubyGems, no gems installed, skip it
      end
      gemname ? gemname : "system"
    end
    methods    = Hash.new{|h,k| h[k] = []}
    namespaces = methods.clone 
    @paths.each_with_index do |path, source_index|
      ri_reader = RI::RiReader.new(RI::RiCache.new(path))
      obtain_classes(ri_reader.top_level_namespace.first).each{|name| namespaces[name] << source_index }
      obtain_methods(ri_reader.top_level_namespace.first).each{|name| methods[name] << source_index }
    end
    @method_array = methods.sort_by{|h,k| h}.map do |name, sources|
      "#{name} #{sources.map{|x| x.to_s}.join(' ')}"
    end
    @namespace_array = namespaces.sort_by{|h,k| h}.map do |name, sources|
      "#{name} #{sources.map{|x| x.to_s}.join(' ')}"
    end

=begin
    puts "@method_array: #{@method_array.size}"
    puts "@namespace_array: #{@namespace_array.size}"
    puts @method_array.inject(0){|s,x| s + x.size}
    puts @namespace_array.inject(0){|s,x| s + x.size}
=end
  end

  MAGIC = "FastRI index #{FASTRI_INDEX_FORMAT}"
  # Load the index from the given IO.
  # It must contain a textual representation generated by #dump.
  def load(anIO)
    header = anIO.gets    
    raise "Invalid format." unless header.chomp == MAGIC
    anIO.gets  # discard "Sources:"
    paths     = []
    gem_names = []
    until (line = anIO.gets).index("=" * 80) == 0
      gemname, path = line.strip.split(/\s+/)
      paths     << path
      gem_names << gemname
    end
    anIO.gets # discard "Namespaces:"
    namespace_array = []
    until (line = anIO.gets).index("=" * 80) == 0
      namespace_array << line
    end
    anIO.gets # discard "Methods:"
    method_array = []
    until (line = anIO.gets).index("=" * 80) == 0
      method_array << line
    end
    @paths           = paths
    @gem_names       = gem_names
    @namespace_array = namespace_array
    @method_array    = method_array
  end

  # Serializes index to the given IO.
  def dump(anIO)
    anIO.puts MAGIC
    anIO.puts "Sources:"
    @paths.zip(@gem_names).each{|p,g| anIO.puts "%-30s  %s" % [g, p]}
    anIO.puts "=" * 80
    anIO.puts "Namespaces:"
    anIO.puts @namespace_array
    anIO.puts "=" * 80
    anIO.puts "Methods:"
    anIO.puts @method_array
    anIO.puts "=" * 80
  end
#{{{ RiReader compatibility interface

  # Returns an array with the top level namespace.
  def top_level_namespace(scope = nil)
    [TopLevelEntry.new(self, "", -1, scope ? scope_to_sindex(scope) : nil)]
  end

  # Returns an array of ClassEntry objects whose names match +target+, and
  # which correspond to the namespaces contained in +namespaces+.
  # +namespaces+ is an array of ClassEntry objects.
  def lookup_namespace_in(target, namespaces)
    result = []
    namespaces.each do |ns|
      result.concat(ns.contained_modules_matching(target))
    end
    result
  end

  # Returns the ClassDescription associated to the given +full_name+.
  def find_class_by_name(full_name, scope = nil)
    entry = get_entry(@namespace_array, full_name, ClassEntry, scope)
    return nil unless entry && entry.full_name == full_name
    get_class(entry)
  end
  
  # Returns the MethodDescription associated to the given +full_name+.
  # Only the first definition is returned when <tt>scope = nil</tt>.
  def find_method_by_name(full_name, scope = nil)
    entry = get_entry(@method_array, full_name, MethodEntry, scope)
    return nil unless entry && entry.full_name == full_name
    get_method(entry)
  end

  # Returns an array of MethodEntry objects, corresponding to the methods in
  # the ClassEntry objects in the +namespaces+ array.
  def find_methods(name, is_class_method, namespaces)
    result = []
    namespaces.each do |ns|
      result.concat ns.methods_matching(name, is_class_method)
    end
    result
  end

  # Return the MethodDescription for a given MethodEntry
  # by deserializing the YAML.
  def get_method(method_entry)
    path = method_entry.path_name
    File.open(path) { |f| RI::Description.deserialize(f) }
  end

  # Return a ClassDescription for a given ClassEntry.
  def get_class(class_entry)
    result = nil
    for path in class_entry.path_names
      path = RI::RiWriter.class_desc_path(path, class_entry)
      desc = File.open(path) {|f| RI::Description.deserialize(f) }
      if result
        result.merge_in(desc)
      else
        result = desc
      end
    end
    result
  end

  # Return the names of all classes and modules.
  def full_class_names(scope = nil)
    all_entries(@namespace_array, scope)
  end

  # Return the names of all methods.
  def full_method_names(scope = nil)
    all_entries(@method_array, scope)
  end

  # Return a list of all classes, modules, and methods.
  def all_names(scope = nil)
    full_class_names(scope).concat(full_method_names(scope))
  end

#{{{ New (faster) interface

  # Returns the number of methods in the index.
  def num_methods
    @method_array.size
  end

  # Returns the number of namespaces in the index.
  def num_namespaces
    @namespace_array.size
  end

  # Returns the ClassEntry associated to the given +full_name+.
  def get_class_entry(full_name, scope = nil)
    entry = get_entry(@namespace_array, full_name, ClassEntry, scope)
    return nil unless entry && entry.full_name == full_name
    entry
  end
  
  # Returns the MethodEntry associated to the given +full_name+.
  def get_method_entry(full_name, scope = nil)
    entry = get_entry(@method_array, full_name, MethodEntry, scope)
    return nil unless entry && entry.full_name == full_name
    entry
  end

  # Returns array of ClassEntry objects under class_entry_or_name
  # (either String or ClassEntry) in the hierarchy.
  def namespaces_under(class_entry_or_name, recursive, scope = nil)
    namespaces_under_matching(class_entry_or_name, //, recursive, scope)
  end

  # Returns array of ClassEntry objects under class_entry_or_name (either
  # String or ClassEntry) in the hierarchy whose +full_name+ matches the given
  # regexp.
  def namespaces_under_matching(class_entry_or_name, regexp, recursive, scope = nil)
    case class_entry_or_name
    when ClassEntry
      class_entry = class_entry_or_name
    when ""
      class_entry = top_level_namespace(scope)[0]
    else
      class_entry = get_entry(@namespace_array, class_entry_or_name, ClassEntry, scope)
    end
    return [] unless class_entry
    ret = []
    re1, re2 = matching_regexps_namespace(class_entry.full_name)
    (class_entry.index+1...@namespace_array.size).each do |i|
      entry = @namespace_array[i]
      break unless re1 =~ entry
      next if !recursive && re2 !~ entry 
      full_name = entry[/\S+/]
      next unless regexp =~ full_name
      if scope
        sources = namespace_sources(i)
        if sources.include?(sindex = scope_to_sindex(scope))
          ret << ClassEntry.new(self, full_name, i, sindex)
        end
      else
        ret << ClassEntry.new(self, full_name, i, nil)
      end
    end
    ret
  end

  # Returns array of MethodEntry objects under class_entry_or_name
  # (either String or ClassEntry) in the hierarchy.
  def methods_under(class_entry_or_name, recursive, scope = nil)
    methods_under_matching(class_entry_or_name, //, recursive, scope)
  end

  # Returns array of MethodEntry objects under class_entry_or_name (either
  # String or ClassEntry) in the hierarchy whose +full_name+ matches the given
  # regexp.
  def methods_under_matching(class_entry_or_name, regexp, recursive, scope = nil)
    case class_entry_or_name
    when ClassEntry
      full_name = class_entry_or_name.full_name
    else
      full_name = class_entry_or_name
    end
    method_entry = get_entry(@method_array, full_name, MethodEntry)
    return [] unless method_entry
    ret = []
    re1, re2 = matching_regexps_method(full_name)
    (method_entry.index...@method_array.size).each do |i|
      entry = @method_array[i]
      break unless re1 =~ entry
      next if !recursive && re2 !~ entry 
      full_name = entry[/\S+/]
      next unless regexp =~ full_name
      if scope
        sources = method_sources(i)
        if sources.include?(sindex = scope_to_sindex(scope))
          ret << MethodEntry.new(self, full_name, i, sindex)
        end
      else
        ret << MethodEntry.new(self, full_name, i, nil)
      end
    end
    ret
  end

  # Returns array of Strings corresponding to the base directories of all the
  # sources fo the given entry_or_name.
  def source_paths_for(entry_or_name)
    case entry_or_name
    when ClassEntry
      namespace_sources(entry_or_name.index).map{|i| @paths[i] }
    when MethodEntry
      method_sources(entry_or_name.index).map{|i| @paths[i]}
    when nil
      []
    else
      case entry_or_name
      when /[#.]\S+/
        method_entry = get_entry(@method_array, entry_or_name, MethodEntry, nil)
        source_paths_for(method_entry)
      when ""
        []
      else
        class_entry = get_entry(@namespace_array, entry_or_name, ClassEntry, nil)
        source_paths_for(class_entry)
      end
    end
  end

  private
  def namespace_sources(index)
    @namespace_array[index][/\S+ (.*)/,1].split(/\s+/).map{|x| x.to_i}
  end

  def method_sources(index)
    @method_array[index][/\S+ (.*)/,1].split(/\s+/).map{|x| x.to_i}
  end

  def all_entries(array, scope)
    if scope
      wanted_sidx = scope_to_sindex(scope)
      chosen = array.select{|x| x[/ (.*$)/, 1].split(/\s+/).map{|x| x.to_i}.include? wanted_sidx }
    else
      chosen = array
    end
    chosen.map{|x| x[/(\S+)/]}
  end

  def matching_regexps_namespace(prefix)
    if prefix.empty?
      [//, /^[^:]+ /]
    else
      [/^#{Regexp.escape(prefix)}/, /^#{Regexp.escape(prefix)}(::|[#.])[^:]+ / ]
    end
  end

  def matching_regexps_method(prefix)
    if prefix.empty?
      [//, /^[#.] /]  # the second should never match
    else
      [/^#{Regexp.escape(prefix)}([#.]|::)/, /^#{Regexp.escape(prefix)}([#.])\S+ / ]
    end
  end

  def scope_to_sindex(scope)
    case scope
    when Integer
      scope
    else
      @gem_names.index(scope)
    end
  end

  def get_entry(array, fullname, klass, scope = nil)
    index = binary_search(array, fullname)
    return nil unless index
    entry = array[index]
    sources = entry[/\S+ (.*)/,1].split(/\s+/).map{|x| x.to_i}
    if scope
      wanted_sidx = scope_to_sindex(scope)
      return nil unless wanted_sidx
      return nil unless sources.include?(wanted_sidx)
      return klass.new(self, entry[/\S+/], index, wanted_sidx)
    end
    klass.new(self, entry[/\S+/], index, nil)
  end

  def binary_search(array, name, from = 0, to = array.size - 1)
    middle = (from + to) / 2
    pivot = array[middle][/\S+/]
    if from == to
      if pivot.index(name) == 0
        from
      else
        nil
      end
    elsif name <= pivot
      binary_search(array, name, from, middle)
    elsif name > pivot
      binary_search(array, name, middle+1, to)
    end
  end
    
  def obtain_classes(namespace, res = [])
    subnamespaces = namespace.classes_and_modules
    subnamespaces.each do |ns|
      res << ns.full_name
      obtain_classes(ns, res)
    end
    res
  end

  def obtain_methods(namespace, res = [])
    subnamespaces = namespace.classes_and_modules
    subnamespaces.each do |ns|
      res.concat ns.all_method_names
      obtain_methods(ns, res)
    end
    res
  end
end

end #module FastRI

# vi: set sw=2 expandtab:
