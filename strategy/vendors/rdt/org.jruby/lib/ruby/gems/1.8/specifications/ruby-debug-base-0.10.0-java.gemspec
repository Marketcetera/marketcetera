Gem::Specification.new do |s|
  s.name = %q{ruby-debug-base}
  s.version = "0.10.0"
  s.platform = %q{java}

  s.specification_version = 2 if s.respond_to? :specification_version=

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["debug-commons team"]
  s.date = %q{2008-02-04}
  s.description = %q{Java extension to make fast ruby debugger run on JRuby. It is the same what ruby-debug-base is for native Ruby.}
  s.files = ["Rakefile", "README", "lib/ruby_debug_base.jar", "lib/ruby-debug-base.rb"]
  s.has_rdoc = true
  s.homepage = %q{http://rubyforge.org/projects/debug-commons/}
  s.require_paths = ["lib"]
  s.rubyforge_project = %q{debug-commons}
  s.rubygems_version = %q{1.0.1}
  s.summary = %q{Java implementation of Fast Ruby Debugger}
end
