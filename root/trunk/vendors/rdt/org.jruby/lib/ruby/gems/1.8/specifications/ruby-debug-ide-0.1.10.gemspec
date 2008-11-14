Gem::Specification.new do |s|
  s.name = %q{ruby-debug-ide}
  s.version = "0.1.10"

  s.specification_version = 2 if s.respond_to? :specification_version=

  s.required_rubygems_version = Gem::Requirement.new(">= 0") if s.respond_to? :required_rubygems_version=
  s.authors = ["Markus Barchfeld, Martin Krauskopf"]
  s.autorequire = %q{ruby-debug-base}
  s.date = %q{2008-02-04}
  s.default_executable = %q{rdebug-ide}
  s.description = %q{An interface which glues ruby-debug to IDEs like Eclipse (RDT) and NetBeans.}
  s.email = %q{rubyeclipse-dev-list@sourceforge.net}
  s.executables = ["rdebug-ide"]
  s.files = ["README", "bin/rdebug-ide", "lib/ruby-debug", "lib/ruby-debug/xml_printer.rb", "lib/ruby-debug/command.rb", "lib/ruby-debug/processor.rb", "lib/ruby-debug/commands", "lib/ruby-debug/commands/load.rb", "lib/ruby-debug/commands/breakpoints.rb", "lib/ruby-debug/commands/variables.rb", "lib/ruby-debug/commands/control.rb", "lib/ruby-debug/commands/threads.rb", "lib/ruby-debug/commands/stepping.rb", "lib/ruby-debug/commands/eval.rb", "lib/ruby-debug/commands/catchpoint.rb", "lib/ruby-debug/commands/frame.rb", "lib/ruby-debug/commands/inspect.rb", "lib/ruby-debug/interface.rb", "lib/ruby-debug/printers.rb", "lib/ruby-debug/helper.rb", "lib/ruby-debug/event_processor.rb", "lib/ruby-debug.rb"]
  s.homepage = %q{http://rubyforge.org/projects/debug-commons/}
  s.require_paths = ["lib"]
  s.required_ruby_version = Gem::Requirement.new(">= 1.8.2")
  s.rubyforge_project = %q{debug-commons}
  s.rubygems_version = %q{1.0.1}
  s.summary = %q{IDE interface for ruby-debug.}

  s.add_dependency(%q<ruby-debug-base>, ["= 0.10.0"])
end
