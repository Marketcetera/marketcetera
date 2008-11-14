# Copyright (C) 2006  Mauricio Fernandez <mfp@acm.org>

# emulate rubygems.rb and define Gem.path if not loaded
# This is much faster than requiring rubygems.rb, which loads way too much
# stuff.
unless defined? ::Gem
  require 'rbconfig'
  module Gem
    def self.path
      ENV['GEM_HOME'] || default_dir
    end
    def self.default_dir
      if defined? RUBY_FRAMEWORK_VERSION
        return File.join(File.dirname(Config::CONFIG["sitedir"]), "Gems")
      else
        File.join(Config::CONFIG['libdir'], 'ruby', 'gems', Config::CONFIG['ruby_version'])
      end
    end
  end
end
# don't let rdoc/ri/ri_paths load rubygems.rb, that takes ~100ms !
emulation = $".all?{|x| /rubygems\.rb$/ !~ x} # 1.9 compatibility
$".unshift "rubygems.rb" if emulation
require 'rdoc/ri/ri_paths'
$".delete "rubygems.rb" if emulation
require 'rdoc/ri/ri_writer'

module FastRI
module Util
  # Return an array of <tt>[name, version, path]</tt> arrays corresponding to
  # the last version of each installed gem. +path+ is the base path of the RI
  # documentation from the gem. If the version cannot be determined, it will
  # be +nil+, and the corresponding gem might be repeated in the output array
  # (once per version).
  def gem_directories_unique
    return [] unless defined? Gem
    gemdirs = Dir["#{Gem.path}/doc/*/ri"]
    gems = Hash.new{|h,k| h[k] = []}
    gemdirs.each do |path|
      gemname, version = %r{/([^/]+)-(.*)/ri$}.match(path).captures
      if gemname.nil? # doesn't follow any conventions :(
        gems[path[%r{/([^/]+)/ri$}, 1]] << [nil, path]
      else
        gems[gemname] << [version, path]
      end
    end
    gems.sort_by{|name, _| name}.map do |name, versions|
      version, path = versions.sort.last
      [name, version, File.expand_path(path)]
    end
  end
  module_function :gem_directories_unique

  # Return the <tt>[name, version, path]</tt> array for the gem owning the RI
  # information stored in +path+, or +nil+.
  def gem_info_for_path(path, gem_dir_info = FastRI::Util.gem_directories_unique)
    path = File.expand_path(path)
    matches = gem_dir_info.select{|name, version, gem_path| path.index(gem_path) == 0}
    matches.sort_by{|name, version, gem_path| [gem_path.size, version, name]}.last
  end
  module_function :gem_info_for_path

  # Return the +full_name+ (in ClassEntry or MethodEntry's sense) given a path
  # to a .yaml file relative to a "base RI DB path".
  def gem_relpath_to_full_name(relpath)
    case relpath
    when %r{^(.*)/cdesc-([^/]*)\.yaml$}
      path, name = $~.captures
      (path.split(%r{/})[0..-2] << name).join("::")
    when %r{^(.*)/([^/]*)-(i|c)\.yaml$}
      path, escaped_name, type = $~.captures
      name = RI::RiWriter.external_to_internal(escaped_name)
      sep = ( type == 'c' ) ? "." : "#"
      path.gsub("/", "::") + sep + name
    end
  end
  module_function :gem_relpath_to_full_name
  
  # Returns the home directory (win32-aware).
  def find_home
    # stolen from RubyGems
    ['HOME', 'USERPROFILE'].each do |homekey|
      return ENV[homekey] if ENV[homekey]
    end
    if ENV['HOMEDRIVE'] && ENV['HOMEPATH']
      return "#{ENV['HOMEDRIVE']}:#{ENV['HOMEPATH']}"
    end
    begin
      File.expand_path("~")
    rescue StandardError => ex
      if File::ALT_SEPARATOR
        "C:/"
      else
        "/"
      end
    end
  end
  module_function :find_home

  def change_query_method_type(query)
    if md = /\A(.*)(#|\.|::)([^#.:]+)\z/.match(query)
      namespace, sep, meth = md.captures
      case sep
      when /::/ then "#{namespace}##{meth}"
      when /#/ then "#{namespace}::#{meth}"
      else 
        query
      end
    else
      query
    end
  end
  module_function :change_query_method_type


  module MagicHelp
    def help_method_extract(m) # :nodoc:
      unless m.inspect =~ %r[\A#<(?:Unbound)?Method: (.*?)>\Z]
        raise "Cannot parse result of #{m.class}#inspect: #{m.inspect}"
      end
      $1.sub(/\A.*?\((.*?)\)(.*)\Z/){ "#{$1}#{$2}" }.sub(/\./, "::").sub(/#<Class:(.*?)>#/) { "#{$1}::" }
    end

    def magic_help(query)
      if query =~ /\A(.*?)(#|::|\.)([^:#.]+)\Z/
        c, k, m = $1, $2, $3
        mid = m
        begin
          c = c.split(/::/).inject(Object){|s,x| s.const_get(x)}
          m = case k
              when "#"
                c.instance_method(m)
              when "::"
                c.method(m)
              when "."
                begin
                  # if it's a private_instance_method, assume it was created
                  # with module_function
                  if c.private_instance_methods.include?(m)
                    c.instance_method(m)
                  else
                    c.method(m)
                  end
                rescue NameError
                  c.instance_method(m)
                end
              end

          ret = help_method_extract(m)
          if ret == 'Class#new' and
              c.private_method_defined?(:initialize)
            return c.name + "::new"
          elsif ret =~ /^Kernel#/ and
              Kernel.instance_methods(false).include? mid
            return "Object##{mid}"
          end
          ret
        rescue Exception
          query
        end
      else
        query
      end
    end
  end


end # module Util
end # module FastRI
