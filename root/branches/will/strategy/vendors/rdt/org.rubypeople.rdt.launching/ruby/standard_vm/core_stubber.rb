OUTPUT_PATH = ARGV.first + "/"


def file_name(klass)
  file_name = OUTPUT_PATH + klass.to_s.downcase
  file_name.gsub!("::", "/")
  file_name << ".rb"
  return file_name
end

def dir_names(file_name)
  last_slash = file_name.rindex("/")
  return nil if last_slash.nil?
  file_name[0...last_slash]
end

def print_method(f, klass, method, method_name, singleton = false)
  begin
    ri_query_string = "#{klass}#{singleton ? '::' : '#' }#{method_name}"
    str = RI_DRIVER.get_info_for(ri_query_string)
    RI_DRIVER.display.reset
    if !str.nil?
      str.each_line {|line| f << "  # #{line}" }   
    else
      puts "Got no docs for #{klass}.#{method_name}"
    end
  rescue Exception => e
    puts "Trouble getting method #{klass}.#{method_name}'s docs: #{e}"
  end
  f << "  def "
  f << "self." if singleton
  f << method_name.to_s
  if !method.nil? and method.arity != 0
    # TODO We need to handle methods that take blocks!
    f << "("
    if method.arity < 0
      args = []
       (method.arity.abs + 1).times {|i| args << "arg#{i}" }
      args << "*rest"
      f << args.join(", ")    
    else
      args = []
      method.arity.times {|i| args << "arg#{i}" }
      f << args.join(", ")
    end
    f << ")"
  end
  f << "\n  end\n\n"
end

require 'FileUtils'
require 'rdoc/ri/ri_driver'
require 'rdoc/ri/ri_display'
require 'rdoc/ri/ri_formatter'
class RiDriver
  def display
    @display
  end
end

module RI
  class StubberFormatter < TextFormatter
    def initialize(options, indent)
      super
      @str = ""
    end
    
    def puts(*args)
      if args.empty?
        @str << "\n"
      else
        args.each {|arg| @str << "#{arg}\n" }
      end
      @str
    end
    
    def print(*args)
      args.each {|arg| @str << arg }     
      @str
    end
    
    def get_contents
      @str
    end
    
    def reset
      @str = ""
    end
  end
end
class StubberRiDisplay
  include RiDisplay
  
  def initialize(options)
    @options = options
    @formatter = RI::StubberFormatter.new(@options, "     ")
  end    
  
  def reset
    @formatter.reset
  end
  
  ######################################################################
  
    def display_usage
#    page do
      RI::Options::OptionList.usage(short_form=true)
#    end
  end


  ######################################################################
  
  def display_method_info(method)
#    page do
      @formatter.draw_line(method.full_name)
      display_params(method)
      @formatter.draw_line
      display_flow(method.comment)
      if method.aliases && !method.aliases.empty?
        @formatter.blankline
        aka = "(also known as "
        aka << method.aliases.map {|a| a.name }.join(", ") 
        aka << ")"
        @formatter.wrap(aka)
    end
    @formatter.get_contents
#    end
  end
  
  ######################################################################
  
  def display_class_info(klass, ri_reader)
#    page do 
      superclass = klass.superclass_string
      
      if superclass
        superclass = " < " + superclass
      else
        superclass = ""
      end
      
      @formatter.draw_line(klass.display_name + ": " +
                           klass.full_name + superclass)
      
      display_flow(klass.comment)
      @formatter.draw_line 
    
      unless klass.includes.empty?
        @formatter.blankline
        @formatter.display_heading("Includes:", 2, "")
        incs = []
        klass.includes.each do |inc|
          inc_desc = ri_reader.find_class_by_name(inc.name)
          if inc_desc
            str = inc.name + "("
            str << inc_desc.instance_methods.map{|m| m.name}.join(", ")
            str << ")"
            incs << str
          else
            incs << inc.name
          end
      end
        @formatter.wrap(incs.sort.join(', '))
      end
      
      unless klass.constants.empty?
        @formatter.blankline
        @formatter.display_heading("Constants:", 2, "")
        len = 0
        klass.constants.each { |c| len = c.name.length if c.name.length > len }
        len += 2
        klass.constants.each do |c|
          @formatter.wrap(c.value, 
                          @formatter.indent+((c.name+":").ljust(len)))
        end 
      end
      
      unless klass.class_methods.empty?
        @formatter.blankline
        @formatter.display_heading("Class methods:", 2, "")
        @formatter.wrap(klass.class_methods.map{|m| m.name}.sort.join(', '))
      end
      
      unless klass.instance_methods.empty?
        @formatter.blankline
        @formatter.display_heading("Instance methods:", 2, "")
        @formatter.wrap(klass.instance_methods.map{|m| m.name}.sort.join(', '))
      end
      
      unless klass.attributes.empty?
        @formatter.blankline
        @formatter.wrap("Attributes:", "")
        @formatter.wrap(klass.attributes.map{|a| a.name}.sort.join(', '))
      end
#    end
    @formatter.get_contents
  end
  
  ######################################################################
  
  # Display a list of method names
  
  def display_method_list(methods)
    nil
  end
  
  ######################################################################
  
  def display_class_list(namespaces)
    nil
  end

######################################################################
  
  def display_params(method)

    params = method.params

    if params[0,1] == "("
      if method.is_singleton
        params = method.full_name + params
      else
        params = method.name + params
      end
    end
    params.split(/\n/).each do |p|
      @formatter.wrap(p) 
      @formatter.break_to_newline
    end
    @formatter.get_contents
  end
  ######################################################################
  
  def display_flow(flow)
    if !flow || flow.empty?
      @formatter.wrap("(no description...)")
    else
      @formatter.display_flow(flow)
    end
    @formatter.get_contents
  end
end

RI_DRIVER = RiDriver.new
@klasses = Module.constants.select {|c| ["Class", "Module"].include?(eval("#{c}.class").to_s) }
@klasses = @klasses.collect {|k| eval("#{k}")}
@klasses = @klasses.uniq.sort_by {|klass| klass.to_s }
@klasses.each do |klass|
  next if klass.to_s[0].chr == "f" # TODO Skip if first char is lowercase
  next if klass.to_s == "JavaPackageModuleTemplate"
  file_name = file_name(klass)
  dirs = dir_names(file_name)
  FileUtils.mkdir_p(dirs) if !dirs.nil? and !File.exist?(file_name)
  open(file_name, 'w') do |f|
    # Spit out class RI docs
    begin
      class_docs = RI_DRIVER.get_info_for(klass.to_s)
      f << "=begin\n#{class_docs}\n=end\n" if class_docs
      RI_DRIVER.display.reset
    rescue Exception => e
      # ignore
      puts "Trouble getting type's docs: #{e}"
    end
    f << "#{klass.class.to_s.downcase} #{klass}"
    f << " < #{klass.superclass.to_s }" if klass.respond_to?(:superclass) and !klass.superclass.nil?
    f << "\n"
    puts "Type: #{klass}"
    puts "Included Modules: #{klass.included_modules}"
    klass.included_modules.each {|mod| puts mod.to_s; f << "  include #{mod.to_s}\n" unless mod.to_s == "Kernel" && klass.to_s != "Object"}
    f << "\n"

    # FIXME We aren't grabbing some important methods inside Module (like "include")
    klass.methods(false).each do |method_name|
      method = eval("#{klass}").method(method_name) rescue nil    
      print_method(f, klass, method, method_name.to_s, true)
    end
    # TODO Fix it so we can get a hold of the module instance methods properly
    klass.instance_methods(false).each do |method_name|
      begin
        obj = nil
        if klass.class.to_s == "Module"
          obj = Object.new
          obj.extend(klass)
        elsif klass.to_s == "Symbol" 
          obj = :symbol
        elsif klass.to_s == "Integer" 
          obj = 1
        elsif klass.to_s == "Bignum" 
          obj = 1
        elsif klass.to_s == "MatchData" 
          obj =  /(.)(.)(.)/.match("abc")
        elsif klass.to_s == "Fixnum" 
          obj = 1
        elsif klass.to_s == "Float" 
          obj = 1.0
        elsif klass.to_s == "TrueClass" 
          obj = true
        elsif klass.to_s == "FalseClass" 
          obj = false
        elsif klass.to_s == "NilClass" 
          obj = nil
        elsif klass.to_s == "CGI"
          ENV["REQUEST_METHOD"] = "GET"
          obj = klass.new
        else
          obj = klass.new
        end
        method = obj.method(method_name.to_s)
      rescue StandardError => e
        puts "Couldn't grab method #{method_name.to_s}: #{e}"
        # TODO If we can't create an instance of a class, generate dynamic subclass where we can, and then
        # grab methods from there
        begin
          # If we're a module, we may need to force the function to be more visible to grab it
          unless obj.nil? 
            obj.module_eval do
              module_function(method_name.to_s)
            end
            method = obj.method(method_name.to_s)      
          end
        rescue StandardError => e
          puts e
          method = nil
        end
      end
      print_method(f, klass, method, method_name.to_s)
    end
    f << "end\n"
  end
end